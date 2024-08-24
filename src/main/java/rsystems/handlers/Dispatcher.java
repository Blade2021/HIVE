package rsystems.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.commands.moderation.Shutdown;
import rsystems.commands.moderation.*;
import rsystems.commands.stream.StreamMode;
import rsystems.commands.stream.StreamVerify;
import rsystems.commands.user.*;
import rsystems.commands.utility.*;
import rsystems.events.GratitudeListener;
import rsystems.objects.AutoResponse;
import rsystems.objects.Command;
import rsystems.objects.Reference;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Dispatcher extends ListenerAdapter {

    private final Set<Command> commands = ConcurrentHashMap.newKeySet();
    private final ExecutorService pool = Executors.newCachedThreadPool(newThreadFactory("command-runner", false));

    public Dispatcher() {

        // User Commands
        registerCommand(new Commands());
        registerCommand(new Led());
        registerCommand(new GetKarma());
        registerCommand(new Order66());
        //registerCommand(new Search());
        registerCommand(new Help());
        registerCommand(new ThreeLawsSafe());
        registerCommand(new Mini());

        // Utility Commands
        registerCommand(new Ping());
        registerCommand(new PowerCal());
        registerCommand(new LedList());
        registerCommand(new ReferenceList());
        registerCommand(new Halloween());
        registerCommand(new Christmas());

        // Moderation Commands
        registerCommand(new Clear());
        registerCommand(new Reload());
        registerCommand(new Cleanse());
        registerCommand(new ReferenceTester());
        registerCommand(new Shutdown());

        //registerCommand(new GetTopTen());


        // Stream Commands
        //registerCommand(new StreamMode());
        //registerCommand(new StreamVerify());

        //registerCommand(new Meltdown());


        // Dev Only
        //registerCommand(new Test());
        //registerCommand(new Test2());
    }

    public Set<Command> getCommands() {
        return Collections.unmodifiableSet(new HashSet<>(this.commands));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.isFromGuild()) {
            final MessageChannel channel = event.getChannel();

            //Ignore all bots
            if (event.getAuthor().isBot()) {

                //Check for Stream Chat channel
                if (channel.getIdLong() == HiveBot.streamHandler.getStreamChatChannelID()) {

                    if (HiveBot.streamHandler.isStreamActive()) {
                        HiveBot.streamHandler.parseMessage(event);
                    }
                }
                return;
            }

            if (event.getMessage().getType().isSystem()) {
                return;
            }

            final Long authorID = event.getAuthor().getIdLong();

            // Check Blacklist for user
            try {
                if (HiveBot.database.checkBlacklist(authorID))
                    return;
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            final String prefix = Config.get("bot_prefix");
            final String message = event.getMessage().getContentRaw();


            // Check for commands
            if (message.toLowerCase().startsWith(prefix.toLowerCase())) {
                for (final Command c : this.getCommands()) {
                    if (message.toLowerCase().startsWith(prefix.toLowerCase() + c.getName().toLowerCase() + ' ') || message.equalsIgnoreCase(prefix + c.getName())) {
                        this.executeCommand(c, c.getName(), prefix, message, event);
                        return;
                    } else {
                        for (final String alias : c.getAliases()) {
                            if (message.toLowerCase().startsWith(prefix.toLowerCase() + alias.toLowerCase() + ' ') || message.equalsIgnoreCase(prefix + alias)) {
                                this.executeCommand(c, alias, prefix, message, event);
                                return;
                            }
                        }
                    }
                }

                //Command not found


                //CHECK FOR REFERENCES

                try {
                    if (HiveBot.database.checkForReference(message.toLowerCase().substring(1))) {
                        //Reference was found

                        Reference foundReference = HiveBot.database.getReference(message.toLowerCase().substring(1));
                        if (event.getMessage().getMessageReference() != null) {

                            Message originalMessage = event.getMessage().getReferencedMessage();
                            originalMessage.replyEmbeds(foundReference.createEmbed()).queue();

                        } else {
                            event.getMessage().replyEmbeds(foundReference.createEmbed()).queue();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    ExceptionHandler.notifyException(e, this.getClass().getName());
                }

                //References not found

            } else {
                try{
                    AutoResponse ar = HiveBot.database.checkForAutoResponse(event.getMessage().getContentDisplay(),event.getChannel().getIdLong());
                    if(ar != null){

                        EmbedBuilder builder = new EmbedBuilder();
                        builder.setColor(HiveBot.getColor(HiveBot.colorType.GENERIC))
                                        .setDescription(ar.getResponse())
                                .setTitle(ar.getTitle());
                        event.getMessage().replyEmbeds(builder.build()).queue();
                        HiveBot.database.autoResponse_setTimestamp(ar.getName(),event.getChannel().getIdLong());
                        return;
                    }

                } catch (SQLException e) {
                    ExceptionHandler.notifyException(e, this.getClass().getName());
                    e.printStackTrace();
                }
            }
            // Prefix not found

            //Check for Gratitude
            for (String trigger : HiveBot.gratitudeListener.getTriggers()) {
                if (event.getMessage().getContentDisplay().toLowerCase().contains(trigger)) {
                    GratitudeListener.gratitudeMessageReceived(event);
                    return;
                }
            }


            // No gratitude triggers found
        } else {
            // Message did not come from Guild

            if (event.getMessage().toString().toLowerCase().contains(HiveBot.prefix + "optout")) {
                try {
                    Integer optStatus = HiveBot.database.setOptStatus(event.getAuthor().getIdLong());

                    if (optStatus != null) {

                        if (optStatus == 0) {
                            event.getMessage().reply("You will now be able to be messaged by me when someone sends you a Coffee").queue();
                        } else {
                            event.getMessage().reply("You will no longer receive any more messages from me.  Warm Regards.").queue();
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public boolean registerCommand(final Command command) {
        if (command.getName().contains(" "))
            throw new IllegalArgumentException("Name must not have spaces!");
        if (this.commands.stream().map(Command::getName).anyMatch(c -> command.getName().equalsIgnoreCase(c)))
            return false;
        this.commands.add(command);
        return true;
    }

    private void executeCommand(final Command c, final String alias, final String prefix, final String message,
                                final MessageReceivedEvent event) {
        this.pool.submit(() ->
        {
            boolean authorized = false;
            if ((!c.isOwnerOnly()) && (c.getPermissionIndex() == null) && (c.getDiscordPermission() == null)) {
                authorized = true;
            } else {

                if (event.getMember() != null) {
                    try {
                        authorized = isAuthorized(c, event.getGuild().getIdLong(), event.getMember(), c.getPermissionIndex());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        ExceptionHandler.notifyException(e, this.getClass().getName());
                    }
                }
            }
            if (authorized) {

                try {
                    final String content = this.removePrefix(alias, prefix, message);
                    c.dispatch(event.getAuthor(), event.getChannel(), event.getMessage(), content, event);

                    Logger logger = LoggerFactory.getLogger(Dispatcher.class);
                    logger.info("{} called by {} [{}]", c.getName(), event.getAuthor().getAsTag(), event.getAuthor().getIdLong());

                    HiveBot.database.logCommandUsage(c.getName());
                } catch (final NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                    event.getMessage().reply("**ERROR:** Bad format received").queue();
                    //messageOwner(numberFormatException, c, event);
                } catch (final Exception e) {
                    e.printStackTrace();
                    ExceptionHandler.notifyException(e, this.getClass().getName());
                    event.getChannel().sendMessage("**There was an error processing your command!**").queue();
                }
            } else {

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(HiveBot.getColor(HiveBot.colorType.ERROR));
                embedBuilder.setTitle("Unauthorized Request");
                embedBuilder.setDescription(String.format("%s\n You are not authorized for command: `%s`", event.getMember().getAsMention(), c.getName()));

                if (c.getPermissionIndex() != null) {
                    embedBuilder.addField("Mod Permission", c.getPermissionIndex().toString(), true);
                }

                if (c.getDiscordPermission() != null) {
                    embedBuilder.addField("Discord Permission", c.getDiscordPermission().getName(), true);
                }

                embedBuilder.setFooter(event.getAuthor().getId());

                event.getMessage().replyEmbeds(embedBuilder.build()).queue();
            }
        });
    }

    private String removePrefix(final String commandName, final String prefix, String content) {
        content = content.substring(commandName.length() + prefix.length());
        if (content.startsWith(" "))
            content = content.substring(1);
        return content;
    }

    public static ThreadFactory newThreadFactory(String threadName, boolean isdaemon) {
        return (r) ->
        {
            Thread t = new Thread(r, threadName);
            t.setDaemon(isdaemon);
            return t;
        };
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        Command.removeResponses(event.getChannel(), event.getMessageIdLong());
    }

    public Map<String, Integer> getCommandMap() {

        Map<String, Integer> commandMap = new HashMap<>();

        for (final Command c : this.getCommands()) {

            commandMap.putIfAbsent(c.getName(), c.getPermissionIndex());

        }

        return commandMap;
    }

    public static Boolean isAuthorized(final Command c, final Long guildID, final Member member, final Integer permissionIndex) throws SQLException {
        boolean authorized = false;

        if (c.isOwnerOnly()) {
            return member.getIdLong() == HiveBot.botOwnerID;
        }

        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }

        if (c.getDiscordPermission() != null) {
            if (member.getPermissions().contains(c.getDiscordPermission())) {
                return true;
            } else {
                if (c.getPermissionIndex() == null) {
                    return false;
                }
            }
        }

        if ((c.getDiscordPermission() == null) && (c.getPermissionIndex() == null)) {
            return true;
        }


        final Map<Long, Integer> authmap = HiveBot.database.getModRoles();
        for (Role role : member.getRoles()) {

            Long roleID = role.getIdLong();

            if (authmap.containsKey(roleID)) {
                int modRoleValue = authmap.get(roleID);

                /*
                Form a binary string based on the permission level integer found.
                Example: 24 = 11000
                 */
                String binaryString = Integer.toBinaryString(modRoleValue);

                //Reverse the string for processing
                //Example 24 = 11000 -> 00011
                String reverseString = new StringBuilder(binaryString).reverse().toString();

                //Turn the command rank into a binary string
                //Example 8 = 1000
                String binaryIndexString = Integer.toBinaryString(permissionIndex);

                //Reverse the string for lookup
                //Example 8 = 1000 -> 0001
                String reverseLookupString = new StringBuilder(binaryIndexString).reverse().toString();

                int realIndex = reverseLookupString.indexOf('1');

                char indexChar = '0';
                try {

                    indexChar = reverseString.charAt(realIndex);

                } catch (IndexOutOfBoundsException e) {

                } finally {
                    if (indexChar == '1') {
                        authorized = true;
                    }
                }

                if (authorized)
                    break;
            }
        }

        return authorized;
    }

    public static Boolean checkAuthorized(final Long guildID, final Member member, final Integer permissionIndex, final Permission discordPermission) throws SQLException {
        boolean authorized = false;

        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }

        if (discordPermission != null) {
            if (member.getPermissions().contains(discordPermission)) {
                return true;
            } else {
                if (permissionIndex == null) {
                    return false;
                }
            }
        }

        Map<Long, Integer> authmap = HiveBot.database.getModRoles();
        for (Role role : member.getRoles()) {

            Long roleID = role.getIdLong();

            if (authmap.containsKey(roleID)) {
                int modRoleValue = authmap.get(roleID);

                /*
                Form a binary string based on the permission level integer found.
                Example: 24 = 11000
                 */
                String binaryString = Integer.toBinaryString(modRoleValue);

                //Reverse the string for processing
                //Example 24 = 11000 -> 00011
                String reverseString = new StringBuilder(binaryString).reverse().toString();

                //Turn the command rank into a binary string
                //Example 8 = 1000
                String binaryIndexString = Integer.toBinaryString(permissionIndex);

                //Reverse the string for lookup
                //Example 8 = 1000 -> 0001
                String reverseLookupString = new StringBuilder(binaryIndexString).reverse().toString();

                int realIndex = reverseLookupString.indexOf('1');

                char indexChar = '0';
                try {

                    indexChar = reverseString.charAt(realIndex);

                } catch (IndexOutOfBoundsException e) {

                } finally {
                    if (indexChar == '1') {
                        authorized = true;
                    }
                }

                if (authorized)
                    break;
            }
        }
        return authorized;

    }

}
