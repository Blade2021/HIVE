package rsystems.commands.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

public class Shutdown extends Command {

    private static final String[] ALIASES = new String[] {"sd"};

    @Override
    public Integer getPermissionIndex() {
        return 1024;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) {
        HiveBot.dbPool.getPool().close();

        event.getMessage().reply("Goodbye...").queue();

        event.getJDA().shutdown();
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String[] getAliases(){
        return ALIASES;
    }
}
