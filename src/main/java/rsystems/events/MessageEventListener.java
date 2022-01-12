package rsystems.events;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MessageEventListener extends ListenerAdapter {

    @Override
    public void onMessageUpdate(final MessageUpdateEvent event) {
        if(event.isFromGuild()){
            if(event.getChannelType().isThread()){
                return;
            } else {
                if(event.getMessage().isPinned()){
                    MessageBuilder messageBuilder = new MessageBuilder();
                    messageBuilder.setActionRows(ActionRow.of(
                            Button.primary(String.format("depin-7:%d:%d",event.getMessage().getIdLong(),event.getAuthor().getIdLong()),"7 Day De-Pin"),
                            Button.primary(String.format("depin-30:%d:%d",event.getMessage().getIdLong(),event.getAuthor().getIdLong()),"30 Day De-Pin"))
                    );
                    messageBuilder.setContent("Would you like to automatically De-Pin this message?");

                    event.getMessage().reply(messageBuilder.build()).queue(success -> {
                        success.delete().queueAfter(15, TimeUnit.MINUTES);
                    });

                }
            }
        }
    }

    @Override
    public void onMessageDelete(final MessageDeleteEvent event) {
        try {
            HiveBot.database.deleteRow("MessageTable","MessageID",event.getMessageIdLong());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
