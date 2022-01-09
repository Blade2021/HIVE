package rsystems.events;

import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.update.GenericChannelUpdateEvent;
import net.dv8tion.jda.api.events.thread.GenericThreadEvent;
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildStateListener extends ListenerAdapter {

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        if (event.getChannelType().isThread()) {
            ThreadChannel channel = (ThreadChannel) event.getChannel();
            channel.join().queue();
        }
    }

    @Override
    public void onThreadRevealed(ThreadRevealedEvent event) {
        event.getThread().join().queue();
    }

    @Override
    public void onGenericThread(GenericThreadEvent event) {
        event.getThread().join().queue();
    }

    @Override
    public void onGenericChannelUpdate(GenericChannelUpdateEvent<?> event) {
        if (event.isFromGuild() && event.getChannelType().isThread()) {
            ThreadChannel threadChannel = (ThreadChannel) event.getChannel();
            if (!threadChannel.isArchived()) {
                threadChannel.join().queue();
            }
        }
    }


}
