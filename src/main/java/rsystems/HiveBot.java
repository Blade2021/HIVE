package rsystems;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import rsystems.adapters.*;
import rsystems.commands.*;
import rsystems.events.DocStream;
import rsystems.events.Mentionable;
import rsystems.events.WelcomeWagon;
import rsystems.handlers.*;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Logger;

public class HiveBot{
    public static String prefix = Config.get("prefix");
    public static String helpPrefix = Config.get("helpprefix");
    public static String version = "0.17.1";
    public static String restreamID = Config.get("restreamid");
    public static DataFile dataFile = new DataFile();
    private static Boolean streamMode = false;
    public static String docDUID = Config.get("docDUID");


    // Commands array
    public static ArrayList<Command> commands = new ArrayList<Command>();
    //Reference array
    public static ArrayList<Reference> references = new ArrayList<Reference>();
    // Message Check Class
    public static MessageCheck messageCheck = new MessageCheck();
    // Load Reference data
    public static ReferenceLoader referenceLoader = new ReferenceLoader();

    public static HallMonitor hallMonitor = new HallMonitor();

    //Initiate Logger
    public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) throws LoginException, IOException {
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
        api.addEventListener(hallMonitor);
        api.addEventListener(new Mentionable());
        api.addEventListener(new Info());
        api.addEventListener(new LinkGrabber());
        api.addEventListener(new Notify());
        api.addEventListener(new Page());
        api.addEventListener(new Ping());
        api.addEventListener(new Poll());
        api.addEventListener(new ReferenceTrigger());
        api.addEventListener(new Role());
        //api.addEventListener(new Say());
        api.addEventListener(new Shutdown());
        api.addEventListener(new Status());
        api.addEventListener(new Twitch());
        api.addEventListener(new TwitchSub());
        api.addEventListener(new Who());
        api.addEventListener(new DocStream());
        api.addEventListener(new Analyze());
        api.addEventListener(new WelcomeWagon());
        api.addEventListener(new Help());
        api.addEventListener(new Janitor());
        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.playing(Config.get("activity")));

        for(Command c:commands){
            System.out.println(c.getCommand());
        }

        try{
            MyLogger.setup();
        }catch(IOException e){
            e.printStackTrace();
        }

        //AdminCommand shutdown = new AdminCommand("shutdown","shutdown","Shuts down the bot",2, 0);

        /*TwitchHandler twitchHandler = new TwitchHandler();
        twitchHandler.registerFeatures();
        twitchHandler.start();

         */

        //Create commands
        commands.add(new Command("Shutdown"));  //0
        commands.add(new Command("Notify"));  //1
        commands.add(new Command("Info")); // 2
        commands.add(new Command("Who")); // 3
        commands.add(new Command("Ping")); // 4
        commands.add(new Command("Code")); // 5
        commands.add(new Command("Page")); // 6
        commands.add(new Command("Clear")); // 7
        commands.add(new Command("Role")); // 8
        commands.add(new Command("Assign")); // 9
        commands.add(new Command("Poll")); // 10
        commands.add(new Command("Analyze")); // 11
        commands.add(new Command("Ask")); // 12
        commands.add(new Command("Admin")); // 13
        commands.add(new Command("Threelawssafe")); // 14
        commands.add(new Command("Version")); // 15
        commands.add(new Command("Execute order 66")); // 16
        commands.add(new Command("Rule34")); // 17
        commands.add(new Command("Request")); // 18
        commands.add(new Command("Twitchsub")); // 19
        commands.add(new Command("Stats")); // 20
        commands.add(new Command("Getdata")); // 21
        commands.add(new Command("Who")); // 22
        commands.add(new Command("Help")); // 23
        commands.add(new Command("SendMarkers")); //24
        commands.add(new Command("GetStreamMode")); // 25
        commands.add(new Command("SetStreamMode")); // 26
        commands.add(new Command("Commands")); // 27
        commands.add(new Command("Sponge")); // 28
        commands.add(new Command("Welcome")); // 29
        commands.add(new Command("Uptime")); // 30
        commands.add(new Command("updateReference")); // 31
        commands.add(new Command("reloadAll")); // 32
        commands.add(new Command("appendData")); // 33
        commands.add(new Command("writeData")); // 34
        commands.add(new Command("removeData")); // 35
        commands.add(new Command("Changelog")); // 36
        commands.add(new Command("ReferenceList")); // 37
        commands.add(new Command("Resign")); //38
        commands.add(new Command("getAssignableRoles")); //39

        CommandData commandData = new CommandData();

        try {
            // Wait for discord jda to completely load
            api.awaitReady();

            //Load Tasks
            AutoStatus task1 = new AutoStatus(api,"Current Version: " + HiveBot.version);
            AutoStatus task2 = new AutoStatus(api,Config.get("activity"));
            UptimeStatus task3 = new UptimeStatus(api);
            HoneyStatus task4 = new HoneyStatus(api,api.getGuildById("469330414121517056"));


            // Schedule Tasks
            Timer timer = new Timer();
            timer.schedule(task1, 30000, 120000); // Current version
            timer.schedule(task2,60000,120000); // Default status
            timer.schedule(task3,90000,120000); // Uptime
            timer.schedule(task4,120000,120000); // Honey

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void setStreamMode(Boolean streamMode) {
        HiveBot.streamMode = streamMode;
    }

    public static Boolean getStreamMode(){
        return HiveBot.streamMode;
    }

    public static void reloadAll(){
        HiveBot.dataFile.loadDataFile();
        HiveBot.messageCheck.reloadData();
        HiveBot.referenceLoader.updateData();

        CommandData commandData = new CommandData();
    }
}

