package rsystems.events;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import javax.annotation.Nonnull;

public class PrivateMessageListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        if(HiveBot.localPollHandler.checkForUser(event.getAuthor().getIdLong())){
            HiveBot.localPollHandler.privateMessageEvent(event);
        }
    }
}
