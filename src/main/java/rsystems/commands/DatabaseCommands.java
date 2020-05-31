package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;
import rsystems.handlers.SQLHandler;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Future;

import static rsystems.HiveBot.LOGGER;

public class DatabaseCommands extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        // User exists in DB
        if (HiveBot.commands.get(41).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(41))) {

                    SQLHandler sqlHandler = new SQLHandler();
                    if (sqlHandler.getDate(event.getMember().getId()).isBlank()) {
                        event.getMessage().addReaction("⚠").queue();
                    } else {
                        event.getMessage().addReaction("✅").queue();
                    }
                }
            } catch (NullPointerException e) {
            }
        }

        // Get list of users in DB
        if (HiveBot.commands.get(42).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(42))) {

                    SQLHandler sqlHandler = new SQLHandler();
                    HashMap<String, String> idMap = new HashMap<>();
                    idMap.putAll(sqlHandler.getAllUsers());

                    EmbedBuilder output = new EmbedBuilder();
                    StringBuilder userMap = new StringBuilder();
                    StringBuilder nameMap = new StringBuilder();


                    for (Map.Entry<String, String> entry : idMap.entrySet()) {
                        userMap.append(entry.getKey()).append("\n");
                        nameMap.append(entry.getValue()).append("\n");
                    }

                    output.setTitle("Privileged Users in DB");
                    output.addField("User ID", userMap.toString(), true);
                    output.addField("User Name", nameMap.toString(), true);

                    /*
                    ArrayList<String> users = new ArrayList<>();
                    users.addAll(sqlHandler.getAllUsers("NAME"));
                    */
                    event.getChannel().sendMessage(output.build()).queue();
                    output.clear();
                }
            } catch (NullPointerException e) {
            }
        }

        // Set name of user in DB
        if (HiveBot.commands.get(43).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(43))) {

                    SQLHandler sqlHandler = new SQLHandler();
                    if (sqlHandler.setName(args[1], args[2]) > 0) {
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        event.getMessage().addReaction("⚠").queue();
                    }
                }
            } catch (NullPointerException e) {
            }
        }

        // Delete user from DB
        if (HiveBot.commands.get(44).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(44))) {

                    SQLHandler sqlHandler = new SQLHandler();
                    if (sqlHandler.removeUser(args[1])) {
                        event.getMessage().addReaction("⚠").queue();
                    } else {
                        event.getMessage().addReaction("✅").queue();
                    }
                }
            } catch (NullPointerException e) {
            }
        }

        // Get date using ID
        if (HiveBot.commands.get(45).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(45))) {

                    SQLHandler sqlHandler = new SQLHandler();

                    //Check to see if mentions were used

                    // No mentions found
                    if (event.getMessage().getMentionedMembers().size() == 0) {
                        String date = sqlHandler.getDate(args[1]);

                        if (date.isBlank()) {
                            event.getMessage().addReaction("⚠").queue();
                        } else {
                            event.getMessage().addReaction("✅").queue();
                            event.getChannel().sendMessage(date).queue();
                        }
                    } else {

                        // Found mentions
                        StringBuilder dateString = new StringBuilder();

                        event.getMessage().getMentionedMembers().forEach(member -> {
                            String date = sqlHandler.getDate(member.getId());

                            if (!date.isBlank()) {
                                dateString.append(member.getUser().getAsTag()).append(" - ").append(date).append("\n");
                            }
                        });

                        event.getChannel().sendMessage(dateString.toString()).queue();
                    }
                }
            } catch (NullPointerException e) {
            }
        }

        // Get all data
        if (HiveBot.commands.get(46).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(46))) {

                    SQLHandler sqlHandler = new SQLHandler();
                    ArrayList<String> userIDs = new ArrayList<>();
                    userIDs = sqlHandler.getAllUsers("ID");

                    EmbedBuilder output = new EmbedBuilder();

                    //StringBuilder idString = new StringBuilder();
                    StringBuilder nameString = new StringBuilder();
                    StringBuilder dateString = new StringBuilder();

                    for (String s : userIDs) {
                        //idString.append(s).append("\n");
                        nameString.append(sqlHandler.getName(s)).append("\n");
                        dateString.append(sqlHandler.getDate(s)).append("\n");
                    }

                    //output.addField("ID",idString.toString(),true);
                    output.addField("Name", nameString.toString(), true);
                    output.addField("Date", dateString.toString(), true);

                    event.getChannel().sendMessage(output.build()).queue();
                    output.clear();

                }
            } catch (NullPointerException e) {
                System.out.println(e.getCause());
            }
        }


    }
}
