package rsystems.commands.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.Config;
import rsystems.objects.Command;

import java.time.temporal.ChronoUnit;

public class Ping extends Command {
    private static final String[] ALIASES = new String[] {};

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) {
        reply(event, "Ping: ...", m -> m.editMessage("Ping: " + message.getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS) + "ms").queue());
    }

    @Override
    public String getHelp() {

        String returnString ="`{prefix}{command}`\n" +
                "Returns the latency of the BOT.";

        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    @Override
    public String getName() {
        return "Ping";
    }

    @Override
    public String[] getAliases(){
        return ALIASES;
    }
}
