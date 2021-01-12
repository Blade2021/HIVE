package rsystems.commands.modCommands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import rsystems.HiveBot;
import rsystems.objects.Command;

public class LocalPoll extends Command {

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
        return null;
    }
}
