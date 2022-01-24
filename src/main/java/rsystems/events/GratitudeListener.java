package rsystems.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GratitudeListener extends ListenerAdapter {

    private static ArrayList<String> coolDownChannels = new ArrayList<>();
    private static Long karmaPosReaction = Long.valueOf(Config.get("KARMA_POS_REACTION"));
    private static Long karmaNegReaction = Long.valueOf(Config.get("KARMA_NEG_REACTION"));
    private static String stageReaction = "\uD83D\uDCE6";

    private static String[] triggers = {"thanks", "thank you", "thnx", "thx "};

    public String[] getTriggers(){
        return triggers;
    }

    public static void gratitudeMessageReceived(MessageReceivedEvent event) {

        for (String trigger : triggers) {
            if (event.getMessage().getContentDisplay().toLowerCase().contains(trigger)) {
                if ((!event.getMessage().getMentionedMembers().isEmpty()) || (event.getMessage().getReferencedMessage() != null)) {

                    try {
                        if (HiveBot.karmaSQLHandler.getAvailableKarmaPoints(event.getAuthor().getId()) >= 1) {
                            Member receivingMember = null;
                            if (!event.getMessage().getMentionedMembers().isEmpty()) {
                                // Assign the receiver to the first mentioned member
                                receivingMember = event.getMessage().getMentionedMembers().get(0);
                            } else {
                                if (event.getMessage().getReferencedMessage().getMember() != null) {
                                    // Assign the receiver to the replied message's author
                                    receivingMember = event.getMessage().getReferencedMessage().getMember();
                                }
                            }

                            Member sendingMember = event.getMember();

                            if ((receivingMember != null) && (!receivingMember.getUser().isBot()) && (receivingMember != sendingMember)) {

                                event.getMessage().addReaction(stageReaction).queue();
                                event.getMessage().addReaction("\u274C").queue();

                                event.getMessage().removeReaction(stageReaction,event.getJDA().getSelfUser()).queueAfter(60,TimeUnit.SECONDS);
                                event.getMessage().removeReaction("\u274C",event.getJDA().getSelfUser()).queueAfter(60,TimeUnit.SECONDS);

                                if (!HiveBot.karmaSQLHandler.insertStaging(event.getChannel().getIdLong(), event.getMessageIdLong(), sendingMember.getIdLong())) {
                                    event.getMessage().addReaction("⚠").queue();
                                }

                                return;
                            } else {

                                //ADD A POTATO
                                event.getMessage().addReaction("\uD83E\uDD54").queue();
                                return;
                            }

                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (!coolDownChannels.contains(event.getChannel().getId())) {

                        for (String s : triggers) {
                            if (event.getMessage().getContentRaw().toLowerCase().contains(s)) {
                                event.getChannel().sendMessage("Don't forget to send karma! <:KU:717177145717424180> ~karma for more info!").queue(success -> {
                                    success.delete().queueAfter(10, TimeUnit.MINUTES);
                                });

                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            System.out.println("Adding " + event.getChannel().getName() + " to cooldown");
                                            coolDownChannels.add(event.getChannel().getId());
                                            //Sleep the thread for 10 minutes
                                            Thread.sleep(600000);
                                        } catch (InterruptedException ie) {
                                        }
                                        //Remove the entry from the HashMap
                                        Iterator it = coolDownChannels.iterator();
                                        while (it.hasNext()) {
                                            String checkId = (String) it.next();
                                            if (checkId.equalsIgnoreCase(event.getChannel().getId())) {
                                                it.remove();
                                            }
                                        }
                                    }
                                }).start();
                            }
                        }
                    }

                }
            }
        }

    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;

        final Long messageID = event.getMessageIdLong();
        final boolean reactionEmote = event.getReactionEmote().isEmote();

        Long reactionID = null;
        if (reactionEmote)
            reactionID = event.getReaction().getReactionEmote().getIdLong();

        String emojiID = null;
        if (!reactionEmote)
            emojiID = event.getReactionEmote().getEmoji();



		/*
			REACTION RELATED TO STAGING
		*/


        //If reaction was an emoji & emoji was a thumbs up or X
        if ((!reactionEmote) && ((emojiID.equalsIgnoreCase(stageReaction)) || (emojiID.equalsIgnoreCase("\u274C")))) {


            //Check karma staging table to see if messageID was found
            try {
                if (HiveBot.karmaSQLHandler.checkStaging(messageID, event.getMember().getIdLong())) {

                    //Message was found in the staging table.
                    final String finalEmojiID = emojiID;
                    event.getChannel().retrieveMessageById(messageID).queue(message -> {

                        try {
                            HiveBot.karmaSQLHandler.deleteFromStaging(messageID);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        // Clear all reactions
                        message.clearReactions().queue(success -> {
                            message.addReaction("\uD83D\uDCEC").queue();
                        });

                        User receiver = null;
                        if (message.getMentionedMembers().isEmpty()) {
                            receiver = message.getReferencedMessage().getMember().getUser();
                        } else {
                            receiver = message.getMentionedMembers().get(0).getUser();
                        }

                        if (receiver != null) {
                            if (receiver.isBot()) {
                                event.getChannel().sendMessage("Nice try!").queue();
                                return;
                            }

                            //Send positive karma
                            if (finalEmojiID.equalsIgnoreCase(stageReaction)) {
                                //send karma to original user
                                // add reaction to message to confirm karma was sent.
                                try {
                                    HiveBot.karmaSQLHandler.updateKarma(message.getIdLong(), event.getUser(), receiver, true);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    });

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }


		/*
			KARMA REACTIONS
		*/

        if ((reactionEmote) && ((reactionID.equals(karmaPosReaction)) || (reactionID.equals(karmaNegReaction)))) {

            try {
                if (HiveBot.karmaSQLHandler.getAvailableKarmaPoints(event.getUserId()) >= 1) {

                    final Long finalReactionID = reactionID;
                    event.getChannel().retrieveMessageById(messageID).queue(message -> {

                        final User sendingUser = event.getUser();
                        final User receivingUser = message.getAuthor();

                        if ((sendingUser != null) && (receivingUser != null)) {

                            if ((sendingUser != receivingUser) && (!receivingUser.isBot())) {

                                boolean direction = finalReactionID.equals(karmaPosReaction);


                                System.out.println(String.format("Sending %s karma from %s to %s", direction, sendingUser.getAsTag(), receivingUser.getAsTag()));
                                try {
                                    HiveBot.karmaSQLHandler.updateKarma(event.getMessageIdLong(), sendingUser, receivingUser, direction);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {

                                //Remove reaction
                                event.getReaction().removeReaction(event.getUser()).queue();
                            }
                        }
                    });

                } else {
                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}