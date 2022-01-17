package rsystems.twitch.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Highlight;
import rsystems.HiveBot;

public class LiveEvent {

    @EventSubscriber
    public void goLiveEvent(ChannelGoLiveEvent event){

        HiveBot.streamHandler.setStreamActive(true,event.getStream().getTitle());

        System.out.println(String.format("GO LIVE EVENT DETECTED! %s",event.getStream().getUserName()));

    }

    public void endStreamEvent(ChannelGoOfflineEvent event){

        HiveBot.streamHandler.setStreamActive(false);
        HiveBot.streamHandler.setStreamTopic(null);

        System.out.println(String.format("STREAM END EVENT DETECTED! %s",event.getChannel().getName()));

    }
}
