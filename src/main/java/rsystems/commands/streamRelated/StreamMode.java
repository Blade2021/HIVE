package rsystems.commands.streamRelated;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

public class StreamMode extends Command {

    @Override
    public Integer getPermissionIndex() {
        return 8;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        String[] args = content.split("\\s+");

        if((args != null) && (!args[0].isEmpty())){
            if(args[0].equalsIgnoreCase("true")){
                HiveBot.setStreamMode(true);
            } else {
                HiveBot.setStreamMode(false);
            }

            reply(event,"Setting stream mode to: " + String.valueOf(HiveBot.getStreamMode()).toUpperCase());

        } else {
            reply(event, "Stream mode is currently `" + String.valueOf(HiveBot.getStreamMode()).toUpperCase() + "`");
        }
    }

    @Override
    public String getHelp() {
        return null;
    }

}
