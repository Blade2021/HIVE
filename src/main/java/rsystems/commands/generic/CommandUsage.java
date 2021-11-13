package rsystems.commands.generic;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class CommandUsage extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        if(handleEvent(content) != null){
            reply(event,"That command has been used " + handleEvent(content) + " times.");
        }
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        if(handleEvent(content) != null){
            reply(event,"That command has been used " + handleEvent(content) + " times.");
        }
    }

    @Override
    public String getHelp() {
        return "don't use this command yet. - Blade";
    }

    private Integer handleEvent(String content){
        Integer output = null;
        String[] args = content.split("\\s+");
        try {
            output = HiveBot.sqlHandler.checkUsage(args[0]);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return output;
    }
}
