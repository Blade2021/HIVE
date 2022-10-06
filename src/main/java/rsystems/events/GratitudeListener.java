package rsystems.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GratitudeListener extends ListenerAdapter {

    private static final ArrayList<String> coolDownChannels = new ArrayList<>();
    private static final Long karmaPosReaction = Long.valueOf(Config.get("KARMA_POS_REACTION"));
    private static final Long karmaNegReaction = Long.valueOf(Config.get("KARMA_NEG_REACTION"));
    private static final String acceptEmoji = "\uD83D\uDCE6"; // THIS IS THE BOX EMOJI
    private static final String declineEmoji = "\u274C";  // THIS IS THE RED X EMOJI

    private static final String potatoEmoji = "\uD83E\uDD54"; //THIS IS THE POTATO EMOJI

    private static final String[] triggers = {"thanks", "thank you", "thnx", "thx "};

    public String[] getTriggers(){
        return triggers;
    }

    public static void gratitudeMessageReceived(MessageReceivedEvent event) {

        for (String trigger : triggers) {
            if (event.getMessage().getContentDisplay().toLowerCase().contains(trigger)) {
                if ((!event.getMessage().getMentions().getMembers().isEmpty()) || (event.getMessage().getReferencedMessage() != null)) {

                    try {
                        if (HiveBot.karmaSQLHandler.getAvailableKarmaPoints(event.getAuthor().getId()) >= 1) {
                            Member receivingMember = null;
                            if (!event.getMessage().getMentions().getMembers().isEmpty()) {
                                // Assign the receiver to the first mentioned member
                                receivingMember = event.getMessage().getMentions().getMembers().get(0);
                            } else {
                                if (event.getMessage().getReferencedMessage().getMember() != null) {
                                    // Assign the receiver to the replied message's author
                                    receivingMember = event.getMessage().getReferencedMessage().getMember();
                                }
                            }

                            Member sendingMember = event.getMember();

                            if ((receivingMember != null) && (!receivingMember.getUser().isBot()) && (receivingMember != sendingMember)) {

                                event.getMessage().addReaction(Emoji.fromUnicode(acceptEmoji)).queue();
                                event.getMessage().addReaction(Emoji.fromUnicode(declineEmoji)).queue();

                                event.getMessage().removeReaction(Emoji.fromUnicode(acceptEmoji),event.getJDA().getSelfUser()).queueAfter(60,TimeUnit.SECONDS);
                                event.getMessage().removeReaction(Emoji.fromUnicode(declineEmoji),event.getJDA().getSelfUser()).queueAfter(60,TimeUnit.SECONDS);

                                if (!HiveBot.karmaSQLHandler.insertStaging(event.getChannel().getIdLong(), event.getMessageIdLong(), sendingMember.getIdLong())) {
                                    event.getMessage().addReaction(Emoji.fromUnicode("⚠")).queue();
                                }

                                return;
                            } else {

                                //ADD A POTATO
                                event.getMessage().addReaction(Emoji.fromUnicode(potatoEmoji)).queue();
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
        final String reactionEmote = event.getReaction().getEmoji().toString();
        final Long id = event.getReaction().getEmoji().asCustom().getIdLong();



		/*
			REACTION RELATED TO STAGING
		*/


        //If reaction was an emoji & emoji was a thumbs up or X
        if ((reactionEmote.equalsIgnoreCase(acceptEmoji)) || (reactionEmote.equalsIgnoreCase("\u274C"))) {


            //Check karma staging table to see if messageID was found
            try {
                if (HiveBot.karmaSQLHandler.checkStaging(messageID, event.getMember().getIdLong())) {

                    //Message was found in the staging table.
                    //final String finalEmojiID = emojiID;
                    event.getChannel().retrieveMessageById(messageID).queue(message -> {

                        try {
                            HiveBot.karmaSQLHandler.deleteFromStaging(messageID);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        // Clear all reactions
                        message.clearReactions().queue(success -> {
                            message.addReaction(Emoji.fromUnicode("\uD83D\uDCEC")).queue();
                        });

                        User receiver = null;
                        if (message.getMentions().getMembers().isEmpty()) {
                            receiver = message.getReferencedMessage().getMember().getUser();
                        } else {
                            receiver = message.getMentions().getMembers().get(0).getUser();
                        }

                        if (receiver != null) {
                            if (receiver.isBot()) {
                                event.getChannel().sendMessage("Nice try!").queue();
                                return;
                            }

                            //Send positive karma
                            if (reactionEmote.equalsIgnoreCase(acceptEmoji)) {
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

        if ((id.equals(karmaPosReaction)) || (id.equals(karmaNegReaction))) {

            try {
                if (HiveBot.karmaSQLHandler.getAvailableKarmaPoints(event.getUserId()) >= 1) {

                    //final Long finalReactionID = reactionID;
                    event.getChannel().retrieveMessageById(messageID).queue(message -> {

                        final User sendingUser = event.getUser();
                        final User receivingUser = message.getAuthor();

                        if ((sendingUser != null) && (receivingUser != null)) {

                            if ((sendingUser != receivingUser) && (!receivingUser.isBot())) {

                                boolean direction = reactionEmote.equals(karmaPosReaction);


                                System.out.printf("Sending %s karma from %s to %s%n", direction, sendingUser.getAsTag(), receivingUser.getAsTag());
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