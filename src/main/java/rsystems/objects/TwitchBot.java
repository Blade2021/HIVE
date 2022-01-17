package rsystems.objects;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import rsystems.Config;
import rsystems.twitch.events.LiveEvent;

public class TwitchBot {

    private TwitchClient client;

    public TwitchBot() {
        client = TwitchClientBuilder.builder()
                .withDefaultAuthToken(new OAuth2Credential("twitch", Config.get("TWITCH_TOKEN")))
                .withEnableHelix(true)
                .build();

        client.getEventManager().getEventHandler(SimpleEventHandler.class).registerListener(new LiveEvent());
        client.getClientHelper().enableStreamEventListener(Config.get("TWITCH_CHANNEL_NAME").toLowerCase());
    }

    public TwitchClient getClient() {
        return client;
    }

    public void refreshCred(){
        TwitchIdentityProvider provider = new TwitchIdentityProvider(Config.get("TWITCH_CLIENT_ID"), Config.get("TWITCH_CLIENT_SECRET"), "");
        provider.getAppAccessToken();
    }
}

