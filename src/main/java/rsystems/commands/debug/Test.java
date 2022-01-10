package rsystems.commands.debug;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.objects.Command;

import java.sql.SQLException;

public class Test extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        reply(event,"Yes Blade this is working");
    }

    @Override
    public String getHelp() {
        return "Testing command";
    }
}
