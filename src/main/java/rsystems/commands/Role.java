package rsystems.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.util.ArrayList;
import java.util.List;

import static rsystems.HiveBot.LOGGER;

public class Role extends ListenerAdapter {

    boolean getMembers = false;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(HiveBot.commands.get(8).checkCommand(event.getMessage().getContentRaw())){
            try {
                if(RoleCheck.getRank(event,event.getMember().getId()) >= HiveBot.commands.get(8).getRank()){
                    LOGGER.info(HiveBot.commands.get(8).getCommand() + " called by " + event.getAuthor().getAsTag());
                    // User has administrator rights
                    if (args.length < HiveBot.commands.get(8).getMinimumArgCount()) {
                        // Not enough arguments (nothing to check)
                        event.getMessage().addReaction("\uD83D\uDEAB").queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Not enough arguments supplied").queue();
                    } else {
                        // Good argument count
                        String roleName = event.getMessage().getContentRaw().substring(args[0].length()+1);
                        roleName = roleName.replaceAll(" true","");

                        if((args.length > 2) && (args[2].equalsIgnoreCase("true")) && (RoleCheck.getRank(event,event.getMember().getId()) >= 3)){
                            getMembers = true;
                        }

                        getMemberRoles(event,roleName);
                    }
                } else {
                    // User does not have administrator rights
                    event.getMessage().addReaction("\uD83D\uDEAB").queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to this command.").queue();
                }
            } catch (NullPointerException e) {
                event.getChannel().sendMessage("Something went wrong...").queue();
                System.out.println("Null found on permissions request");
            }
        }
        getMembers = false;
    }

    private void getMemberRoles(GuildMessageReceivedEvent event, String roleName){
        try {
            if (event.getGuild().getRoles().toString().toLowerCase().contains(roleName.toLowerCase())) {
                for (net.dv8tion.jda.api.entities.Role role : event.getGuild().getRoles()) {
                    if (role.getName().equalsIgnoreCase(roleName)) {
                        //Role was found

                        List<String> memberList = new ArrayList();

                        try {
                            int x = 0;
                            for (Member m : event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName(roleName, true))) {
                                if (getMembers) {

                                    //Only go up to 200 members
                                    if (x < 200) {
                                        memberList.add(m.getUser().getAsTag());
                                    }
                                }
                                x++;
                            }
                            event.getMessage().addReaction("✅").queue();
                            event.getChannel().sendMessage("`" + roleName + "` has " + x + " users.").queue();

                            if ((getMembers) && (x>0)) {
                                event.getAuthor().openPrivateChannel().queue((channel) ->
                                {
                                    channel.sendMessage("`" + roleName + "`: " + memberList.toString()).queue();
                                    memberList.clear();
                                });
                            }
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + "`" + roleName + "` was not found").queue();
            } else {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + "`" + roleName + "` was not found").queue();
            }
        } catch (InsufficientPermissionException e){
            event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
        }
    }



}
