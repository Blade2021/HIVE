package rsystems.objects;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.commands.CheckRole;
import rsystems.commands.funCommands.Order66;
import rsystems.commands.funCommands.ThreeLawsSafe;
import rsystems.commands.generic.Ping;
import rsystems.commands.karmaSystem.GetKarma;
import rsystems.commands.karmaSystem.GetPoints;
import rsystems.commands.karmaSystem.Karma;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Dispatcher extends ListenerAdapter {

    private final Set<Command> commands = ConcurrentHashMap.newKeySet();
    private final ExecutorService pool = Executors.newCachedThreadPool(newThreadFactory("command-runner", false));

    public Dispatcher() {
        this.registerCommand(new GetKarma());
        this.registerCommand(new GetPoints());
        this.registerCommand(new Karma());
        this.registerCommand(new Order66());
        this.registerCommand(new ThreeLawsSafe());
        this.registerCommand(new Ping());
        this.registerCommand(new CheckRole());

        for(Command c:commands){
            System.out.println(c.getName());
        }
    }

    public Set<Command> getCommands() {
        return Collections.unmodifiableSet(new HashSet<>(this.commands));
    }

    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        final Long authorID = event.getAuthor().getIdLong();

        // Check Blacklist for user
        if (HiveBot.sqlHandler.checkBlacklist(authorID))
            return;

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

    @Override
    public void onPrivateMessageReceived(final PrivateMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        final Long authorID = event.getAuthor().getIdLong();

        // Check Blacklist for user
        if (HiveBot.sqlHandler.checkBlacklist(authorID))
            return;

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
                                final GuildMessageReceivedEvent event) {
        this.pool.submit(() ->
        {
            try {
                final String content = this.removePrefix(alias, prefix, message);
                c.dispatch(event.getAuthor(), event.getChannel(), event.getMessage(), content, event);
            } catch (final NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
                event.getMessage().reply("**ERROR:** Bad format received").queue();
                //messageOwner(numberFormatException, c, event);
            } catch (final Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage("**There was an error processing your command!**").queue();
                //messageOwner(e, c, event);
            }
        });
    }

    private void executeCommand(final Command c, final String alias, final String prefix, final String message,
                                final PrivateMessageReceivedEvent event) {
        this.pool.submit(() ->
        {
            try {
                final String content = this.removePrefix(alias, prefix, message);
                c.dispatch(event.getAuthor(), event.getChannel(), event.getMessage(), content, event);
            } catch (final NumberFormatException numberFormatException) {
                numberFormatException.printStackTrace();
                event.getMessage().reply("**ERROR:** Bad format received").queue();
                //messageOwner(numberFormatException, c, event);
            } catch (final Exception e) {
                e.printStackTrace();
                event.getChannel().sendMessage("**There was an error processing your command!**").queue();
                //messageOwner(e, c, event);
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

}
