package rsystems.commands.development;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;
import java.util.UUID;

public class Test extends Command {

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        String uniqueID = UUID.randomUUID().toString().toUpperCase();
        reply(event,uniqueID);
    }

    @Override
    public String getHelp() {
        return "Testing command";
    }
}
