package rsystems.commands;



import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.LOGGER;

public class Janitor extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if (event.getMessage().getAuthor().isBot()) {
            if (event.getAuthor().getId().equalsIgnoreCase("83010416610906112")) {
                if (event.getMessage().getContentRaw().startsWith("How long until the next Live Stream!?!")) {
                    try {
                        event.getMessage().addReaction("⏳").queue();
                        event.getMessage().delete().reason("Removing stream message").queueAfter(10, TimeUnit.MINUTES,null,(exception) -> {
                            LOGGER.severe("Failed to delete stream message " + exception.getCause().toString());
                        });
                    }catch(PermissionException e){
                        LOGGER.severe("Failed to delete stream message.  Missing Permissions: " + e.getPermission() + " Channel: " + event.getChannel().getName());
                    }catch(NullPointerException | ErrorResponseException e){
                    }
                }
            }
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase("!stream")) {
            try {
                event.getMessage().addReaction("⏳").queue();
                event.getMessage().delete().reason("Removing stream trigger").queueAfter(30, TimeUnit.SECONDS,null,(exception) -> {
                    LOGGER.severe("Failed to delete stream trigger " + exception.getCause().toString());
                });
            }catch(PermissionException e){
                LOGGER.severe("Failed to delete stream trigger.  Missing Permissions: " + e.getPermission() + " Channel: " + event.getChannel().getName());
            }catch(NullPointerException | ErrorResponseException e){
            }
        }

    }
}