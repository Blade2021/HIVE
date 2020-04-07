package rsystems.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class Role extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args[0].equalsIgnoreCase(HiveBot.prefix + "Role")) {
            try {
                if (event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    // User has administrator rights
                    if (args.length < 2) {
                        // Not enough arguments (nothing to check)
                        event.getMessage().addReaction("\uD83D\uDEAB").queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Not enough arguments supplied").queue();
                    } else {
                        String roleName = event.getMessage().getContentRaw().substring(args[0].length()+1);
                        if (event.getGuild().getRoles().toString().toLowerCase().contains(roleName.toLowerCase())) {
                            for(net.dv8tion.jda.api.entities.Role role:event.getGuild().getRoles()){
                                if(role.getName().equalsIgnoreCase(roleName)){
                                    //Role was found
                                    try {
                                        int x = 0;
                                        for (Member m : event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName(roleName, true))) {
                                            x++;
                                        }
                                        event.getMessage().addReaction("✅").queue();
                                        event.getChannel().sendMessage("`" + roleName + "` has " + x + " users.").queue();
                                        return;
                                    } catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "`" + roleName + "` was not found").queue();
                        } else {
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "`" + roleName + "` was not found").queue();
                        }
                    }
                } else {
                    // User does not have administrator rights
                    event.getMessage().addReaction("\uD83D\uDEAB").queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to this command.").queue();
                }
            } catch (NullPointerException e) {
                //todo Add better error description
                System.out.println("Null found on permissions request");
                e.printStackTrace();
            }
        }
    }
}
