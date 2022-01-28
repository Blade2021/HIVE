package rsystems.objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.tasks.BotActivity;

import java.awt.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StreamHandler extends ListenerAdapter {

    private boolean streamActive = false;
    private String streamTopic = null;

    private Long streamQuestionChannelID = Long.parseLong(Config.get("Stream_Question_Post_ChannelID"));
    private Long streamChatChannelID = Long.parseLong(Config.get("Stream_Chat_ChannelID"));
    private Long streamLinksPostChannelID = Long.parseLong(Config.get("Stream_Links_Post_ChannelID"));

    private boolean firstHereClaimed = false;

    private static List<Long> keeperList = new ArrayList<>();

    public StreamHandler() {
    }

    public boolean isStreamActive() {
        return streamActive;
    }

    public void setStreamActive(boolean streamActive){
        setStreamActive(streamActive,null);
    }

    public void setStreamActive(boolean streamActive, String streamTopic) {

        if(!streamActive){
            clearQuestions(this.streamQuestionChannelID);

            BotActivity.handleTask();
            firstHereClaimed = false;

        } else {
            if(!this.streamActive) {

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle(String.format("%s is going LIVE soon!",Config.get("HOST_NICKNAME")));
                builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
                builder.setDescription(String.format("Come one, come all!  Join us on the %s Stream!  Links below!\n**Remember to like and subscribe!**\n\nBe sure to type `/here` during a livestream to receive your bonus stream points!",Config.get("HOST_NICKNAME")));
                if(streamTopic != null) {
                    builder.addField("Topic", streamTopic, false);

                    this.streamTopic = streamTopic;
                }
                builder.addField("Twitch", Config.get("STREAM_TWITCH_LINK"), true);
                builder.addField("YouTube", Config.get("STREAM_YOUTUBE_LINK"), true);

                getChannel(streamChatChannelID).sendMessageEmbeds(builder.build()).queue();

                HiveBot.jda.getPresence().setActivity(Activity.streaming("Stream Mode Active", Config.get("STREAM_TWITCH_LINK")));
            }
        }

        this.streamActive = streamActive;
    }

    public String getStreamTopic() {
        return streamTopic;
    }

    public void setStreamTopic(String streamTopic) {
        this.streamTopic = streamTopic;
    }

    public Long getStreamChatChannelID() {
        return streamChatChannelID;
    }

    public Long getStreamQuestionChannelID() {
        return streamQuestionChannelID;
    }

    public Long getStreamLinksPostChannelID() {
        return streamLinksPostChannelID;
    }

    public TextChannel getChannel(final Long channelID){
        if(HiveBot.mainGuild().getTextChannelById(channelID) != null){
            return HiveBot.mainGuild().getTextChannelById(channelID);
        } else {
            return null;
        }
    }

    public void parseMessage(MessageReceivedEvent event){

        if(this.streamActive) {
            // Ask Command
            if(event.getMessage().getContentDisplay().toLowerCase().contains(HiveBot.prefix+"ask ")){

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
                    event.getChannel().getHistoryBefore(event.getMessageId(),100).limit(100).queue(messageHistory -> {

                        List<Message> messages = messageHistory.getRetrievedHistory();

                        for(Message m:messages){
                            if(m.getContentRaw().contains(question)){
                                System.out.println(String.format("Question: %s\nFound Message: %s",question,m.getContentRaw()));
                                event.getMessage().addReaction("⚠").queue();
                                return;
                            }
                        }

                        String platform = getPlatform(event.getMessage());
                        if(platform == null){
                            platform = "WHUT?";
                        }

                        //If current question was not found in messages
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle(String.format("Platform: %s",platform));
                        embedBuilder.addField("**Requester**",author,false);
                        embedBuilder.addField("**Question**",String.format("```%s```",question.trim()),false);
                        embedBuilder.setTimestamp(Instant.now());

                        Random rand = new Random();
                        float r = rand.nextFloat();
                        float g = rand.nextFloat();
                        float b = rand.nextFloat();

                        Color randomColor = new Color(r,g,b);
                        embedBuilder.setColor(randomColor);


                        TextChannel questionPushChannel = event.getGuild().getTextChannelById(this.streamQuestionChannelID);

                        if(questionPushChannel != null) {
                            questionPushChannel.sendMessageEmbeds(embedBuilder.build()).queue(success -> {
                                keeperList.add(success.getIdLong());
                                success.addReaction("✅").queue();
                                success.addReaction("\u274C").queue();
                            });
                            embedBuilder.clear();
                            event.getMessage().addReaction("\uD83D\uDCE8").queue();
                            return;
                        }

                    });
                }
                catch(InsufficientPermissionException e){
                    System.out.println("Error: Missing permission: " + e.getPermission().getName());
                }
                catch(NullPointerException e){
                    System.out.println("THE CHANNEL DISAPPEARED!");
                }
            }

            // LINK LISTENER
            if ((event.getMessage().getContentRaw().contains("http://")) || (event.getMessage().getContentRaw().contains("https://")) || (event.getMessage().getContentRaw().contains("www."))) {

                final String messageraw = event.getMessage().getContentRaw();
                final TextChannel pushChannel = HiveBot.mainGuild().getTextChannelById(streamLinksPostChannelID);

                // Call method to get link
                String link = getLink(messageraw);

                if (link.length() <= 5) {
                    //Link was not long enough to verify
                    return;
                }

                // Call method to get author
                String author = getAuthor(event, messageraw);
                if (pushChannel != null) {

                    List<Message> messages = pushChannel.getHistory().retrievePast(20).complete();

                    for (Message m : messages) {
                        if (m.getContentRaw().contains(link)) {
                            event.getMessage().addReaction("⚠").queue();
                            return;
                        }
                    }
                    //If current link was not found in messages
                    pushChannel.sendMessage(author + link).queue();
                    event.getMessage().addReaction("\uD83D\uDCE8").queue();
                    return;
                }
            }
        }
    }

    private String getLink(String message){
        String link = "";

        if(message.contains("http")){
            int linkStart = message.indexOf("http");
            try{
                // Space was found after link
                link = message.substring(linkStart,message.indexOf(" ",linkStart+1));
            }
            catch(StringIndexOutOfBoundsException e){
                // No space was found
                link = message.substring(linkStart);
            }
        } else {
            int linkStart = message.indexOf("www");
            try {
                // Space was found after link
                link = message.substring(linkStart, message.indexOf(" ", linkStart + 1));
            }
            catch(StringIndexOutOfBoundsException e){
                // No space was found
                link = message.substring(linkStart);
            }
        }
        return link;
    }

    private String getAuthor(MessageReceivedEvent event, String message){
        // Initialize author
        String author = "";
        // Does message contain brackets?
        if((message.contains("[")) && (message.contains("]"))){
            try{
                // Get locations of brackets
                int openBracketLocation = message.indexOf("[");
                int closeBracketLocation = message.indexOf("]");

                int colonLocation = message.indexOf(":");
                // Grab author, and strip youtube and twitch from author
                author = message.substring(colonLocation+1,closeBracketLocation);
                //author = author + " : ";
            }
            catch (StringIndexOutOfBoundsException e){
                System.out.println("Could not find author");
            }
        } else {
            author = event.getMessage().getAuthor().getName();
            author = author + " : ";
        }

        return author;
    }

    /**
     * This will return the platform that the question came from by processing the string.
     * @param message
     * @return Which platform the question originated from.  aka. Discord, YouTube, Twitch
     */
    private String getPlatform(Message message){
        String platform = null;
        //if(message.getAuthor().isBot()){
            final String messageText = message.getContentDisplay();

            if((messageText.contains("[")) && (messageText.contains("]"))) {

                final int openBracketLocation = messageText.indexOf("[");
                final int colonLocation = messageText.indexOf(":");

                platform = messageText.substring(openBracketLocation+1,colonLocation);
            }
        //}
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

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getUser().isBot()){
            return;
        }

        final Long messageID = event.getMessageIdLong();
        if(keeperList.contains(messageID)){

            try {

                if(HiveBot.dispatcher.checkAuthorized(event.getGuild().getIdLong(),event.getMember(),8,null)) {

                    if (event.getReaction().getReactionEmote().isEmoji()) {

                        final String reaction = event.getReaction().getReactionEmote().getEmoji();
                        final TextChannel pushChannel = HiveBot.mainGuild().getTextChannelById(this.streamQuestionChannelID);

                        if (reaction.equals("✅")) {
                            pushChannel.retrieveMessageById(messageID).queue(success -> {
                                if (!success.getEmbeds().isEmpty()) {
                                    MessageEmbed embed = success.getEmbeds().get(0);
                                    EmbedBuilder embedBuilder = new EmbedBuilder(embed);
                                    embedBuilder.setColor(Color.GREEN);
                                    embedBuilder.setFooter("ANSWERED | " + event.getMember().getEffectiveName(), event.getUser().getEffectiveAvatarUrl());

                                    success.editMessageEmbeds(embedBuilder.build()).override(true).queue();

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

                                    success.editMessageEmbeds(embedBuilder.build()).override(true).queue();

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
    public void onMessageDelete(MessageDeleteEvent event) {
        if(keeperList.contains(event.getMessageIdLong())){
            keeperList.remove(event.getMessageIdLong());
        }
    }

    private void clearQuestions(Long channelID){
        try {
            List<Message> messages = new ArrayList<>();
            TextChannel channel = HiveBot.mainGuild().getTextChannelById(channelID);
            if(channel != null) {
                channel.getIterableHistory()
                        .cache(false)
                        .forEachAsync(messages::add)
                        .thenRun(() -> channel.purgeMessages(messages));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isFirstHereClaimed() {
        return firstHereClaimed;
    }

    public void setFirstHereClaimed(boolean firstHereClaimed) {
        this.firstHereClaimed = firstHereClaimed;
    }
}
