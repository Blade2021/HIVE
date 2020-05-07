package rsystems.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.handlers.DataFile;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class HallMonitor extends ListenerAdapter {
    private DataFile dataFile = HiveBot.dataFile;
    private Integer filterLevel = Integer.valueOf(dataFile.getDatafileData().get("FilterLevel").toString());
    private String logChannel = dataFile.getDatafileData().get("LogChannelID").toString();
    String removedMessage = " Your message was removed due to inappropriate content [Vulgar Language].  Please refrain from using vulgar language here.  This action has been logged.";
    String badMessage = " Your message has been flagged due to inappropriate content [Vulgar Language].  Please edit or delete your message immediately.  This action has been logged.";

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Ignore all bots EXCEPT Restream
        if(event.getAuthor().isBot()){
            return;
        }

        if(languageCheck(event.getMessage())){
            if(filterLevel > 1) {
                try {
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + removedMessage).queue();
                } catch (InsufficientPermissionException e) {
                    sendNotice(event);
                }
            } else {
                sendNotice(event);
            }
            logInstance(event.getGuild(),event.getMember(),event.getMessage());
        }
    }

    public void onGuildMessageUpdate(GuildMessageUpdateEvent event){
        if(languageCheck(event.getMessage())){
            if(filterLevel > 1){
                try{
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + removedMessage).queue();
                }catch(InsufficientPermissionException e){
                    sendNotice(event);
                }
            } else {
                sendNotice(event);
            }
            if(filterLevel >= 2) {
                logInstance(event.getGuild(), event.getMember(), event.getMessage());
            }
        } else {
            try{
                event.getMessage().removeReaction("⚠").queue();
            } catch(NullPointerException ignored){}
        }
    }

    private boolean languageCheck(Message message){
        String lowerCase_message = message.getContentRaw().toLowerCase();
        ArrayList<String> badWords = dataFile.getArrayList("BadWords");
        for(String test:badWords){
            if(lowerCase_message.contains(test.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    private void sendNotice(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage(event.getAuthor().getAsMention() + badMessage).queue((m) -> m.delete().queueAfter(30, TimeUnit.SECONDS));
        event.getMessage().addReaction("⚠").queue();
    }

    private void sendNotice(GuildMessageUpdateEvent event){
        event.getMessage().addReaction("⚠").queue();
    }

    private void logInstance(Guild guild, Member member, Message message){
        try {
            TextChannel logChannel = guild.getTextChannelById(this.logChannel);
            logChannel.sendMessage("This message has been flagged for vulgar language.\n" + message.getJumpUrl()).queue();
        } catch(NullPointerException | InsufficientPermissionException e){
            System.out.println("Error when logging IAC event");
        }
    }
}