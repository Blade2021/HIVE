package rsystems.objects;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.twitch.ChannelStateListener;
import rsystems.twitch.events.FollowEvents;

import java.sql.SQLException;

public class TwitchBot {

    private TwitchClient client;
    private Credential credential;

    public TwitchBot() {

        try {
            reloadCredential();

            if(credential != null) {

                client = TwitchClientBuilder.builder()
                        .withDefaultAuthToken(new OAuth2Credential("twitch", credential.getAccess_Token()))
                        .withEnablePubSub(true)
                        .withClientId(Config.get("TWITCH_CLIENT_ID"))
                        .withClientSecret(Config.get("TWITCH_CLIENT_SECRET"))
                        .withEnableHelix(true)
                        .build();

                client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new ChannelStateListener());
                client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new FollowEvents());
                client.getClientHelper().enableStreamEventListener(Config.get("TWITCH_CHANNEL_NAME").toLowerCase());
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TwitchClient getClient() {
        return client;
    }

    public void refreshCredential(Credential credential, String client_ID, String client_secret){

        TwitchIdentityProvider provider = new TwitchIdentityProvider(Config.get("TWITCH_CLIENT_ID"), Config.get("TWITCH_CLIENT_SECRET"), "");
        provider.getAppAccessToken();
    }

    public void reloadCredential() throws SQLException {
        credential = HiveBot.database.getCredential(Integer.parseInt(Config.get("TWITCH_BROADCASTER_ID")));
    }

    public Credential getCredential(){
        return this.credential;
    }
}

