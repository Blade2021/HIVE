package rsystems.commands.modCommands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.util.concurrent.TimeUnit;

public class LocalPoll extends Command {

    @Override
    public Integer getPermissionIndex() {
        return 4;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        if(HiveBot.localPollHandler.checkForUser(event.getAuthor().getIdLong())){
            reply(event,"You already have a poll request started.");
        } else {
            // Open private message to receive information
            event.getAuthor().openPrivateChannel().queue((privateChannel) ->
            {
                reply(event,"Check your direct messages", m -> m.delete().queueAfter(120, TimeUnit.SECONDS));
                privateChannel.sendMessage("Starting POLL Request\nPlease observe the following:\n\nUse the keyword **CANCEL** at any time to cancel this request\nUse the keyword **SUBMIT** at any time to submit your poll request\n\nLets get started!\n\nPlease enter a description for your poll:").queue(success -> {
                            HiveBot.localPollHandler.setupPoll(sender.getIdLong(), channel.getIdLong());
                        },
                        failure -> {
                            reply(event, "I am unable to fulfill this request.  You have your direct messages from users turned off.");
                        });
            });
        }
    }

    @Override
    public String getHelp() {

        StringBuilder returnString = new StringBuilder();
        returnString.append(String.format("SYNTAX: %s%s {ChannelID}\n\n", Config.get("prefix"),this.getName()));
        returnString.append("This command will help create an embeded message containging a poll formed message.  After you initiate this command, HIVE will send you a direct message with further instructions.\n\n");
        returnString.append("Once complete, the poll will be posted in the channel that the command was called, Unless otherwise specified!");
        return returnString.toString();
    }
}
