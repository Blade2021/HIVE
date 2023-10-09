package rsystems.commands.development;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.Reference;

import java.sql.SQLException;
import java.util.Map;

public class Meltdown extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        for(Map.Entry<String, Reference> ReferenceMap:HiveBot.referenceHandler.getRefMap().entrySet()){
            HiveBot.database.oneTimeInsertReference(ReferenceMap.getValue());
        }
    }

    @Override
    public String getHelp() {
        return "Do not trigger this unless you are Blade";
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }
}
