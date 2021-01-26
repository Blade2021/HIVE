package rsystems.commands.adminCommands.authorization;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

public class UserAuth extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 32768;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        handleEvent(content,message);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        handleEvent(content,message);
    }

    private void handleEvent(String content, Message message){
        String[] args = content.split("\\s+");
        if((args != null) && (args.length >= 1)){

            final Long userID = Long.valueOf(args[1]);
            if(HiveBot.mainGuild().getMemberById(userID) == null){
                //error
                return;
            } else {

                final Member member = HiveBot.mainGuild().getMemberById(userID);

                if (args[0].equalsIgnoreCase("add")) {
                    final Integer authLevel = Integer.parseInt(args[2]);
                    if(authLevel != null) {
                        if(HiveBot.sqlHandler.insertAuthUser(userID, authLevel, member.getUser().getAsTag())){
                            message.addReaction("✅ ").queue();
                            return;
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("update")) {
                    final Integer authLevel = Integer.parseInt(args[2]);
                    if(authLevel != null) {
                        if(HiveBot.sqlHandler.updateAuthUser(userID,authLevel)){
                            message.addReaction("✅ ").queue();
                            return;
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if(HiveBot.sqlHandler.removeAuthUser(userID)){
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
                "**Add**\n`{prefix}{command} add [UserID] [AuthLevel]`\nThis will add the user to the authorization table with the permission value given.\n\n" +
                "**Remove**\n`{prefix}{command} remove [UserID]`\nThis will remove the user from the authorization table.\n\n" +
                "**Update**\n`{prefix}{command} update [UserID] [AuthLevel]`\nThis will update the user (if found) on the authorization table with the new authorization level.\n");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }
}
