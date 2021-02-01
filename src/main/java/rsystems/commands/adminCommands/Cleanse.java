package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Cleanse extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 256;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event,"whut?");
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        try {

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
    public String getHelp() {

        String returnString = ("`{prefix}{command}`\n" +
                "Clear all messages in a channel.  This should be used **infrequently** as will use many API calls.\n\n");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }
}
