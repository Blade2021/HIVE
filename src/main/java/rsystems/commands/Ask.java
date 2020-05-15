package rsystems.commands;

/*
    @author: Blade2021
    @description: Pull links from designated channel, Sanitize links from extra words/characters.  Then post to target channel
*/


import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import rsystems.HiveBot;
import rsystems.handlers.DataFile;

import java.util.ArrayList;
import java.util.List;

import static rsystems.handlers.DataFile.datafileData;

public class Ask extends ListenerAdapter {
    DataFile dataFile = HiveBot.dataFile;
    String pullChannel = dataFile.getDatafileData().get("QuestionPullChannel").toString();
    String pushChannel = dataFile.getDatafileData().get("QuestionPushChannel").toString();

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        TextChannel textChannel = event.getGuild().getTextChannelById(pushChannel);

        if(event.getChannel().getId().equals(pullChannel)){

            if(event.getAuthor().isBot()){
                // Allow only restream bot to continue
                if(!event.getMember().getId().equals(HiveBot.restreamID)){
                    return;  // Exit
                }
            }
            if ((event.getMessage().getContentRaw().contains(HiveBot.prefix + "ask"))) {
                // Assign message to local variable
                String messageraw = event.getMessage().getContentRaw();

                // Call method to get link
                String question = getQuestion(messageraw);

                if(question.length() <= 5){
                    //Link was not long enough to verify
                    return;
                }

                // Call method to get author
                String author = getAuthor(event,messageraw);


                try{
                    //Get history of the past 20 messages
                    List<Message> messages = textChannel.getHistory().retrievePast(20).complete();

                    for(Message m:messages){
                        if(m.getContentRaw().contains(question)){
                            event.getMessage().addReaction("⚠").queue();
                            return;
                        }
                    }
                    //If current link was not found in messages
                    textChannel.sendMessage(author + question).queue();
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
                author = author + " : ";
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
    /*
    public void cleanUp(Guild guild){
        ArrayList<Long> markers = new ArrayList<>();
        List<Message> messages = guild.getTextChannelById("id").getHistory().retrievePast(99).complete();
        for(Message m:messages){
            m.getContentRaw().contains("STREAM MARKER");
            markers.add(m.getIdLong());
        }

        if(markers.size() >= 3){
            List<Message> rmMessages = guild.getTextChannelById("id").getHistoryBefore(markers.get(1),99).complete().getRetrievedHistory();
            guild.getTextChannelById("id").purgeMessages(rmMessages);
        }
    }

    */
}
