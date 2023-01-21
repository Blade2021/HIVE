package rsystems;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.event.Event;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.JDAImpl;
//import net.twasi.obsremotejava.OBSRemoteController;
import rsystems.events.*;
import rsystems.handlers.*;
import rsystems.objects.DBPool;
import rsystems.objects.StreamHandler;
import rsystems.objects.TwitchBot;
import rsystems.tasks.*;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Logger;

public class HiveBot {
    public static String prefix = Config.get("prefix");
    public static int activityStatusIndex = 0;

    public static Long botOwnerID = Long.valueOf(Config.get("owner_ID"));

    public static DBPool dbPool = new DBPool(Config.get("DATABASE_HOST"), Config.get("DATABASE_USER"), Config.get("DATABASE_PASS"));
    public static SQLHandler database = new SQLHandler(dbPool.getPool());
    public static KarmaSQLHandler karmaSQLHandler = new KarmaSQLHandler(dbPool.getPool());

    public static Dispatcher dispatcher;
    public static SlashCommandDispatcher slashCommandDispatcher;
    public static GratitudeListener gratitudeListener;
    public static StreamHandler streamHandler;
    public static TwitchBot twitchBot;

    public static ReferenceHandler referenceHandler = new ReferenceHandler();

    public static JDAImpl jda = null;

    public static OBSRemoteController obsRemoteController = null;

    public static Guild mainGuild() {
        return jda.getGuildById(Config.get("GUILD_ID"));
    }

    public static Map<Long, ArrayList<String>> emojiPerkMap = new HashMap<>();

    public static boolean debug = Boolean.parseBoolean(Config.get("DEBUG"));

    private static final Map<colorType, String> colorMap = new HashMap<>();

    //Initiate Loggers
    public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) throws LoginException {
        JDA api = JDABuilder.createDefault(Config.get("token"))
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableCache(CacheFlag.ACTIVITY)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build();

        // DISPATCHERS
        api.addEventListener(dispatcher = new Dispatcher());
        api.addEventListener(slashCommandDispatcher = new SlashCommandDispatcher());

        // EVENT LISTENERS
        api.addEventListener(gratitudeListener = new GratitudeListener());
        api.addEventListener(streamHandler = new StreamHandler());
        api.addEventListener(new GuildStateListener());
        api.addEventListener(new ButtonStateListener());
        api.addEventListener(new MessageEventListener());
        api.addEventListener(new MemberStateListener());
        api.addEventListener(new ModalEventListener());

        api.getPresence().setStatus(OnlineStatus.ONLINE);
        api.getPresence().setActivity(Activity.playing(Config.get("activity")));


        loadColorMap();

        try {
            // Wait for discord jda to completely load
            api.awaitReady();
            jda = (JDAImpl) api;

            obsRemoteController = OBSRemoteController.builder()
                    .host(Config.get("OBS-ADDRESS"))
                    .port(Integer.parseInt(Config.get("OBS-PORT")))
                    .password(Config.get("OBS-KEY"))
                    .connectionTimeout(30)
                    .build();
            //obsRemoteController.connect();

            referenceHandler.loadReferences();

            api.getGuilds().forEach(guild -> {
                slashCommandDispatcher.submitGuildCommands(guild);


                guild.retrieveActiveThreads().queue(threads -> {
                    for (ThreadChannel thread : threads) {
                        if(!thread.isJoined()) {
                            thread.join().queue();
                        }
                    }
                });

            });

            //HiveBot.authMap.putIfAbsent(Long.valueOf("620805075190677514"),65535);
            try {
                HiveBot.database.loadPerkEmojis();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new LedListCheck(), 1000 * 60 * 5, 1000 * 60 * 5);

            timer.schedule(new AddKarmaPoints(), 600000, 21600000);
            timer.schedule(new Newcomer(), 60000, 21600000);
            timer.scheduleAtFixedRate(new CheckRequests(),10000,1000);
            timer.scheduleAtFixedRate(new BotActivity(), 30000, 30000);
            timer.scheduleAtFixedRate(new CheckDatabase(), 60000, 300000);
            //timer.scheduleAtFixedRate(new GrabKarmaTopThree(), 3000, 360000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (Config.get("TWITCH_ENABLED").equalsIgnoreCase("TRUE")) {
            twitchBot = new TwitchBot();
        }

    }

    public static Color getColor(colorType colorType) {
        if (colorMap.containsKey(colorType)) {
            return Color.decode(colorMap.get(colorType));
        } else {
            return Color.decode(colorMap.get(HiveBot.colorType.GENERIC));
        }
    }

    private static void loadColorMap() {
        colorMap.putIfAbsent(colorType.NOTIFICATION, "#EC8800");
        colorMap.putIfAbsent(colorType.USER, "#40F040");
        colorMap.putIfAbsent(colorType.GENERIC, "#37B9FF");
        colorMap.putIfAbsent(colorType.ERROR, "#FF3737");
        colorMap.putIfAbsent(colorType.NOVA, "#F5661A");
        colorMap.putIfAbsent(colorType.STREAM, "#8B40F0");
        colorMap.putIfAbsent(colorType.FRUIT, "#FF6145");
    }

    public enum colorType {
        NOTIFICATION, USER, GENERIC, ERROR, NOVA, STREAM, FRUIT
    }

    public static String getPrefix() {
        return prefix;
    }
}

