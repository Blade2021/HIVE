package rsystems.handlers;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.commands.debug.Test;
import rsystems.commands.moderator.ReferenceTest;
import rsystems.commands.stream.StreamMode;
import rsystems.commands.user.Commands;
import rsystems.commands.user.GetKarma;
import rsystems.objects.Command;

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

        registerCommand(new Test());
        registerCommand(new Commands());
        registerCommand(new StreamMode());
        registerCommand(new GetKarma());
        registerCommand(new ReferenceTest());

    }

    public Set<Command> getCommands() {
        return Collections.unmodifiableSet(new HashSet<>(this.commands));
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //Ignore all bots
        if (event.getAuthor().isBot()) {
            return;
        }

        if(event.getMessage().getType().isSystem()){
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
        String message = event.getMessage().getContentRaw();

        final MessageChannel channel = event.getChannel();

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
            if (c.getPermissionIndex() == null) {
                authorized = true;
            } else {
                if (HiveBot.mainGuild().getMemberById(event.getAuthor().getIdLong()) != null) {
                    try {
                        authorized = checkAuthorized(HiveBot.mainGuild().getMemberById(event.getAuthor().getIdLong()), c.getPermissionIndex());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (authorized) {

                try {
                    final String content = this.removePrefix(alias, prefix, message);
                    c.dispatch(event.getAuthor(), event.getChannel(), event.getMessage(), content, event);

                    HiveBot.database.logCommandUsage(c.getName());
                } catch (final NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                    event.getMessage().reply("**ERROR:** Bad format received").queue();
                    //messageOwner(numberFormatException, c, event);
                } catch (final Exception e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage("**There was an error processing your command!**").queue();
                    //messageOwner(e, c, event);
                }
            } else {
                event.getMessage().reply(String.format(event.getAuthor().getAsMention() + " You are not authorized for command: `%s`\nPermission Index: %d", c.getName(), c.getPermissionIndex())).queue();
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

    /**
     * Check to see if a user is authorized for a command.  Any command that doesn't have a permission index set is automatically approved.
     * @param member The member to be checked.
     * @param commandPermission The permission index of the command.  (Can be null!)
     * @return True = Authorized | False = Not Authorized
     */
    public boolean checkAuthorized(final Member member, final Integer commandPermission) throws SQLException {
        boolean authorized = false;

        Integer userAuthOverride = HiveBot.database.checkAuthOverride(member.getIdLong());
        if(userAuthOverride != null){
            String binaryString = Integer.toBinaryString(userAuthOverride);

            //Reverse the string for processing
            //Example 24 = 11000 -> 00011
            String reverseString = new StringBuilder(binaryString).reverse().toString();

            //Turn the command rank into a binary string
            //Example 8 = 1000
            String binaryIndexString = Integer.toBinaryString(commandPermission);

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
        } else {
            Map<Long, Integer> authmap = HiveBot.database.getAuthRoles();

            for (Role r : member.getRoles()) {
                if (authmap.containsKey(r.getIdLong())) {
                    int modRoleValue = authmap.get(r.getIdLong());

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
                    String binaryIndexString = Integer.toBinaryString(commandPermission);

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
        }
        return authorized;
    }
	
	public Map<String,Integer> getCommandMap(){
		
		Map<String,Integer> commandMap = new HashMap<>();
		
		for (final Command c : this.getCommands()) {
			
			commandMap.putIfAbsent(c.getName(),c.getPermissionIndex());
			
		}
		
		return commandMap;
	}

    public static Boolean isAuthorized(final Command c, final Long guildID, final Member member, final Integer permissionIndex) throws SQLException {
        boolean authorized = false;

        if (c.isOwnerOnly()) {
            if (member.getIdLong() == HiveBot.botOwnerID) {
                return true;
            } else {
                return false;
            }
        }

        if (member.hasPermission(Permission.ADMINISTRATOR)) {
            return true;
        }

        if (c.getDiscordPermission() != null) {
            if (member.getPermissions().contains(c.getDiscordPermission())) {
                return true;
            } else {
                if(c.getPermissionIndex() == null){
                    return false;
                }
            }
        }

        if ((c.getDiscordPermission() == null) && (c.getPermissionIndex() == null)) {
            return true;
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
