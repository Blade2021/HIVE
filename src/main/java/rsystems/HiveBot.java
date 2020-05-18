package rsystems;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import rsystems.adapters.Command;
import rsystems.commands.*;
import rsystems.events.DocStream;
import rsystems.events.HIVE;
import rsystems.events.WelcomeWagon;
import rsystems.handlers.CommandData;
import rsystems.handlers.DataFile;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;

public class HiveBot{
    public static String prefix = Config.get("prefix");
    public static String helpPrefix = Config.get("helpprefix");
    public static String version = "0.16.2";
    public static String restreamID = Config.get("restreamid");
    public static DataFile dataFile = new DataFile();
    private static Boolean streamMode = false;
    public static String docDUID = Config.get("docDUID");

    public static ArrayList<Command> commands = new ArrayList<Command>();
    //public static AdminCommand shutdown = new AdminCommand("shutdown","shutdown","Shuts down the bot",2, 0);

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
        api.addEventListener(new HIVE());
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
        api.addEventListener(new Analyze());
        api.addEventListener(new WelcomeWagon());
        api.addEventListener(new Help());
        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.playing(Config.get("activity")));

        //AdminCommand shutdown = new AdminCommand("shutdown","shutdown","Shuts down the bot",2, 0);

        /*TwitchHandler twitchHandler = new TwitchHandler();
        twitchHandler.registerFeatures();
        twitchHandler.start();

         */

        commands.add(new Command("Shutdown","Shutdown HIVE","shutdown",0, 3));  //0
        commands.add(new Command("Notify","Add/Remove notify role for notifications","notify",0,0));  //1
        commands.add(new Command("Info","Get helpful information about HIVE","info",0,0)); // 2
        commands.add(new Command("Who","Display information about HIVE or a user","who",0,0)); // 3
        commands.add(new Command("Ping","Display the latency for HIVE","ping",0,0)); // 4
        commands.add(new Command("Code","Information regarding formatting code on discord","code",0,0)); // 5
        commands.add(new Command("Page","Page the doc to get his attention","page",0,2)); // 6
        commands.add(new Command("Clear","Clear messages from a channel","clear [int]",1,3)); // 7
        commands.add(new Command("Role","Grabs the current user count for a role","role [role]",1,3)); // 8
        commands.add(new Command("Assign","Assign a low class role to a user","assign [role] @user",2,2)); // 9
        commands.add(new Command("Poll","Start a strawpoll with HIVE","poll [option1],[option2],[option3]",2,2)); // 10
        commands.add(new Command("Analyze","Analyze the guild channels for the date of last message","analyze",0,3)); // 11
        commands.add(new Command("Ask","Used to send a question to a special channel for the doc/staff to answer","ask [question]",1,0)); // 12
        commands.add(new Command("Admin","Get admin command menu","admin",0,1)); // 13
        commands.add(new Command("Threelawssafe","Information about the bot LaWs","threelawssafe",0,0)); // 14
        commands.add(new Command("Version","Get HIVE's current version","version",0,0)); // 15
        commands.add(new Command("Execute order 66","Fun command for star wars","execute order 66",0,0)); // 16
        commands.add(new Command("Rule34","Fun command about Rule 34","rule 34",0,0)); // 17
        commands.add(new Command("Request","Display GitHub information about HIVE","request",0,0)); // 18
        commands.add(new Command("Twitchsub","Get information about Twitch Subscriber","twitchsub",0,0)); // 19
        commands.add(new Command("Stats","Get discord server stats","stats",0,1)); // 20
        commands.add(new Command("Getdata","Get data from the data file","getdata | getdata [key]",0,3)); // 21
        commands.add(new Command("Who","Get details about a user","who @user",1,1)); // 22
        commands.add(new Command("Help","Gets command description and syntax","help [commmand]",0,0)); // 23
        commands.add(new Command("SendMarkers","Manually trigger HIVE to send channel markers for a streaming event","sendmarkers",0,2)); //24
        commands.add(new Command("GetStreamMode","Gets the current Stream Mode for passing questions and links","getstreammode",0,2)); // 25
        commands.add(new Command("SetStreamMode","Sets the current Stream Mode for passing questions and links","setstreammode",0,2)); // 26
        commands.add(new Command("Commands","Get a list of all available commands posted to the channel","commands",0,0)); // 27
        commands.add(new Command("Sponge","Turn a string into a spongy text","sponge [text]",1,1)); // 28
        commands.add(new Command("Welcome","Trigger the welcome message","welcome",0,3)); // 29
        commands.add(new Command("Uptime","See how long HIVE has been running","uptime",0,0)); // 30
        CommandData commandData = new CommandData();

    }

    public static void setStreamMode(Boolean streamMode) {
        HiveBot.streamMode = streamMode;
    }

    public static Boolean getStreamMode(){
        return HiveBot.streamMode;
    }
}

