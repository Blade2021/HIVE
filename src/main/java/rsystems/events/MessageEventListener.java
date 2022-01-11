package rsystems.events;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.concurrent.TimeUnit;

public class MessageEventListener extends ListenerAdapter {

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        if(event.isFromGuild()){
            if(event.getChannelType().isThread()){
                return;
            } else {
                if(event.getMessage().isPinned()){
                    MessageBuilder messageBuilder = new MessageBuilder();
                    messageBuilder.setActionRows(ActionRow.of(Button.primary(String.format("depin:%d",event.getMessage().getIdLong()),"Automatic Depin")));
                    messageBuilder.setContent("Would you like to automatically depin this message in 7 days?");

                    event.getMessage().reply(messageBuilder.build()).queue(success -> {
                        success.delete().queueAfter(15, TimeUnit.MINUTES);
                    });
                }
            }
        }
    }
}
