package rsystems.commands.modCommands;

import com.sun.tools.javac.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.sql.SQLException;

import static rsystems.HiveBot.LOGGER;

public class Shutdown extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }

        String message = event.getMessage().getContentRaw();
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(!args[0].startsWith(HiveBot.prefix)){
            return;
        }


        if(HiveBot.commands.get(0).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(0))){
                    event.getChannel().sendMessage("Shutting down...").queue();
                    try{
                        HiveBot.sqlHandler.closeConnection();
                    } catch(NullPointerException e){
                        System.out.println("Could not find connection to DB");
                    }
                    System.out.println("Shut down called by " + event.getMessage().getAuthor().getName());
                    event.getJDA().shutdown();
                } else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
                System.out.println("Null permission found");
            }
        }

    }

}

