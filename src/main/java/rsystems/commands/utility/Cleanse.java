package rsystems.commands.utility;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Cleanse extends Command {

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        try {

            HiveBot.jda.getPresence().setStatus(OnlineStatus.DO_NOT_DISTURB);

            List<Message> messages = new ArrayList<>();
            event.getChannel().getIterableHistory()
                    .cache(false)
                    .forEachAsync(messages::add)
                    .thenRun(() -> event.getChannel().purgeMessages(messages)).thenRun(() -> HiveBot.jda.getPresence().setStatus(OnlineStatus.ONLINE));
        }

        catch(InsufficientPermissionException e){
            reply(event,"Missing Permission: " + e.getPermission().getName());
            System.out.println(event.getAuthor().getName() + "attempted to call CLEAR without access");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public String getHelp() {
        return "Clear all messages in the channel being called";
    }
}
