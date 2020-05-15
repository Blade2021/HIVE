package rsystems.commands;

/*
    @author: Blade2021
    @description: Pull links from designated channel, Sanitize links from extra words/characters.  Then post to target channel
*/


import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.handlers.DataFile;

import java.util.List;

public class LinkGrabber extends ListenerAdapter {

    //DataFile dataFile = HiveBot.dataFile;
    String pullChannel = HiveBot.dataFile.getDatafileData().get("LinksPullChannel").toString();
    String pushChannel = HiveBot.dataFile.getDatafileData().get("LinksPushChannel").toString();

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        TextChannel textChannel = event.getGuild().getTextChannelById(pushChannel);
        if(event.getChannel().getId().equals(pullChannel)) {
            if (HiveBot.getStreamMode()) {
                if (event.getAuthor().isBot()) {
                    // Allow only restream bot to continue
                    if (!event.getMember().getId().equals(HiveBot.restreamID)) {
                        return;  // Exit
                    }
                }
                if ((event.getMessage().getContentRaw().contains("http://")) || (event.getMessage().getContentRaw().contains("https://")) || (event.getMessage().getContentRaw().contains("www."))) {
                    // Assign message to local variable
                    String messageraw = event.getMessage().getContentRaw();

                    // Call method to get link
                    String link = getLink(messageraw);

                    if (link.length() <= 5) {
                        //Link was not long enough to verify
                        return;
                    }

                    // Call method to get author
                    String author = getAuthor(event, messageraw);


                    try {
                        //Get history of the past 20 messages
                        List<Message> messages = textChannel.getHistory().retrievePast(20).complete();

                        for (Message m : messages) {
                            if (m.getContentRaw().contains(link)) {
                                event.getMessage().addReaction("⚠").queue();
                                return;
                            }
                        }
                        //If current link was not found in messages
                        textChannel.sendMessage(author + link).queue();
                    } catch (InsufficientPermissionException e) {
                        System.out.println("Error: Missing permission: " + e.getPermission().getName());
                    } catch (NullPointerException e) {
                        System.out.println("THE CHANNEL DISAPPEARED!");
                    }
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
}
