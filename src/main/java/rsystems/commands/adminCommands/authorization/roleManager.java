package rsystems.commands.adminCommands.authorization;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

public class roleManager extends Command {
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

            final Long roleID = Long.valueOf(args[1]);
            if(HiveBot.drZzzGuild().getRoleById(roleID) == null){
                return;
            } else {

                final Role role = HiveBot.drZzzGuild().getRoleById(roleID);

                if (args[0].equalsIgnoreCase("add")) {
                    final Integer authLevel = Integer.parseInt(args[2]);
                    if(authLevel != null) {
                        if(HiveBot.sqlHandler.addAuthRole(roleID, role.getName(), authLevel)){
                            message.addReaction("✅ ").queue();
                            return;
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("update")) {
                    final Integer authLevel = Integer.parseInt(args[2]);
                    if(authLevel != null) {
                        if(HiveBot.sqlHandler.updateAuthRole(roleID, authLevel)){
                            message.addReaction("✅ ").queue();
                            return;
                        }
                    }
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if(HiveBot.sqlHandler.removeAuthRole(roleID)){
                        message.addReaction("✅ ").queue();
                        return;
                    }
                }
            }
        }
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getName(){
        return "authRoleManager";
    }
}
