package rsystems.commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
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
                // No Argument Detected
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.CYAN);
                error.setTitle("Specify amount to delete");
                error.setDescription("Usage: `" + HiveBot.prefix + "clear [#of messages]`");
                event.getChannel().sendMessage(error.build()).queue();
            } else {
                try {

                    if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

                        if(!(event.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)) && !(event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL))){
                            event.getMessage().addReaction("\uD83D\uDEAB").queue();
                            event.getChannel().sendMessage("Missing permissions | Error 3X74R").queue();
                            return;
                        }

                        List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1]) + 1).complete();
                        event.getChannel().deleteMessages(messages).queue();

                        // Too many messages
                        EmbedBuilder success = new EmbedBuilder();
                        success.setColor(Color.ORANGE);
                        success.setTitle("Successfully deleted " + args[1] + " messages.");
                        success.setFooter("Command called by: " + event.getMessage().getAuthor().getName(), event.getAuthor().getAvatarUrl());
                        event.getChannel().sendMessage(success.build()).queue();
                    } else {
                        event.getMessage().addReaction("\uD83D\uDEAB").queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have the nessessary permissons for this command").queue();
                    }
                }
                catch (IllegalArgumentException e){
                    if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                        // Too many messages
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(Color.RED);
                        error.setTitle("\uD83D\uDEAB Too many messages selected");
                        error.setDescription("Between 1-100 messages can be deleted at one time.");
                        event.getChannel().sendMessage(error.build()).queue();
                    } else {
                        // Messages too old
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(Color.RED);
                        error.setTitle("\uD83D\uDEAB Selected messages are older than 2 weeks");
                        error.setDescription("Messages older than 2 weeks cannot be deleted.");
                        event.getChannel().sendMessage(error.build()).queue();
                    }
                }
                catch(InsufficientPermissionException e){
                    System.out.println("Clear tried to call without access");
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
