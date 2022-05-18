package rsystems.commands.debug;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class Test2 extends Command {

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {

        HiveBot.obsRemoteController.setSourceVisibility("Test","Dog",false,callback -> {
            System.out.println("Test2");
            System.out.println(callback.toString());
        });

    }

    @Override
    public String getHelp() {
        return "Testing command";
    }
}