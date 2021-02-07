package rsystems.commands.adminCommands.authorization;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

public class roleManager extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 16384;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        handleEvent(content, message);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        handleEvent(content, message);
    }

    private void handleEvent(String content, Message message) {
        String[] args = content.split("\\s+");
        if ((args != null) && (args.length >= 1)) {

            final Long roleID = Long.valueOf(args[1]);
            if (HiveBot.mainGuild().getRoleById(roleID) == null) {
                return;
            } else {

                final Role role = HiveBot.mainGuild().getRoleById(roleID);

                if (args[0].equalsIgnoreCase("add")) {
                    final Integer authLevel = Integer.parseInt(args[2]);
                    if (authLevel != null) {
                        if (authLevel <= 32767) {
                            if (HiveBot.sqlHandler.addAuthRole(roleID, role.getName(), authLevel)) {
                                message.addReaction("✅ ").queue();
                                return;
                            }
                        } else {
                            message.reply("Maximum allowed authentication level: 32767").queue();
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("update")) {
                    final Integer authLevel = Integer.parseInt(args[2]);
                    if (authLevel != null) {
                        if (authLevel <= 32767) {
                            if (HiveBot.sqlHandler.updateAuthRole(roleID, authLevel)) {
                                message.addReaction("✅ ").queue();
                                return;
                            }
                        } else {
                            message.reply("Maximum allowed authentication level: 32767").queue();
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if (HiveBot.sqlHandler.removeAuthRole(roleID)) {
                        message.addReaction("✅ ").queue();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public String getHelp() {

        String returnString = ("{prefix}{command} [Sub-Command] [args]\n\n" +
                "**Add**\n`{prefix}{command} add [RoleID] [AuthLevel]`\nThis will add the role to the authorization table with the permission value given.\n\n" +
                "**Remove**\n`{prefix}{command} remove [RoleID]`\nThis will remove the role from the authorization table.\n\n" +
                "**Update**\n`{prefix}{command} update [RoleID] [AuthLevel]`\nThis will update the role (if found) on the authorization table with the new authorization level.\n");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}", this.getName());
        return returnString;
    }

    @Override
    public String getName() {
        return "AuthRoleManager";
    }
}
