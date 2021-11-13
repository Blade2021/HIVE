package rsystems.commands.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.LED;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class LedList extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        try {
            ArrayList<String> ledList = HiveBot.sqlHandler.getLEDList();

            reply(event,ledList.toString());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
