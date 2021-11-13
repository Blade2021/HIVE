package rsystems.events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.sql.SQLException;

public class MessageDeletedEvent extends ListenerAdapter {

    /**
     * Delete any rows found in the embed table with the corresponding message ID.
     * @param event
     */
    @Override
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {
        try {
            if(HiveBot.sqlHandler.getEmbedChannel(event.getMessageIdLong()) != null){
                HiveBot.sqlHandler.deleteValue("HIVE_EmbedTable","MessageID",event.getMessageIdLong());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
