package rsystems.commands;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.List;

public class Clear extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){

        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase((HiveBot.prefix + HiveBot.commands.get(7).getCommand()))){
            try{
                if(RoleCheck.getRank(event,event.getMember().getId()) >= HiveBot.commands.get(7).getRank()){
                    // USER DOES HAVE ADMIN RIGHTS

                    if(args.length < 2){
                        // No Argument Detected, Post Helpful doc
                        EmbedBuilder info = new EmbedBuilder();
                        info.setColor(Color.YELLOW);
                        info.setTitle("Specify amount to delete");
                        info.setDescription("Usage: `" + HiveBot.prefix + "clear [# of messages]`");
                        event.getChannel().sendMessage(info.build()).queue();
                        info.clear();
                    } else {
                        // Argument was found, Execute method for clear command
                        int msgcount = Integer.parseInt(args[1]);
                        clearMessage(event, msgcount);
                    }
                } else {
                    // USER DOES NOT HAVE ADMIN RIGHTS
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (PermissionException e){
                // Most likely missing embed permissions
                event.getChannel().sendMessage("Missing Permission: " + e.getPermission().getName()).queue();
            } catch (NullPointerException e){
                // Could not grab role from user
                event.getChannel().sendMessage("Something went wrong...").queue();
            }
        }
    }

    private void clearMessage(GuildMessageReceivedEvent event, int msgcount){
        try {
            List<Message> messages = event.getChannel().getHistory().retrievePast(msgcount + 1).complete();
            event.getChannel().deleteMessages(messages).queue();
            try {
                EmbedBuilder info = new EmbedBuilder();
                info.setColor(Color.ORANGE);
                info.setTitle("Successfully deleted " + (messages.size()-1) + " messages.");
                info.setFooter("Command called by: " + event.getMessage().getAuthor().getName(), event.getAuthor().getAvatarUrl());
                event.getChannel().sendMessage(info.build()).queue();
                info.clear();
            } catch (PermissionException e){
                // Missing Permissions for embed
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }
        catch (IllegalArgumentException illegalArg){
            try {
                if (illegalArg.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                    // Too many messages
                    EmbedBuilder error = new EmbedBuilder();
                    error.setColor(Color.RED);
                    error.setTitle("\uD83D\uDEAB Too many messages selected");
                    error.setDescription("Between 1-99 messages can be deleted at one time.");
                    event.getChannel().sendMessage(error.build()).queue();
                    error.clear();
                } else {
                    // Messages too old
                    EmbedBuilder error = new EmbedBuilder();
                    error.setColor(Color.RED);
                    error.setTitle("\uD83D\uDEAB Selected messages are older than 2 weeks");
                    error.setDescription("Messages older than 2 weeks cannot be deleted.");
                    event.getChannel().sendMessage(error.build()).queue();
                    error.clear();
                }
            } catch (PermissionException e){
                //Missing permissions for embed
                event.getChannel().sendMessage("Missing Permission: " + e.getPermission().getName()).queue();
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
