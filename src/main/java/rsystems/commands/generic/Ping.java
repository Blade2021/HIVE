package rsystems.commands.generic;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.objects.Command;

import java.time.temporal.ChronoUnit;

public class Ping extends Command {
    private static final String[] ALIASES = new String[] {};

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event, "Ping: ...", m -> m.editMessage("Ping: " + message.getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS) + "ms").queue());
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        reply(event, "Ping: ...", m -> m.editMessage("Ping: " + message.getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS) + "ms").queue());
    }

    @Override
    public String getHelp() {
        return "Just a test";
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
