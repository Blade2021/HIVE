package rsystems.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LinkGrabber extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String pullChannel = "469343461150162955";
        String pushChannel = "698214886622232606";


        TextChannel textChannel = event.getGuild().getTextChannelById(pushChannel);

        if(event.getChannel().getId().equals(pullChannel)){
            if((event.getMessage().getMember().getId().equals("650410966130884629")) || (event.getMessage().getMember().getId().equals("83010416610906112")) || (event.getMessage().getMember().getId().equals("521305298770853888"))) {
                return;
            } else {
                if ((event.getMessage().getContentRaw().contains("http://")) || (event.getMessage().getContentRaw().contains("https://")) || (event.getMessage().getContentRaw().contains("www."))) {
                    // Assign message to local variable
                    String messageraw = event.getMessage().getContentRaw();
                    // Initialize link start index
                    String link = "";
                    if(messageraw.contains("http")){
                        int linkStart = messageraw.indexOf("http");
                        try{
                            // Space was found after link
                            link = messageraw.substring(linkStart,messageraw.indexOf(" ",linkStart+1));
                        }
                        catch(StringIndexOutOfBoundsException e){
                            // No space was found
                            link = messageraw.substring(linkStart);
                        }
                    } else {
                        int linkStart = messageraw.indexOf("www");
                        try {
                            // Space was found after link
                            link = messageraw.substring(linkStart, messageraw.indexOf(" ", linkStart + 1));
                        }
                        catch(StringIndexOutOfBoundsException e){
                            // No space was found
                            link = messageraw.substring(linkStart);
                        }
                    }
                    // Initialize author
                    String author = "";
                    // Does message contain brackets?
                    if((messageraw.contains("[")) && (messageraw.contains("]"))){
                        try{
                            // Get locations of brackets
                            int openBracketLocation = messageraw.indexOf("[");
                            int closeBracketLocation = messageraw.indexOf("]");
                            // Grab author, and strip youtube and twitch from author
                            author = messageraw.substring(openBracketLocation+1,closeBracketLocation).replaceFirst("YouTube:","").replaceFirst("Twitch:","");
                            author = author + " : ";
                        }
                        catch (StringIndexOutOfBoundsException e){
                            System.out.println("Could not find author");
                        }
                    } else {
                        author = event.getMessage().getAuthor().getName();
                    }
                    try{
                        textChannel.sendMessage(author + link).queue();
                    }
                    catch(InsufficientPermissionException e){
                        System.out.println("No permission allowed for that channel");
                    }
                }
            }
        }
    }
}
