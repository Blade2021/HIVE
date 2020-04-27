package rsystems.Handlers;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.events.channel.ChannelGoLiveEvent;
import com.github.twitch4j.common.util.TwitchUtils;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.StreamMarkersList;
import com.github.twitch4j.helix.domain.Subscription;
import com.github.twitch4j.helix.domain.SubscriptionList;
import org.json.simple.JSONArray;
import rsystems.Config;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class TwitchGetVideoID {
    private static TwitchClient twitchClient = TwitchHandler.getClient();
    private static String authToken = Config.get("TWITCH-TOKEN");

    private void channelGoLive(ChannelGoLiveEvent event){
        System.out.println(event.getChannel().getName());
        System.out.println(event.getTitle());
    }

    public static void getMarkers(String videoID) {
        // TestCase

        //TwitchClient twitchClient = TwitchHandler.getClient();

        StreamMarkersList resultList = twitchClient.getHelix().getStreamMarkers(authToken, "", "", null, Config.get("Twitch-DOC-UserID"), videoID).execute();
        resultList.getStreamMarkers().forEach(stream -> {
            stream.getVideos().forEach(videoMarker -> {
                videoMarker.getMarkers().forEach(marker -> {
                    System.out.println(marker.getPosition_seconds() + ":" + marker.getDescription());
                });
            });
        });
    }

}