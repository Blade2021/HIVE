package rsystems.commands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.handlers.DataFile;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.LOGGER;

public class HallMonitor extends ListenerAdapter {
    private DataFile dataFile = HiveBot.dataFile;
    private Integer filterLevel = Integer.valueOf(dataFile.getDatafileData().get("FilterLevel").toString());
    private String logChannel = dataFile.getDatafileData().get("LogChannelID").toString();
    String removedMessage = " Your message was removed due to inappropriate content [Vulgar Language].  Please refrain from using vulgar language here.  This action has been logged.";
    String badMessage = " Your message has been flagged due to inappropriate content [Vulgar Language].  Please edit or delete your message immediately or risk the message being deleted.  This action has been logged.";

    //Initialize a HashMap for storing futures
    private Map<String,Future<?>> futures = new HashMap<>();


    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Ignore all bots
        if(event.getAuthor().isBot()){
            return;
        }

        //Does message contain vulgar language?
        if(languageCheck(event.getMessage().getContentRaw())){
            if(filterLevel > 1) {
                try {
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + removedMessage).queue();
                } catch (InsufficientPermissionException e) {
                    sendNotice(event);
                }
            } else {
                sendNotice(event);
                futures.put(event.getMessageId(),event.getMessage().delete().submitAfter(30,TimeUnit.SECONDS));
            }
            logInstance(event.getGuild(),event.getMember(),event.getMessage());
        }
    }

    public void onGuildMessageUpdate(GuildMessageUpdateEvent event){
        if(languageCheck(event.getMessage().getContentRaw())){
            if(filterLevel > 1){
                try{
                    event.getMessage().delete().queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + removedMessage).queue();
                }catch(InsufficientPermissionException e){
                    sendNotice(event);
                }
            } else {
                sendNotice(event);
                Boolean futureFound = false;
                for(Map.Entry<String,Future<?>> entry:futures.entrySet()){
                    String key = entry.getKey();
                    if(key.equalsIgnoreCase(event.getMessageId())){
                        entry.getValue().cancel(true);
                        futureFound = true;
                    }
                }
                if(!futureFound){
                    futures.put(event.getMessageId(),event.getMessage().delete().submitAfter(30,TimeUnit.SECONDS));
                    try {
                        event.getMessage().addReaction("⁉").queue();
                    }catch(NullPointerException e){

                    }
                }
            }
            if(filterLevel >= 2) {
                logInstance(event.getGuild(), event.getMember(), event.getMessage());
            }
        } else {
            try{
                String messageid = "";

                // Cancel the future
                for(Map.Entry<String,Future<?>> entry:futures.entrySet()){
                    String key = entry.getKey();
                    if(key.equalsIgnoreCase(event.getMessageId())){
                        entry.getValue().cancel(true);
                        System.out.println("Removing future for message: " + entry.getKey());
                        messageid = entry.getKey();
                    }
                }


                //Remove the entry from the HashMap
                Iterator it = futures.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    if(pair.getKey().toString().equalsIgnoreCase(messageid)){
                        it.remove();
                    }
                }

                event.getMessage().removeReaction("⚠").queue();
                event.getMessage().removeReaction("⁉").queue();

                System.out.println(futures.size());
            } catch(NullPointerException ignored){}
        }
    }

    public boolean languageCheck(String message){
        String lowerCase_message = message.toLowerCase();
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
        LOGGER.warning(member.getUser().getAsTag() + " flagged for using vulgar language.  MessageID:" + message.getId());

        try {
            TextChannel logChannel = guild.getTextChannelById(this.logChannel);
            logChannel.sendMessage("This message has been flagged for vulgar language.\n" + message.getJumpUrl()).queue();
        } catch(NullPointerException | InsufficientPermissionException e){
            System.out.println("Error when logging IAC event");
        }
    }
}