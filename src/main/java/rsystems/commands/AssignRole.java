package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.AssignableRoles;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.List;

public class AssignRole extends ListenerAdapter {

    // Initialize our AssignableRoles Object
    AssignableRoles aroles = new AssignableRoles();

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        // Helpful notes or not enough arguments
        if ((args[0].equalsIgnoreCase((HiveBot.helpPrefix + "assign")) || ((args.length < 2) && (args[0].equalsIgnoreCase(HiveBot.prefix + "assign"))))) {
            try {
                // Get user authorization level
                if (RoleCheck.getRank(event, Long.toString(event.getMember().getUser().getIdLong())) >= 2) {
                    EmbedBuilder info = new EmbedBuilder();
                    info.setColor(Color.CYAN);
                    info.setTitle(HiveBot.prefix + "assign / " + HiveBot.prefix+ "resign" );
                    info.setDescription("This command is available to mods to add/remove assignable roles to users.\n\nTo use this command follow the syntax below.  Remember all users must be **MENTIONS**! You CAN mention more then one user at a time.");
                    info.addField("`Assign`",HiveBot.prefix + "assign RoleName " + event.getAuthor().getAsMention(),false);
                    info.addField("`Resign`",HiveBot.prefix + "resign RoleName " + event.getAuthor().getAsMention(),false);
                    event.getChannel().sendMessage(info.build()).queue();
                    info.clear();
                } else {
                    event.getChannel().sendMessage("You do not have access to that command.").queue();
                }
            } catch (InsufficientPermissionException e) {
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
            return;

        }

        // Assign ROLE command
        if((args[0].equalsIgnoreCase(HiveBot.prefix + "assign")) && (args.length > 2)){
            try {
                // Get user authorization level
                if (RoleCheck.getRank(event, Long.toString(event.getMember().getUser().getIdLong())) >= 2) {
                    // Initialize a boolean for return level
                    Boolean roleFound = false;
                    for (String role : aroles.getRoles()) {
                        // Search the assignableRoles array for role that was sent
                        if (role.equalsIgnoreCase(args[1])) {
                            // Role was found in array, now get a list of Mentioned members for assignment
                            List<Member> mentions = event.getMessage().getMentionedMembers();
                            for (Member m : mentions) {
                                try {
                                    // Assign role to member
                                    event.getGuild().modifyMemberRoles(m, event.getGuild().getRolesByName(role, false), null).queue();
                                    roleFound = true;
                                } catch(NullPointerException e){
                                    // Member was not found
                                    roleFound = false;
                                }
                            }
                        }
                    }
                    // Attach an emoji for verification
                    if(roleFound){
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        event.getMessage().addReaction("⚠").queue();
                    }
                } else {
                    event.getMessage().addReaction("🚫").queue();
                    event.getChannel().sendMessage("You do not have access to that command").queue();
                }
            } catch(NullPointerException e) {

            }
            catch(InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }

        // Resign command
        if((args[0].equalsIgnoreCase(HiveBot.prefix + "resign")) && (args.length > 2)){
            try {
                // Get user authorization level
                if (RoleCheck.getRank(event, Long.toString(event.getMember().getUser().getIdLong())) >= 2) {
                    // Initalize a boolean for return level
                    Boolean roleFound = false;
                    for (String role : aroles.getRoles()) {
                        // Search the assignableRoles array for role that was sent
                        if (role.equalsIgnoreCase(args[1])) {
                            // Role was found in array, now get a list of Mentioned members for assignment
                            List<Member> mentions = event.getMessage().getMentionedMembers();
                            for (Member m : mentions) {
                                try {
                                    // Remove role from member
                                    event.getGuild().modifyMemberRoles(m, null, event.getGuild().getRolesByName(role, false)).queue();
                                    roleFound = true;
                                } catch(NullPointerException e){
                                    // Member was not found
                                    roleFound = false;
                                }
                            }
                        }
                    }
                    // Attach an emoji for verification
                    if(roleFound){
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        event.getMessage().addReaction("⚠").queue();
                    }
                } else {
                    event.getMessage().addReaction("🚫").queue();
                    event.getChannel().sendMessage("You do not have access to that command").queue();
                }
            } catch(NullPointerException e) {

            } catch(InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }

        if((args[0].equalsIgnoreCase(HiveBot.prefix + "getARoles"))){
            if (RoleCheck.getRank(event, Long.toString(event.getMember().getUser().getIdLong())) >= 2) {
                event.getChannel().sendMessage(aroles.getRoles().toString()).queue();
            } else {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that commmand").queue();
            }
        }

        if((args[0].equalsIgnoreCase(HiveBot.prefix + "reloadaroles"))){
            if (RoleCheck.getRank(event, Long.toString(event.getMember().getUser().getIdLong())) >= 3) {
                try{
                    aroles.loadRoleFile();
                    event.getMessage().addReaction("✅").queue();
                } catch(NullPointerException e){
                    e.printStackTrace();
                }
            } else {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that commmand").queue();
            }
        }
    }

}
