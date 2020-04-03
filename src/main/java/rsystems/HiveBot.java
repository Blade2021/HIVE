package rsystems;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.commands.*;

import javax.security.auth.login.LoginException;

public class HiveBot extends ListenerAdapter {
    public static String prefix = Config.get("prefix");

    public static void main(String[] args) throws LoginException {
        JDA api = JDABuilder.createDefault(Config.get("token")).build();
        api.addEventListener(new Info());
        api.addEventListener(new Clear());
        api.addEventListener(new Notify());
        api.addEventListener(new Helpdoc());
        api.addEventListener(new Ping());
        api.addEventListener(new Who());

        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.playing(Config.get("activity")));
    }
}

