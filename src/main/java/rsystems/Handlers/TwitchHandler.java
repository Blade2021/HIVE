package rsystems.Handlers;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import rsystems.Config;

public class TwitchHandler {

    private static TwitchClient client;


    public TwitchHandler() {
        client = TwitchClientBuilder.builder()
                .withEnableHelix(true)
                //.withEnablePubSub(true)
                .withClientId(Config.get("Twitch-ClientID"))
                .withClientSecret(Config.get("Twitch-Secret"))
                .build();

        CredentialManager credentialManager = CredentialManagerBuilder.builder().build();
        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(Config.get("Twitch-ClientID"), Config.get("Twitch-Secret"), ""));

        client.getClientHelper().enableStreamEventListener("tevent");
    }

    public static TwitchClient getClient(){
        return client;
    }
}
