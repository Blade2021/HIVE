package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.LOGGER;

public class LocalPoll extends ListenerAdapter {

    private Map<String, ArrayList<String>> pollMap = new HashMap<>(); //User ID, Poll Data Array
    private Map<String, TextChannel> userMap = new HashMap<>();  //User ID, TextChannel of origin
    private Map<String, Integer> pollStep = new HashMap<>();

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (!args[0].startsWith(HiveBot.prefix)) {
            return;
        }


        if (HiveBot.commands.get(40).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.getRank(event, event.getMember().getId()) >= HiveBot.commands.get(40).getRank()) {
                    LOGGER.severe(HiveBot.commands.get(40).getCommand() + " called by " + event.getAuthor().getAsTag());

                    try {

                        for (Map.Entry<String, ArrayList<String>> pollMapEntry : pollMap.entrySet()) {
                            if (pollMapEntry.getKey().equalsIgnoreCase(event.getAuthor().getId())) {
                                event.getChannel().sendMessage("You already have a poll request started.").queue();
                                return;
                            }
                        }


                        // Open private message to receive information
                        event.getAuthor().openPrivateChannel().queue((channel) ->
                        {
                            channel.sendMessage("Starting POLL Request\nPlease observe the following:\n\nUse the keyword **CANCEL** at any time to cancel this request\nUse the keyword **SUBMIT** at any time to submit your poll request\n\nLets get started!\n\nPlease enter a description for your poll:").queue(success -> {

                                        pollMap.put(event.getAuthor().getId(), new ArrayList<>());
                                        userMap.put(event.getAuthor().getId(), event.getChannel());

                                    },
                                    failure -> {
                                        event.getChannel().sendMessage("I am unable to fulfill this request.  You have your direct messages from users turned off.").queue();
                                    });
                        });

                    } catch (NullPointerException e) {

                    }


                } else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
                System.out.println("Null permission found");
            }
        }
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) throws PermissionException {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        for (Map.Entry<String, ArrayList<String>> entry : pollMap.entrySet()) {
            String user = entry.getKey();

            //Confirm a user who initiated is sending a message
            if (user.equalsIgnoreCase(event.getAuthor().getId())) {

                //Cancel poll request
                if (event.getMessage().getContentRaw().equalsIgnoreCase("cancel")) {

                    //Start removing all entries from HashMaps

                    //Remove TextChannel Map
                    for (Map.Entry<String, TextChannel> userMapEntry : userMap.entrySet()) {
                        if (userMapEntry.getKey().equalsIgnoreCase(event.getAuthor().getId())) {
                            userMap.remove(userMapEntry.getKey());
                        }
                    }

                    //Remove poll array map entry
                    pollMap.remove(entry.getKey());

                    //Confirm cancelled to user
                    event.getChannel().sendMessage("Poll Cancelled").queue();
                    return;
                }


                //User is done adding items to poll
                if (event.getMessage().getContentRaw().equalsIgnoreCase("submit")) {
                    event.getChannel().sendMessage("Your poll will look like the following.  Send \"accept\" to send it").queue();
                    event.getChannel().sendMessage(buildPoll(event.getAuthor().getId(),event.getAuthor()).build()).queue();
                    return;
                }


                //User is done adding items to poll
                if (event.getMessage().getContentRaw().equalsIgnoreCase("accept")) {
                    event.getChannel().sendMessage("Completing your poll request").queue();
                    sendPoll(event.getAuthor().getId(),event.getAuthor());
                    return;
                }


                if (entry.getValue().size() < 1) {
                    entry.getValue().add(event.getMessage().getContentDisplay());
                    event.getChannel().sendMessage("Now enter your topics.  Each topic needs to be its own message.\n\nWhen done send \"submit\"").queue();
                    return;
                }

                if (entry.getValue().size() < 8) {
                    entry.getValue().add(event.getMessage().getContentDisplay());
                } else {
                    event.getChannel().sendMessage("Maximum argument count reached.  Send \"accept\" to send it or \"cancel\" to cancel the request").queue();
                    event.getChannel().sendMessage(buildPoll(event.getAuthor().getId(),event.getAuthor()).build()).queue();
                }
            }
        }
    }

    private EmbedBuilder buildPoll(String id,User user) {
        EmbedBuilder pollOut = new EmbedBuilder();
            try {

                for (Map.Entry<String, ArrayList<String>> pollEntry : pollMap.entrySet()) {
                    //Confirm a user who initiated is sending a message
                    if (pollEntry.getKey().equalsIgnoreCase(id)) {

                        pollOut.setTitle("HIVE Poll | " + user.getName());

                        StringBuilder topics = new StringBuilder();

                        for (int index = 0; index < pollEntry.getValue().size(); index++) {
                            if (index <= 0) {
                                pollOut.setDescription(pollEntry.getValue().get(index));
                            } else {
                                topics.append(index).append(". ").append(pollEntry.getValue().get(index)).append("\n");
                            }
                        }

                        pollOut.addField("Topics", topics.toString(), false);
                        //pollMap.remove(pollEntry.getKey());
                    }
                }
            } catch (NullPointerException e) {
            }

        return pollOut;

    }


    private void sendPoll(String id,User user) {
        int pollSize = 0;

        for (Map.Entry<String, ArrayList<String>> pollMapEntry : pollMap.entrySet()) {
            if (pollMapEntry.getKey().equalsIgnoreCase(id)) {
                pollSize = pollMapEntry.getValue().size() - 1;
            }
        }

        int finalPollSize = pollSize;


        // Send the poll
        for (Map.Entry<String, TextChannel> userMapEntry : userMap.entrySet()) {
            if (userMapEntry.getKey().equalsIgnoreCase(id)) {

                TextChannel outboundChannel = userMapEntry.getValue();
                outboundChannel.sendMessage(buildPoll(userMapEntry.getKey(),user).build()).queue(success -> {

                    if (finalPollSize <= 2) {
                        if (finalPollSize > 0) {
                            success.addReaction("\uD83D\uDD3C").queue();
                        }
                        if (finalPollSize > 1) {
                            success.addReaction("\uD83D\uDD3D").queueAfter(100, TimeUnit.MILLISECONDS);
                        }
                    }

                    if (finalPollSize > 2) {
                        if (finalPollSize > 0) {
                            success.addReaction("\u0031\uFE0F\u20E3").queue();
                        }
                        if (finalPollSize > 1) {
                            success.addReaction("\u0032\uFE0F\u20E3").queueAfter(100, TimeUnit.MILLISECONDS);
                        }
                        if (finalPollSize > 2) {
                            success.addReaction("\u0033\uFE0F\u20E3").queueAfter(200, TimeUnit.MILLISECONDS);
                        }
                        if (finalPollSize > 3) {
                            success.addReaction("\u0034\uFE0F\u20E3").queueAfter(300, TimeUnit.MILLISECONDS);
                        }
                        if (finalPollSize > 4) {
                            success.addReaction("\u0035\uFE0F\u20E3").queueAfter(400, TimeUnit.MILLISECONDS);
                        }
                        if (finalPollSize > 5) {
                            success.addReaction("\u0036\uFE0F\u20E3").queueAfter(500, TimeUnit.MILLISECONDS);
                        }
                        if (finalPollSize > 6) {
                            success.addReaction("\u0037\uFE0F\u20E3").queueAfter(600, TimeUnit.MILLISECONDS);
                        }
                        if (finalPollSize > 7) {
                            success.addReaction("\u0038\uFE0F\u20E3").queueAfter(700, TimeUnit.MILLISECONDS);
                        }
                    }

                }); // End of outbound send

                //Remove the entry from the HashMap
                Iterator it = pollMap.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry pair = (Map.Entry)it.next();
                    if(pair.getKey().toString().equalsIgnoreCase(id)){
                        System.out.println("Removing pollMap Entry:" + pair.getKey());
                        it.remove();
                    }
                }

                // Remove map from storage
                System.out.println("Removing userMap Entry: " + userMapEntry.getKey());
                userMap.remove(userMapEntry.getKey());

                System.out.println("New total of UserMap Entries: " + userMap.size());
                System.out.println("New total of PollMap Entries: " + pollMap.size());
            }
        }
    }
}
