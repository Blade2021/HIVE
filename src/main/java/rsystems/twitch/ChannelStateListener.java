package rsystems.twitch;

import com.github.philippheuer.events4j.simple.domain.EventSubscriber;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import rsystems.HiveBot;

public class ChannelStateListener {

    @EventSubscriber
    public void onChannelGoLiveEvent(ChannelGoLiveEvent event){

        HiveBot.streamHandler.setStreamActive(true,event.getStream().getTitle());

        System.out.printf("GO LIVE EVENT DETECTED! %s%n",event.getStream().getUserName());

    }

    @EventSubscriber
    public void onChannelGoOfflineEvent(ChannelGoOfflineEvent event){

        HiveBot.streamHandler.setStreamActive(false);
        HiveBot.streamHandler.setStreamTopic(null);

        System.out.printf("STREAM END EVENT DETECTED! %s%n",event.getChannel().getName());

    }

    public void rewardListener(RewardRedeemedEvent event){
        //event.getRedemption().getReward()
        System.out.println(event);
    }
}
