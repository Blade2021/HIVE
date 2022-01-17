package rsystems.tasks;

import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import rsystems.Config;

public class RefreshToken {

    public static void refreshToken(){

        TwitchIdentityProvider provider = new TwitchIdentityProvider(Config.get("TWITCH_CLIENT_ID"),Config.get("TWITCH_CLIENT_SECRET"),Config.get("TWITCH_REDIRECT_URI"));

    }
}
