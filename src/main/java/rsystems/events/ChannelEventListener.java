package rsystems.events;

import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.sql.SQLException;

public class ChannelEventListener extends ListenerAdapter {

    @Override
    public void onChannelDelete(final ChannelDeleteEvent event) {
        try {
            HiveBot.database.ticketChannelCheck(event.getChannel().getIdLong());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
