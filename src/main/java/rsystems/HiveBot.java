package rsystems;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import rsystems.commands.*;
import rsystems.handlers.DataFile;
import rsystems.handlers.TwitchHandler;

import javax.security.auth.login.LoginException;

public class HiveBot {
    public static String prefix = Config.get("prefix");
    public static String helpPrefix = Config.get("helpprefix");
    public static String restreamID = Config.get("restreamid");
    public static DataFile dataFile = new DataFile();

    public static void main(String[] args) throws LoginException {
        JDA api = JDABuilder.createDefault(Config.get("token"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build();

        api.addEventListener(new AdminInfo());
        api.addEventListener(new Ask());
        api.addEventListener(new AssignRole());
        api.addEventListener(new Clear());
        api.addEventListener(new Code());
        api.addEventListener(new HallMonitor());
        api.addEventListener(new Helpdoc());
        api.addEventListener(new Info());
        api.addEventListener(new LinkGrabber());
        api.addEventListener(new Notify());
        api.addEventListener(new Page());
        api.addEventListener(new Ping());
        api.addEventListener(new Poll());
        api.addEventListener(new Role());
        api.addEventListener(new Say());
        api.addEventListener(new Shutdown());
        api.addEventListener(new Status());
        api.addEventListener(new Twitch());
        api.addEventListener(new TwitchSub());
        api.addEventListener(new Who());
        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.playing(Config.get("activity")));


        //DataFile data = new DataFile();

        /*TwitchHandler twitchHandler = new TwitchHandler();
        twitchHandler.registerFeatures();
        twitchHandler.start();

         */


    }


}

