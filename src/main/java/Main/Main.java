package Main;

import Main.Commands.Clear;
import Main.Commands.Notify;
import Main.Commands.Unfollow;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {
    public static String prefix = "~";

    public static void main(String[] args) throws LoginException {
        String token = "token";
        JDA api = JDABuilder.createDefault(token).build();

        api.addEventListener(new Clear());
        api.addEventListener(new Notify());
        api.addEventListener(new Unfollow());

        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.watching("DrZzzs cool streams"));
    }
}

