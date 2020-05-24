package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;

import static rsystems.HiveBot.LOGGER;

public class Notify extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");


        if ((args[0].equalsIgnoreCase((HiveBot.helpPrefix + HiveBot.commands.get(1).getCommand())) || ((args.length > 1) && (args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(1).getCommand())) && (args[1].equalsIgnoreCase("??"))))) {
            LOGGER.info(HiveBot.commands.get(1).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {
                EmbedBuilder info = new EmbedBuilder();
                info.setColor(Color.CYAN);
                info.setTitle(HiveBot.prefix + "notify");
                info.setDescription("Use the above command to be alerted via mention notifications when DrZzz's posts new videos and streams");
                event.getChannel().sendMessage(info.build()).queue();
                info.clear();
            } catch (InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
            return;
        }


        if((args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(1).getCommand()) || (args[0].equalsIgnoreCase("-" + HiveBot.commands.get(1).getCommand())))){
            LOGGER.info(HiveBot.commands.get(1).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {
                if(!(event.getGuild().getSelfMember().hasPermission(Permission.ADMINISTRATOR)) && !(event.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES))){
                    event.getChannel().sendMessage("Missing permissions | Error 3X95Z").queue();
                    return;  //no point in continuing
                }


                //Check to see if user has notifications role
                if(event.getMember().getRoles().toString().contains("Notify")){
                    //User already has role

                    if((args.length > 1) && (args[1].equalsIgnoreCase("?"))){
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you have the notify role.").queue();
                        return;
                    }

                    event.getGuild().modifyMemberRoles(event.getMember(),null,event.getGuild().getRolesByName("Notify", false)).queue();
                    event.getChannel().sendMessage("Hello " + event.getMessage().getAuthor().getAsMention() + ", I have removed the notify role from you.").queue();

                } else {

                    if((args.length > 1) && (args[1].equalsIgnoreCase("?"))){
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you do not have the notify role.").queue();
                        return;
                    }
                    //User does not have role
                    event.getGuild().modifyMemberRoles(event.getMember(),event.getGuild().getRolesByName("Notify", false),null).queue();
                    event.getChannel().sendMessage("Hello " + event.getMessage().getAuthor().getAsMention() + ", I have added the notify role to you.").queue();
                }
            }

            catch(NullPointerException e){
                System.out.println("Found null for roles");
            }

            catch(InsufficientPermissionException e){
                System.out.println("Notify attempted call without access");
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }

            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
