package rsystems.commands.modCommands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import rsystems.objects.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LocalPoll extends Command {

    private Map<String, ArrayList<String>> pollMap = new HashMap<>(); //User ID, Poll Data Array
    private Map<String, TextChannel> userMap = new HashMap<>();  //User ID, TextChannel of origin


    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        for (Map.Entry<String, ArrayList<String>> pollMapEntry : pollMap.entrySet()) {
            if (pollMapEntry.getKey().equalsIgnoreCase(event.getAuthor().getId())) {
                event.getChannel().sendMessage("You already have a poll request started.").queue();
                return;
            }
        }


        // Open private message to receive information
        event.getAuthor().openPrivateChannel().queue((privateChannel) ->
        {
            privateChannel.sendMessage("Starting POLL Request\nPlease observe the following:\n\nUse the keyword **CANCEL** at any time to cancel this request\nUse the keyword **SUBMIT** at any time to submit your poll request\n\nLets get started!\n\nPlease enter a description for your poll:").queue(success -> {

                        pollMap.put(event.getAuthor().getId(), new ArrayList<>());
                        userMap.put(event.getAuthor().getId(), event.getChannel());

                    },
                    failure -> {
                        event.getChannel().sendMessage("I am unable to fulfill this request.  You have your direct messages from users turned off.").queue();
                    });
        });
    }

    @Override
    public String getHelp() {
        return null;
    }
}
