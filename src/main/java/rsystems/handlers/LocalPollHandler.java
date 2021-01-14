package rsystems.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import rsystems.HiveBot;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LocalPollHandler {

    private static Map<Long, ArrayList<String>> pollMap = new HashMap<>(); //User ID, Poll Data Array
    private static Map<Long, Long> userMap = new HashMap<>();  //User ID, TextChannel ID of origin

    public boolean setupPoll(Long userID, Long channelID) {
        boolean output = false;

        if ((!pollMap.containsKey(userID) && (!userMap.containsKey(userID)))) {
            pollMap.put(userID, new ArrayList<String>());
            userMap.put(userID, channelID);
        }

        return output;
    }

    public boolean addChoice(Long userID, String choice) {
        boolean output = false;

        if (pollMap.containsKey(userID)) {
            pollMap.get(userID).add(choice);
            output = true;
        }

        return output;
    }

    public boolean checkForUser(Long userID) {
        return userMap.containsKey(userID);
    }

    public boolean sendPoll(Long userID) {
        boolean output = false;

        if ((userMap.containsKey(userID) && (pollMap.containsKey(userID)))) {

            //Does the channel exist
            if (HiveBot.drZzzGuild().getTextChannelById(userMap.get(userID)) != null) {
                TextChannel channel = HiveBot.drZzzGuild().getTextChannelById(userMap.get(userID));
                Member member = HiveBot.drZzzGuild().getMemberById(userID);

                int index = pollMap.get(userID).size() - 1;

                try {
                    int finalIndex = index;
                    channel.sendMessage(buildPoll(userID)).queue(success -> {

                        userMap.remove(userID);
                        pollMap.remove(userID);

                        success.addReaction("\u0031\uFE0F\u20E3").queue();
                        success.addReaction("\u0032\uFE0F\u20E3").queueAfter(100, TimeUnit.MILLISECONDS);
                        if (finalIndex > 2) {
                            success.addReaction("\u0033\uFE0F\u20E3").queueAfter(200, TimeUnit.MILLISECONDS);
                        }
                        if (finalIndex > 3) {
                            success.addReaction("\u0034\uFE0F\u20E3").queueAfter(300, TimeUnit.MILLISECONDS);
                        }
                        if (finalIndex > 4) {
                            success.addReaction("\u0035\uFE0F\u20E3").queueAfter(400, TimeUnit.MILLISECONDS);
                        }
                        if (finalIndex > 5) {
                            success.addReaction("\u0036\uFE0F\u20E3").queueAfter(500, TimeUnit.MILLISECONDS);
                        }
                        if (finalIndex > 6) {
                            success.addReaction("\u0037\uFE0F\u20E3").queueAfter(600, TimeUnit.MILLISECONDS);
                        }
                        if (finalIndex > 7) {
                            success.addReaction("\u0038\uFE0F\u20E3").queueAfter(700, TimeUnit.MILLISECONDS);
                        }

                    });

                } catch (PermissionException e) {
                    //error handling
                }
            }
        }

        return output;
    }

    public Message buildPoll(Long userID) {
        if ((userMap.containsKey(userID) && (pollMap.containsKey(userID)))) {

            EmbedBuilder embedBuilder = new EmbedBuilder();
            Member member = HiveBot.drZzzGuild().getMemberById(userID);

            if(member != null) {

                embedBuilder.setTitle("HIVE Poll | " + member.getEffectiveName())
                        .setColor(Color.ORANGE);

                StringBuilder topics = new StringBuilder();

                int index = 0;
                for (String choice : pollMap.get(userID)) {
                    if (index <= 0) {
                        embedBuilder.setDescription(choice);
                    } else {
                        topics.append(index).append(".  ").append(choice).append("\n");
                    }
                    index++;
                }

                embedBuilder.addField("Topics",topics.toString(),false);

                MessageBuilder messageBuilder = new MessageBuilder();
                messageBuilder.setEmbed(embedBuilder.build());
                embedBuilder.clear();
                return messageBuilder.build();
            }
        }

        return null;
    }


    public void privateMessageEvent(PrivateMessageReceivedEvent event) throws PermissionException {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        Long userID = event.getAuthor().getIdLong();

        if (pollMap.containsKey(event.getAuthor().getIdLong())) {

            //Cancel poll request
            if (event.getMessage().getContentRaw().equalsIgnoreCase("cancel")) {

                //Start removing all entries from HashMaps

                //Remove TextChannel Map
                userMap.remove(userID);

                //Remove poll array map entry
                pollMap.remove(userID);

                //Confirm cancelled to user
                event.getChannel().sendMessage("Poll Cancelled").queue();
                return;
            }


            //User is done adding items to poll
            if (event.getMessage().getContentRaw().equalsIgnoreCase("submit")) {
                event.getChannel().sendMessage("Your poll will look like the following.  Send \"accept\" to send it").queue();
                event.getChannel().sendMessage(buildPoll(userID)).queue();
                return;
            }


            //User is done adding items to poll
            if (event.getMessage().getContentRaw().equalsIgnoreCase("accept")) {
                event.getChannel().sendMessage("Completing your poll request").queue();
                sendPoll(userID);
                return;
            }

            if (pollMap.get(userID).size() < 1) {
                pollMap.get(userID).add(event.getMessage().getContentDisplay());
                event.getChannel().sendMessage("Now enter your topics.  Each topic needs to be its own message.\n\nWhen done send \"submit\"").queue();
                return;
            }

            if (pollMap.get(userID).size() < 8) {
                pollMap.get(userID).add(event.getMessage().getContentDisplay());
            } else {
                event.getChannel().sendMessage("Maximum argument count reached.  Send \"accept\" to send it or \"cancel\" to cancel the request").queue();
                event.getChannel().sendMessage(buildPoll(userID)).queue();
            }
        }

    }
}