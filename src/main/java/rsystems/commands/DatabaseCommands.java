package rsystems.commands;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;
import rsystems.handlers.SQLHandler;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import static rsystems.HiveBot.LOGGER;

public class DatabaseCommands extends ListenerAdapter{

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        //Sponge command

        // User exists in DB
        if(HiveBot.commands.get(41).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.getRank(event, event.getMember().getId()) >= HiveBot.commands.get(41).getRank()) {
                    LOGGER.info(HiveBot.commands.get(41).getCommand() + " called by " + event.getAuthor().getAsTag());

                    SQLHandler sqlHandler = new SQLHandler();
                    if(sqlHandler.getDate(event.getMember().getId()).isBlank()){
                        event.getMessage().addReaction("⚠").queue();
                    } else {
                        event.getMessage().addReaction("✅").queue();
                    }
                }else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
            }
        }

        // Get list of users in DB
        if(HiveBot.commands.get(42).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.getRank(event, event.getMember().getId()) >= HiveBot.commands.get(42).getRank()) {
                    LOGGER.info(HiveBot.commands.get(42).getCommand() + " called by " + event.getAuthor().getAsTag());

                    SQLHandler sqlHandler = new SQLHandler();

                    ArrayList<String> users = new ArrayList<>();
                    users.addAll(sqlHandler.getAllUsers("NAME"));

                    event.getChannel().sendMessage(users.toString()).queue();
                }else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
            }
        }

        // Set name of user in DB
        if(HiveBot.commands.get(43).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.getRank(event, event.getMember().getId()) >= HiveBot.commands.get(43).getRank()) {
                    LOGGER.info(HiveBot.commands.get(43).getCommand() + " called by " + event.getAuthor().getAsTag());

                    SQLHandler sqlHandler = new SQLHandler();
                    if(sqlHandler.setName(args[1],args[2]) > 0){
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        event.getMessage().addReaction("⚠").queue();
                    }
                }else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
            }
        }

        // Delete user from DB
        if(HiveBot.commands.get(44).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.getRank(event, event.getMember().getId()) >= HiveBot.commands.get(44).getRank()) {
                    LOGGER.info(HiveBot.commands.get(44).getCommand() + " called by " + event.getAuthor().getAsTag());

                    SQLHandler sqlHandler = new SQLHandler();
                    if(sqlHandler.removeUser(args[1])){
                        event.getMessage().addReaction("⚠").queue();
                    } else {
                        event.getMessage().addReaction("✅").queue();
                    }
                }else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
            }
        }

    }

}
