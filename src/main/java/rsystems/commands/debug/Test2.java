package rsystems.commands.debug;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class Test2 extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        HiveBot.obsRemoteController.setSourceVisibility("Test","Dog",true, callback -> {
            if(callback.getStatus().equalsIgnoreCase("OK")){
                System.out.println("true");
            } else {
                System.out.println("false");
            }
        });
    }

    @Override
    public String getHelp() {
        return "something";
    }
}
