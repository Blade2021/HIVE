package rsystems.commands.modCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UserRole extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        Message returnMessage = handleCommand(sender,message,content);
        if(returnMessage != null){
            reply(event,returnMessage);
        }
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        Message returnMessage = handleCommand(sender,message,content);
        if(returnMessage != null){
            reply(event,returnMessage);
        }
    }

    @Override
    public String getHelp() {
        String returnString ="`{prefix}{command} [Sub-Command] [args]`\n\n" +
                "**Assign**\n`{prefix}{command} assign [RoleID] [Mentions]/[UserID]`\nThis will add the role to the user.\n\n"+
                "**Resign**\n`{prefix}{command} remove [RoleID] [Mentions]/[UserID]`\nThis will take away the role from the user.\n\n"+
                "**List**\n`{prefix}{command} list`\nThis will list all assignable roles found on the whitelist.\n\n"+
                "Please note that only **ASSIGNABLE** roles are allowed to be assigned to users via this command.";

        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    private Message handleCommand(User sender, Message message, String content){
        MessageBuilder mb = new MessageBuilder();

        String[] args = content.split("\\s+");
        if(args != null) {

            if (args[0].equalsIgnoreCase("list")) {
                StringBuilder roleIDString = new StringBuilder();
                StringBuilder roleNameString = new StringBuilder();

                List<Long> assignableRoleList = HiveBot.sqlHandler.assignableRoleList();
                if(assignableRoleList.size() >= 1){
                    for(Long roleID:assignableRoleList){
                        if(HiveBot.mainGuild().getRoleById(roleID) != null){
                            Role role = HiveBot.mainGuild().getRoleById(roleID);

                            roleIDString.append(roleID).append("\n");
                            roleNameString.append(role.getName()).append("\n");

                        }
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Assignable Roles")
                            .setColor(Color.decode("#21ff67"))
                            .addField("Role ID:",roleIDString.toString(),true)
                            .addField("Role Name:",roleNameString.toString(),true);

                    mb.setEmbed(embedBuilder.build());
                    embedBuilder.clear();
                    return mb.build();

                } else {
                    mb.append("No assignable roles found");
                    return mb.build();
                }

            } else {

                if (args.length <= 1) {
                    mb.append("Not enough arguments found");
                    return mb.build();
                }

                Long roleID = Long.parseLong(args[1]);
                if (HiveBot.sqlHandler.checkAssignableRole(roleID)) {

                    Role role = null;

                    if (HiveBot.mainGuild().getRoleById(roleID) != null) {
                        role = HiveBot.mainGuild().getRoleById(roleID);
                    } else {
                        mb.append("That role was not found");
                    }

                    List<Member> members = new ArrayList<>();
                    if (!message.getMentionedMembers().isEmpty()) {
                        members.addAll(message.getMentionedMembers());
                    } else {
                        Long memberID = Long.parseLong(args[2]);
                        if (HiveBot.mainGuild().getMemberById(memberID) != null) {
                            members.add(HiveBot.mainGuild().getMemberById(memberID));
                        }
                    }

                    if (members.size() >= 1) {

                        if (args[0].equalsIgnoreCase("assign")) {
                            for (Member m : members) {
                                HiveBot.mainGuild().addRoleToMember(m, role).reason("Requested by: " + message.getAuthor().getAsTag()).queue();
                            }
                            mb.append("Added role to members");
                            return mb.build();
                        }

                        if (args[0].equalsIgnoreCase("resign")) {
                            for (Member m : members) {
                                HiveBot.mainGuild().removeRoleFromMember(m, role).reason("Requested by: " + message.getAuthor().getAsTag()).queue();
                            }
                            mb.append("Removed role from members");
                            return mb.build();
                        }


                    }

                } else {
                    mb.append("I did not find that role on the authorized assignable role list.");
                    return mb.build();
                }
            }
        }
        return null;
    }

}
