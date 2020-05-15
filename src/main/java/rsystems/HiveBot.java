package rsystems;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import rsystems.commands.*;
import rsystems.events.DocStream;
import rsystems.events.OnlineStatusListener;
import rsystems.handlers.DataFile;
import rsystems.handlers.TwitchHandler;

import javax.security.auth.login.LoginException;

public class HiveBot {
    public static String prefix = Config.get("prefix");
    public static String helpPrefix = Config.get("helpprefix");
    public static String restreamID = Config.get("restreamid");
    public static DataFile dataFile = new DataFile();
    private static Boolean streamMode = false;
    public static String docDUID = Config.get("docDUID");

    public static void main(String[] args) throws LoginException {
        JDA api = JDABuilder.createDefault(Config.get("token"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS,GatewayIntent.GUILD_PRESENCES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.ACTIVITY)
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
        api.addEventListener(new DocStream());
        api.addEventListener(new Test());
        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.playing(Config.get("activity")));

        if(Config.get("debug").equalsIgnoreCase("true")){
            api.addEventListener(new OnlineStatusListener());
        }

        //DataFile data = new DataFile();

        /*TwitchHandler twitchHandler = new TwitchHandler();
        twitchHandler.registerFeatures();
        twitchHandler.start();

         */


    }

    public static void setStreamMode(Boolean streamMode) {
        HiveBot.streamMode = streamMode;
    }

    public static Boolean getStreamMode(){
        return HiveBot.streamMode;
    }
}

