package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.commands.adminCommands.Embed;

import javax.annotation.Nonnull;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AskCommand extends ListenerAdapter {
    String pullChannelID = Config.get("QuestionPullChannel");
    String pushChannelID = Config.get("QuestionPushChannel");

    /**
     * A list of posted question Message IDs
     */
    private static List<Long> keeperList = new ArrayList<>();

    /**
     * Restream's userID, for processing chat messages.
     */
    String restreamID = HiveBot.dataFile.getData("RestreamID").toString();

    /**
     * Checks if a message contained the ask command.  If it did, process it and post it into the questions channel.
     * @param event
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {


        TextChannel textChannel = event.getGuild().getTextChannelById(pushChannelID);

        if(event.getChannel().getId().equals(pullChannelID)){

            if(event.getAuthor().isBot()){
                // Allow only restream bot to continue
                if(!event.getMember().getId().equals(restreamID)){
                    return;  // Exit
                }
            }

            // Ask Command
            if(event.getMessage().getContentRaw().toLowerCase().contains(HiveBot.prefix+"ask ")){
                //System.out.println("debug 1");

                // Assign message to local variable
                String messageraw = event.getMessage().getContentRaw();

                // Call method to get link
                final String question = getQuestion(messageraw);

                if(question.length() <= 5){
                    //Link was not long enough to verify
                    System.out.println("Question was to short");
                    return;
                }

                // Call method to get author
                final String author = getAuthor(event,messageraw);


                try{
                    //Get history of the past 20 messages
                    MessageHistory history = event.getChannel().getHistoryBefore(event.getMessageId(),100).limit(100).complete();
                    List<Message> messages = history.getRetrievedHistory();

                    for(Message m:messages){
                        if(m.getContentRaw().contains(question)){
                            System.out.println(String.format("Question: %s\nFound Message: %s",question,m.getContentRaw()));
                            event.getMessage().addReaction("⚠").queue();
                            return;
                        }
                    }

                    String platform = getPlatform(event.getMessage());
                    if(platform == null){
                        platform = "Discord";
                    }


                    //If current question was not found in messages
                    EmbedBuilder questionBuilder = new EmbedBuilder();
                    questionBuilder.setTitle(String.format("Requester: %s  | Platform: %s",author,platform));
                    questionBuilder.addField("**Question**",question,false);

                    Random rand = new Random();
                    float r = rand.nextFloat();
                    float g = rand.nextFloat();
                    float b = rand.nextFloat();

                    Color randomColor = new Color(r,g,b);

                    questionBuilder.setColor(randomColor);

                    textChannel.sendMessage(questionBuilder.build()).queue(success -> {
                        keeperList.add(success.getIdLong());
                        success.addReaction("✅").queue();
                        success.addReaction("\u274C").queue();
                    });
                    questionBuilder.clear();
                    event.getMessage().addReaction("\uD83D\uDCE8").queue();
                }
                catch(InsufficientPermissionException e){
                    System.out.println("Error: Missing permission: " + e.getPermission().getName());
                }
                catch(NullPointerException e){
                    System.out.println("THE CHANNEL DISAPPEARED!");
                }
            }

        }
    }

    /**
     * This will return the platform that the question came from by processing the string.
     * @param message
     * @return Which platform the question originated from.  aka. Discord, YouTube, Twitch
     */
    private String getPlatform(Message message){
        String platform = null;
        if(message.getAuthor().isBot()){
            final String messageText = message.getContentDisplay();

            if((messageText.contains("[")) && (messageText.contains("]"))) {

                final int openBracketLocation = messageText.indexOf("[");
                final int colonLocation = messageText.indexOf(":");

                platform = messageText.substring(openBracketLocation+1,colonLocation);
            }
        }
        return platform;
    }

    /**
     * Parse out the question from the text provided.
     * @param message
     * @return The question the user provided.
     */
    private String getQuestion(String message){
        String question = "";
        try{
            int questionStart = message.indexOf(HiveBot.prefix + "ask");
            question = message.substring(questionStart+4);
        }
        catch(IndexOutOfBoundsException | NullPointerException e){
            System.out.println("did not find question");
        }
        return question;
    }

    /**
     * Grab the author of the question
     * @param event
     * @param message
     * @return The author of the question
     */
    private String getAuthor(GuildMessageReceivedEvent event, String message){

        // Initialize author
        String author = "";
        // Does message contain brackets?
        if((message.contains("[")) && (message.contains("]"))){
            try{
                // Get locations of brackets
                int openBracketLocation = message.indexOf("[");
                int closeBracketLocation = message.indexOf("]");
                // Grab author, and strip youtube and twitch from author
                author = message.substring(openBracketLocation+1,closeBracketLocation).replaceFirst("YouTube:","").replaceFirst("Twitch:","");
                //author = author + " : ";
            }
            catch (StringIndexOutOfBoundsException e){
                System.out.println("Could not find author");
            }
        } else {
            author = event.getMessage().getAuthor().getName();
            //author = author + " : ";
        }

        return author;
    }

    /**
     * Detect when a reaction was added to a matching question.  If the reaction came from an authorized user, process the question.  Otherwise remove the reaction.
     * @param event
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getUser().isBot()){
            return;
        }

        final Long messageID = event.getMessageIdLong();
        if(keeperList.contains(messageID)){

            try {
                if(HiveBot.dispatcher.checkAuthorized(event.getMember(),8)) {

                    if (event.getReaction().getReactionEmote().isEmoji()) {

                        final String reaction = event.getReaction().getReactionEmote().getEmoji();
                        final TextChannel pushChannel = HiveBot.mainGuild().getTextChannelById(pushChannelID);

                        if (reaction.equals("✅")) {
                            pushChannel.retrieveMessageById(messageID).queue(success -> {
                                if (!success.getEmbeds().isEmpty()) {
                                    MessageEmbed embed = success.getEmbeds().get(0);
                                    EmbedBuilder embedBuilder = new EmbedBuilder(embed);
                                    embedBuilder.setColor(Color.GREEN);
                                    embedBuilder.setFooter("ANSWERED | " + event.getMember().getEffectiveName(), event.getUser().getEffectiveAvatarUrl());

                                    success.editMessage(embedBuilder.build()).override(true).queue();

                                    //success.delete().queueAfter(60, TimeUnit.SECONDS);
                                    //keeperList.remove(messageID);
                                }
                            });
                        }

                        if (reaction.equals("\u274C")) {
                            pushChannel.retrieveMessageById(messageID).queue(success -> {

                                if (!success.getEmbeds().isEmpty()) {
                                    MessageEmbed embed = success.getEmbeds().get(0);
                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                    embedBuilder.setColor(Color.red);
                                    embedBuilder.setTitle(embed.getTitle());
                                    embedBuilder.setDescription("**Question Removed by Moderator/Admin**\n\nPlease review our regulations for proper question etiquette.");
                                    embedBuilder.setFooter("Removed by: " + event.getMember().getEffectiveName(), event.getUser().getEffectiveAvatarUrl());

                                    success.editMessage(embedBuilder.build()).override(true).queue();

                                    //success.delete().queueAfter(60, TimeUnit.SECONDS);
                                    //keeperList.remove(messageID);
                                }
                            });

                        }
                    }
                } else {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove the messageID from the arrayList if the message was deleted.
     * @param event
     */
    @Override
    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        if(keeperList.contains(event.getMessageIdLong())){
            keeperList.remove(event.getMessageIdLong());
        }
    }
}