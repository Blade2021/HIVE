package rsystems.commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;
import java.util.List;

public class Clear extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Don't accept messages from BOT Accounts
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase((HiveBot.prefix + "clear"))){
            if(args.length < 2){
                try {
                    // No Argument Detected
                    EmbedBuilder error = new EmbedBuilder();
                    error.setColor(Color.CYAN);
                    error.setTitle("Specify amount to delete");
                    error.setDescription("Usage: `" + HiveBot.prefix + "clear [#of messages]`");
                    event.getChannel().sendMessage(error.build()).queue();
                } catch (PermissionException pe){
                    event.getChannel().sendMessage("Missing Permission: " + pe.getPermission().getName()).queue();
                }
            } else {
                try {
                    if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                        List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1]) + 1).complete();
                        event.getChannel().deleteMessages(messages).queue();
                        try {
                            // Too many messages
                            EmbedBuilder success = new EmbedBuilder();
                            success.setColor(Color.ORANGE);
                            success.setTitle("Successfully deleted " + args[1] + " messages.");
                            success.setFooter("Command called by: " + event.getMessage().getAuthor().getName(), event.getAuthor().getAvatarUrl());
                            event.getChannel().sendMessage(success.build()).queue();
                        } catch (PermissionException e){
                            event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
                        }
                    } else {
                        event.getMessage().addReaction("\uD83D\uDEAB").queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have the nessessary permissons for this command").queue();
                    }
                }
                catch (IllegalArgumentException e){
                    try {
                        if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                            // Too many messages
                            EmbedBuilder error = new EmbedBuilder();
                            error.setColor(Color.RED);
                            error.setTitle("\uD83D\uDEAB Too many messages selected");
                            error.setDescription("Between 1-99 messages can be deleted at one time.");
                            event.getChannel().sendMessage(error.build()).queue();
                        } else {
                            // Messages too old
                            EmbedBuilder error = new EmbedBuilder();
                            error.setColor(Color.RED);
                            error.setTitle("\uD83D\uDEAB Selected messages are older than 2 weeks");
                            error.setDescription("Messages older than 2 weeks cannot be deleted.");
                            event.getChannel().sendMessage(error.build()).queue();
                        }
                    } catch (PermissionException pe){
                        event.getChannel().sendMessage("Missing Permission: " + pe.getPermission().getName()).queue();
                    }
                }
                catch(InsufficientPermissionException e){
                    event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
                    System.out.println(event.getAuthor().getName() + "attempted to call CLEAR without access");
                }
                catch(NullPointerException e){
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + "You do not have the nessessary permissons for this command").queue();
                }

                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
