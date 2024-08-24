package rsystems.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsystems.HiveBot;
import rsystems.slashCommands.tickets.CloseTicket;
import rsystems.slashCommands.tickets.Ticket;
import rsystems.slashCommands.utility.*;
import rsystems.objects.SlashCommand;
import rsystems.slashCommands.moderation.*;
import rsystems.slashCommands.user.*;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import static rsystems.HiveBot.database;

public class SlashCommandDispatcher extends ListenerAdapter {

    private final Set<SlashCommand> slashCommands = ConcurrentHashMap.newKeySet();
    private final ExecutorService pool = Executors.newCachedThreadPool(newThreadFactory("slashCommand-runner", false));
    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(10);

    private final String[] overrideCommands = {"LED"};

    public SlashCommandDispatcher() {

        // User Commands
        registerCommand(new GetKarma());
        registerCommand(new Commands());
        //registerCommand(new StreamPoints());
        //registerCommand(new Mini());
        //registerCommand(new CreatePoll());
        registerCommand(new Here());

        // Utility Commands
        registerCommand(new Help());
        registerCommand(new Led());
        registerCommand(new LedList());
        registerCommand(new SetYoutubeLink());
        registerCommand(new GetPixelTubeList());

        // Moderation Commands
        registerCommand(new LedControl());
        registerCommand(new Unpin());
        registerCommand(new Activity());
        registerCommand(new Who());
        //registerCommand(new StreamMarker());
        registerCommand(new SubmitToken());
        registerCommand(new ChannelStats());
        registerCommand(new Embed());

        registerCommand(new LibraryAdd());
        registerCommand(new LibraryModify());

        // Dev Commands

        //registerCommand(new Block());
        registerCommand(new ThreadIt());
        registerCommand(new SendACoffee());
        registerCommand(new Ticket());
        registerCommand(new CloseTicket());


    }

    public Set<SlashCommand> getCommands() {
        return Collections.unmodifiableSet(new HashSet<>(this.slashCommands));
    }


    public void onSlashCommandInteraction(final SlashCommandInteractionEvent event) {

        for (final SlashCommand c : this.getCommands()) {
            if (event.getName().equalsIgnoreCase(c.getName())) {
                this.executeCommand(c, event.getCommandString(), event);
                return;
            }
        }
    }


    public boolean registerCommand(final SlashCommand command) {
        if (command.getName().contains(" "))
            throw new IllegalArgumentException("Name must not have spaces!");
        if (this.slashCommands.stream().map(SlashCommand::getName).anyMatch(c -> command.getName().equalsIgnoreCase(c)))
            return false;
        this.slashCommands.add(command);
        return true;
    }

    public SlashCommand getCommandByName(String name){
        for(SlashCommand s:this.slashCommands){
            if(s.getName().equalsIgnoreCase(name)){
                return s;
            }
        }

        return null;
    }

    private void executeCommand(final SlashCommand c, final String message,
                                final SlashCommandInteractionEvent event) {
        this.pool.submit(() ->
        {

            boolean authorized = false;
            if ((c.getPermissionIndex() == null) && (c.getDiscordPermission() == null)) {
                authorized = true;
            } else {
                try {
                    authorized = isAuthorized(c, event.getGuild().getIdLong(), event.getMember(), c.getPermissionIndex());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (authorized) {
                try {
                    final String content = message;
                    c.dispatch(event.getUser(), event.getChannel(), content, event);

                    Logger logger = LoggerFactory.getLogger(Dispatcher.class);
                    logger.info("{} called by {} [{}]",c.getName(),event.getUser().getAsTag(),event.getUser().getIdLong());

                    database.logCommandUsage(c.getName());
                } catch (final NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                    event.getHook().sendMessage("**ERROR:** Bad format received").queue();
                    //messageOwner(event, c, numberFormatException);
                } catch (final Exception e) {
                    event.getHook().sendMessage("**There was an error processing your command!**").queue();
                    e.printStackTrace();
                    ExceptionHandler.notifyException(e,this.getClass().getName());
                }
            } else {

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));
                embedBuilder.setTitle("Unauthorized Request");
                embedBuilder.setDescription(String.format(" You are not authorized for command: `%s`", c.getName()));

                if (c.getPermissionIndex() != null) {
                    embedBuilder.addField("Mod Permission",c.getPermissionIndex().toString(),true);
                }

                if (c.getDiscordPermission() != null) {
                    embedBuilder.addField("Discord Permission",c.getDiscordPermission().getName(),true);
                }

                event.replyEmbeds(embedBuilder.build()).setEphemeral(false).queue();
            }
        });
    }


    public static ThreadFactory newThreadFactory(String threadName, boolean isdaemon) {
        return (r) ->
        {
            Thread t = new Thread(r, threadName);
            t.setDaemon(isdaemon);
            return t;
        };
    }


    public static Boolean isAuthorized(final SlashCommand c, final Long guildID, final Member member, final Integer permissionIndex) throws SQLException {
        boolean authorized = false;

        if (c.isOwnerOnly()) {
            return member.getIdLong() == HiveBot.botOwnerID;
        }

        if(member.hasPermission(Permission.ADMINISTRATOR)){
            return true;
        }

        if(c.getDiscordPermission() != null){
            if(member.getPermissions().contains(c.getDiscordPermission())){
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
                    System.out.println("Index out of bounds");
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


    public void submitGuildCommands(final Guild guild){


            guild.retrieveCommands().queue(commandsList -> {

                ArrayList<String> updateList = new ArrayList<>();

                try {
                    updateList = database.getList("PushUpdateCommand","CommandName");

                    for(String s:this.overrideCommands){
                        if(!updateList.contains(s)){
                            updateList.add(s);
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }


                // DELETE ANY LINGERING COMMANDS THAT ARE NOT PART OF THE APPLICATION ANYMORE FROM THE GUILD COMMAND CACHE
                for(net.dv8tion.jda.api.interactions.commands.Command command:commandsList){
                    Boolean cmdFound = false;

                    for(SlashCommand slashCommand: this.slashCommands){
                        if(slashCommand.getName().equalsIgnoreCase(command.getName())){
                            cmdFound = true;
                            break;
                        }
                    }

                    if(!cmdFound){
                        System.out.printf("REMOVING %s FROM GLOBAL (Command not found in SET)%n",command.getName());
                        command.delete().queue();
                    }
                }

                // ADD ANY MISSING COMMANDS TO THE GUILD
                for(SlashCommand slashCommand: this.slashCommands){

                    if(updateList.contains(slashCommand.getName())){
                        guild.upsertCommand(slashCommand.getCommandData()).queue(success -> {
                            System.out.printf("UPDATED COMMAND: %s FOR GLOBAL  NEW ID: %d%n", success.getName(), success.getIdLong());
                        });
                    } else {

                        Boolean cmdFound = false;
                        for (net.dv8tion.jda.api.interactions.commands.Command command : commandsList) {
                            if (slashCommand.getName().equalsIgnoreCase(command.getName())) {
                                cmdFound = true;
                                break;
                            }
                        }

                        if (!cmdFound) {
                            System.out.printf("DIDN'T FIND COMMAND: %s FOR GUILD: %d%n", slashCommand.getName(),guild.getIdLong());

                            guild.upsertCommand(slashCommand.getCommandData()).queueAfter(5, TimeUnit.SECONDS, success -> {
                                System.out.printf("UPSERT COMMAND: %s FOR GUILD: %d  NEW ID: %d%n", success.getName(),guild.getIdLong(), success.getIdLong());
                            });
                        }
                    }
                }

            });

    }

    public void submitGlobalCommands(final JDA jda){

        jda.retrieveCommands().queue(commandsList -> {

            ArrayList<String> updateList = new ArrayList<>();

            try {
                updateList = database.getList("PushUpdateCommand","CommandName");
            } catch (SQLException e) {
                e.printStackTrace();
            }


            // DELETE ANY LINGERING COMMANDS THAT ARE NOT PART OF THE APPLICATION ANYMORE FROM THE GUILD COMMAND CACHE
            for(net.dv8tion.jda.api.interactions.commands.Command command:commandsList){
                Boolean cmdFound = false;

                for(SlashCommand slashCommand: this.slashCommands){
                    if(slashCommand.getName().equalsIgnoreCase(command.getName())){
                        cmdFound = true;
                        break;
                    }
                }

                if(!cmdFound){
                    System.out.printf("REMOVING %s FROM GLOBAL (Command not found in SET)%n",command.getName());
                    command.delete().queue();
                }
            }

            // ADD ANY MISSING COMMANDS TO THE GUILD
            for(SlashCommand slashCommand: this.slashCommands){

                if(updateList.contains(slashCommand.getName())){
                    jda.upsertCommand(slashCommand.getCommandData()).queue(success -> {
                        System.out.printf("UPDATED COMMAND: %s FOR GLOBAL  NEW ID: %d%n", success.getName(), success.getIdLong());
                    });
                } else {

                    Boolean cmdFound = false;
                    for (net.dv8tion.jda.api.interactions.commands.Command command : commandsList) {
                        if (slashCommand.getName().equalsIgnoreCase(command.getName())) {
                            cmdFound = true;
                            break;
                        }
                    }

                    if (!cmdFound) {
                        System.out.printf("DIDN'T FIND COMMAND: %s FOR GLOBAL%n", slashCommand.getName());

                        jda.upsertCommand(slashCommand.getCommandData()).queueAfter(5, TimeUnit.SECONDS, success -> {
                            System.out.printf("UPSERT COMMAND: %s FOR GLOBAL  NEW ID: %d%n", success.getName(), success.getIdLong());
                        });
                    }
                }
            }

        });

    }

}
