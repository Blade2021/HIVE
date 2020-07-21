package rsystems;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import rsystems.adapters.*;
import rsystems.commands.*;
import rsystems.events.*;
import rsystems.handlers.*;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.logging.Logger;

public class HiveBot{
    public static String prefix = Config.get("prefix");
    public static String altPrefix = Config.get("altprefix");
    public static String helpPrefix = Config.get("helpprefix");
    public static String karmaPrefixPositive = Config.get("KARMA_PREFIX_POS");
    public static String karmaPrefixNegative = Config.get("KARMA_PREFIX_NEG");
    public static String version = "0.17.7";
    public static String restreamID = Config.get("restreamid");
    public static DataFile dataFile = new DataFile();
    private static Boolean streamMode = false;
    public static String docDUID = Config.get("docDUID");
    public static String botSpamChannel = Config.get("botSpamChannelID");

    public static Guild docGuild = null;
    public static Guild steelVein = null;


    // Commands array
    public static ArrayList<Command> commands = new ArrayList<Command>();
    //Extended Reference array
    public static ArrayList<ExtendedReference> extendedReferences = new ArrayList<>();
    //Reference array
    public static ArrayList<Reference> references = new ArrayList<>();
    // Message Check Class
    public static MessageCheck messageCheck = new MessageCheck();
    // Load Reference data
    public static ReferenceLoader referenceLoader = new ReferenceLoader();
    //SQL Handler
    public static SQLHandler sqlHandler = new SQLHandler(Config.get("DATABASE_URL"));
    //Karma SQL Handler
    public static KarmaSQLHandler karmaSQLHandler = new KarmaSQLHandler(Config.get("DATABASE_URL"));
    //Suggestion SQL Handler
    public static SuggestionHandler suggestionHandler = new SuggestionHandler(
            Config.get("DATABASE_URL"),
            HiveBot.dataFile.getData("SuggestionsRequestsChannel").toString(),
            HiveBot.dataFile.getData("SuggestionsReviewChannel").toString(),
            HiveBot.dataFile.getData("SuggestionsPostChannel").toString()
    );

    //Static handlers
    // Hall Monitor Object
    public static HallMonitor hallMonitor = new HallMonitor();
    // Nickname handler
    public static Nickname nickname = new Nickname();


    //Initiate Loggers
    public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public final static Logger karmaLogger = Logger.getLogger("karmaLogger");

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
        api.addEventListener(new DatabaseCommands());
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
        api.addEventListener(new Say());
        api.addEventListener(new LocalPoll());
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
        api.addEventListener(new OnlineStatusListener());
        api.addEventListener(new KarmaInterface());
        api.addEventListener(new ReactionListener());
        api.addEventListener(new GratitudeListener());
        api.addEventListener(new SuggestionInterface());
        api.addEventListener(new Mute());
        api.addEventListener(nickname);
        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.playing(Config.get("activity")));

        for(Command c:commands){
            System.out.println(c.getCommand());
        }

        try{
            MyLogger.setup();
            MyLogger.setup("karmaLogger");
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
        commands.add(new Command("WhoIs")); // 22
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
        commands.add(new Command("localPoll")); //40
        commands.add(new Command("CheckDB")); // 41
        commands.add(new Command("getDBUsers")); // 42
        commands.add(new Command("setDBUsername")); // 43
        commands.add(new Command("removeDBUser")); // 44
        commands.add(new Command("getLSDate")); // 45
        commands.add(new Command("getDBData")); // 46
        commands.add(new Command("Test")); // 47
        commands.add(new Command("addKarma")); // 48
        commands.add(new Command("getKarma")); // 49
        commands.add(new Command("setPoints")); // 50
        commands.add(new Command("getPoints")); // 51
        commands.add(new Command("setKarma")); // 52
        commands.add(new Command("++")); // 53
        commands.add(new Command("getUserKarma")); // 54
        commands.add(new Command("getUserPoints")); // 55
        commands.add(new Command("getDBSize")); // 56
        commands.add(new Command("--")); // 57
        commands.add(new Command("masterOverride")); // 58
        commands.add(new Command("pollOnlineUsers")); // 59
        commands.add(new Command("deleteUser")); // 60
        commands.add(new Command("getTopTen")); //61
        commands.add(new Command("karma")); //62
        commands.add(new Command("getDate")); //63
        commands.add(new Command("getKUserInfo")); //64
        commands.add(new Command("cleanse")); //65
        commands.add(new Command("nick")); //66
        commands.add(new Command("optout")); //67
        commands.add(new Command("updateKarmaUsers")); //68
        commands.add(new Command("getActiveKarmaUsers")); //69
        commands.add(new Command("karmaShort")); //70
        commands.add(new Command("suggest")); //71
        commands.add(new Command("suggestionAccept")); //72
        commands.add(new Command("suggestionReject")); //73
        commands.add(new Command("suggestionStatus")); //74
        commands.add(new Command("suggestionApprove")); //75
        commands.add(new Command("suggestionDeny")); //76
        commands.add(new Command("suggestionOverrideMessage")); //77
        commands.add(new Command("suggestionsList")); //78
        commands.add(new Command("mute")); //79
        commands.add(new Command("muteChannel")); //80
        CommandData commandData = new CommandData();

        try {
            // Wait for discord jda to completely load
            api.awaitReady();

            docGuild = api.getGuildById("469330414121517056");
            steelVein = api.getGuildById("386701951662030858");

            //Load Tasks
            AutoStatus task1 = new AutoStatus(api,"Current Version: " + HiveBot.version);
            AutoStatus task2 = new AutoStatus(api,Config.get("activity"));
            UptimeStatus task3 = new UptimeStatus(api);
            HoneyStatus task4 = new HoneyStatus(api,api.getGuildById("469330414121517056"));

            AutoRemove autoRemoveTask = new AutoRemove();
            AddKarmaPoints addKarmaPoints = new AddKarmaPoints(docGuild);


            // Schedule Tasks
            Timer timer = new Timer();
            timer.schedule(task1, 30000, 120000); // Current version
            timer.schedule(task2,60000,120000); // Default status
            timer.schedule(task3,90000,120000); // Uptime
            timer.schedule(task4,120000,120000); // Honey

            Timer serverTaskTimer = new Timer();
            //Schedule AutoRemove task to run every 6 hours
            serverTaskTimer.schedule(autoRemoveTask,30000,21600000);
            serverTaskTimer.schedule(addKarmaPoints,600000,21600000);


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

