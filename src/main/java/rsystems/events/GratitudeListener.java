package rsystems.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GratitudeListener extends ListenerAdapter {

    private static ArrayList<String> coolDownChannels = new ArrayList<>();
    private static Long karmaPosReaction = Long.valueOf(Config.get("KARMA_POS_REACTION"));

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        String[] triggers = {"thanks", "thank you", "thnx", "thx "};

        for (String trigger : triggers) {
            if (event.getMessage().getContentDisplay().toLowerCase().contains(trigger)) {
                if ((!event.getMessage().getMentionedMembers().isEmpty()) || (event.getMessage().getReferencedMessage() != null)) {

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

                        event.getMessage().addReaction("FailFish:763254562026815489").queue();
                        event.getMessage().addReaction("❌").queue();

                        //karmaStagingMap.putIfAbsent(event.getMessageIdLong(), event.getMember().getIdLong());

                        if(!HiveBot.karmaSQLHandler.insertStaging(event.getChannel().getIdLong(), event.getMessageIdLong(),sendingMember.getIdLong())){
                            event.getMessage().addReaction("⚠").queue();
                        }
                    } else {
                        event.getMessage().addReaction("\uD83E\uDD54").queue();
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
        if(reactionEmote)
            reactionID = event.getReaction().getReactionEmote().getIdLong();

        String emojiID = null;
        if(!reactionEmote)
            emojiID = event.getReactionEmote().getEmoji();

        //Did event contain a karma reaction?
        if(((reactionEmote) && (reactionID.equals(karmaPosReaction))) || (emojiID.equalsIgnoreCase("❌"))){

            final Long finalReactionID = reactionID;
            final String finalEmojiID = emojiID;
            event.getChannel().retrieveMessageById(messageID).queue(message -> {

                final Member messageAuthor = message.getMember();
                final Member reactionAuthor = event.getMember();

                Member karmaReciever = null;

                if(messageAuthor == reactionAuthor){
                    //Check Staging for Message ID and Author ID
                    if (HiveBot.karmaSQLHandler.checkStaging(messageID, event.getMember().getIdLong())) {

                        if (reactionEmote) {
                            if (finalReactionID.equals(karmaPosReaction)) {
                                message.removeReaction("❌", HiveBot.jda.getSelfUser()).queue();

                            }
                        } else {
                            if (finalEmojiID.equalsIgnoreCase("❌")) {
                                final Emote karmaReaction =  HiveBot.drZzzGuild().getEmoteById(karmaPosReaction);
                                if(karmaReaction != null)
                                    message.removeReaction(HiveBot.drZzzGuild().getEmoteById(karmaPosReaction),HiveBot.jda.getSelfUser()).queue();
                                message.clearReactions("❌").queue();
                            }

                        }


                        HiveBot.karmaSQLHandler.deleteFromStaging(event.getMessageIdLong());
                    }
                }







            });
            /*
            //Check Staging for Message ID and Author ID
            if (HiveBot.karmaSQLHandler.checkStaging(messageID, event.getMember().getIdLong())) {

                String removalEmote = null;

                if (reactionEmote) {
                    if (reactionID.equals(karmaPosReaction)) {
                        removalEmote = "❌";

                        //Send Karma
                    }
                } else {
                    if (emojiID.equalsIgnoreCase("❌")) {
                        removalEmote = String.valueOf(karmaPosReaction);
                    }

                }

                if (removalEmote != null) {
                    //Get a list of the past 100 messages
                    String finalRemovalEmote = removalEmote;

                    event.getChannel().retrieveMessageById(event.getMessageId()).queue(success -> {
                        if (finalRemovalEmote.equalsIgnoreCase("❌")) {
                            success.removeReaction("❌", HiveBot.jda.getSelfUser()).queue();
                        } else {
                            success.removeReaction(event.getGuild().getEmoteById(karmaPosReaction), HiveBot.jda.getSelfUser()).queue();
                        }

                        HiveBot.karmaSQLHandler.deleteFromStaging(event.getMessageIdLong());
                    });
                }

            }





        }

        //Check Staging Table for Reaction
        if (HiveBot.karmaSQLHandler.checkStaging(messageID, event.getMember().getIdLong())) {

            System.out.println("KarmaStagingMap ID Found: " + messageID);

            //Is the reaction an emote?

            if(((reactionEmote) && (reactionID.equals(karmaPosReaction))) || (emojiID.equalsIgnoreCase("❌"))){
                String removalEmote = null;

                if (reactionEmote) {
                    if (reactionID.equals(karmaPosReaction)) {
                        removalEmote = "❌";

                        //Send Karma
                    }
                } else {
                    if (emojiID.equalsIgnoreCase("❌")) {
                        removalEmote = String.valueOf(karmaPosReaction);
                    }

                }

                if (removalEmote != null) {
                    //Get a list of the past 100 messages
                    String finalRemovalEmote = removalEmote;

                    event.getChannel().retrieveMessageById(event.getMessageId()).queue(success -> {
                        if (finalRemovalEmote.equalsIgnoreCase("❌")) {
                            success.removeReaction("❌", HiveBot.jda.getSelfUser()).queue();
                        } else {
                            success.removeReaction(event.getGuild().getEmoteById(karmaPosReaction), HiveBot.jda.getSelfUser()).queue();
                        }

                        HiveBot.karmaSQLHandler.deleteFromStaging(event.getMessageIdLong());
                    });
                }
            }
        } else {

            //Message was not in staging
            if (reactionEmote) {

                if (reactionID.equals(karmaPosReaction)){


                }

            }

             */
        }

    }

    private void handleKarmaTransaction(MessageReactionAddEvent event){



    }

}
