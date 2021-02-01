package rsystems.events;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import javax.annotation.Nonnull;

public class PrivateMessageListener extends ListenerAdapter {

    /**
     * This method is used to pass messages to the localPoll handler for processing.  All commands that allow private messages are handled inside the command class.
     * @param event
     */
    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        if(HiveBot.localPollHandler.checkForUser(event.getAuthor().getIdLong())){
            HiveBot.localPollHandler.privateMessageEvent(event);
        }
    }
}
