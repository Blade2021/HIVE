package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import rsystems.Config;
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
                    .thenRun(() -> event.getChannel().purgeMessages(messages));
        }

        catch (IllegalArgumentException illegalArg){
            try {
                if (illegalArg.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                    // Too many messages
                    EmbedBuilder error = new EmbedBuilder();
                    error.setColor(Color.RED);
                    error.setTitle("\uD83D\uDEAB Too many messages selected");
                    error.setDescription("Between 1-99 messages can be deleted at one time.");
                    reply(event,error.build());
                    error.clear();
                } else {
                    // Messages too old
                    EmbedBuilder error = new EmbedBuilder();
                    error.setColor(Color.RED);
                    error.setTitle("\uD83D\uDEAB Selected messages are older than 2 weeks");
                    error.setDescription("Messages older than 2 weeks cannot be deleted.");
                    reply(event,error.build());
                    error.clear();
                }
            } catch (PermissionException e){
                //Missing permissions for embed
                reply(event,"Missing Permission: " + e.getPermission().getName());
            }
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
