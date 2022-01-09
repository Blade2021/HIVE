package rsystems.commands.stream;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class StreamMode extends Command {

    @Override
    public Integer getPermissionIndex() {
        return 8;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        String[] args = content.split("\\s+");

        if((args != null) && (!args[0].isEmpty())){
            if(args[0].equalsIgnoreCase("true")){
                HiveBot.streamHandler.setStreamActive(true);
            } else {
                HiveBot.streamHandler.setStreamActive(false);
            }

            reply(event,"Setting stream mode to: " + String.valueOf(HiveBot.streamHandler.isStreamActive()).toUpperCase());

        } else {
            reply(event, "Stream mode is currently `" + String.valueOf(HiveBot.streamHandler.isStreamActive()).toUpperCase() + "`");
        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
