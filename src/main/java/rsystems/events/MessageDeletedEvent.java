package rsystems.events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class MessageDeletedEvent extends ListenerAdapter {

    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        if(HiveBot.sqlHandler.getEmbedChannel(event.getMessageIdLong()) != null){
            HiveBot.sqlHandler.deleteValue("HIVE_EmbedTable","MessageID",event.getMessageIdLong());
        }
    }
}
