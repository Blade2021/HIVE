package rsystems.twitch.events;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.eventsub.events.ChannelFollowEvent;

public class FollowEvents {

    @EventSubscriber
    public void onChannelFollowEvent(ChannelFollowEvent event){
        System.out.println(event);
    }
}
