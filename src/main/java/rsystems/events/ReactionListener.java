package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.*;


public class ReactionListener extends ListenerAdapter {

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        // Ignore bots
        if (event.getUser().isBot()) {
            return;
        }
        try {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            if (event.getReactionEmote().isEmote()) {
                String reactionID = event.getReactionEmote().getId();

                /*
                KARMA REACTION LISTENER
                 */

                // Check only karma reactions
                if ((reactionID.equalsIgnoreCase("717177145717424180")) || (reactionID.equalsIgnoreCase("717177177849724948"))) {

                    // User attempted to give them self karma via reaction
                    if (event.getMember().getId().equalsIgnoreCase(message.getMember().getId())) {
                        karmaLogger.warning(event.getUser().getAsTag() + " attempted to send karma to themselves via reaction");
                        message.removeReaction(event.getReactionEmote().getEmote(), event.getUser()).queue();
                        return;
                    }

                    // User attempted to give a BOT karma via reaction
                    if (message.getMember().getUser().isBot()) {
                        karmaLogger.warning(event.getUser().getAsTag() + " attempted to send karma to a BoT via reaction");
                        message.removeReaction(event.getReactionEmote().getEmote(), event.getUser()).queue();
                        return;
                    }

                    // POSITIVE KARMA REACTION
                    if (event.getReactionEmote().getId().equalsIgnoreCase("717177145717424180")) {
                        int result = karmaSQLHandler.updateKarma(event.getMember().getId(), message.getAuthor().getId(), true);

                        // Success
                        if (result == 4) {
                            karmaLogger.info("Sending positive karma from: " + event.getMember().getId() + " to: " + message.getMember().getId());
                            nickname.parseRank(event.getGuild(), message.getMember());
                        } else {
                            // Failure
                            message.removeReaction(":KU:717177145717424180", event.getUser()).queue();
                            karmaLogger.warning("Failed to send karma from: " + event.getMember().getId() + " to: " + message.getMember().getId() + " RESULT: " + result);
                        }

                        // NEGATIVE KARMA REACTION
                    } else if (event.getReactionEmote().getId().equalsIgnoreCase("717177177849724948")) {
                        int result = karmaSQLHandler.updateKarma(event.getMember().getId(), message.getAuthor().getId(), false);

                        // Success
                        if (result == 4) {
                            karmaLogger.info("Sending negative karma from: " + event.getMember().getId() + " to: " + message.getMember().getId());
                            nickname.parseRank(event.getGuild(), message.getMember());
                        } else {
                            // Failure
                            message.removeReaction(":KD:717177177849724948", event.getUser()).queue();
                            karmaLogger.warning("Failed to send karma from: " + event.getMember().getId() + " to: " + message.getMember().getId() + " RESULT: " + result);
                        }
                    }
                }


                /*
                SUGGESTION BLOCK LISTENER
                 */
            }

            if (suggestionHandler.checkSuggestionPool(event.getMessageId())) {
                System.out.println("Suggestion Pool: Message ID Found");
                // Test to see if reaction was check or X emoji
                if (event.getReactionEmote().isEmoji() && ((event.getReactionEmote().getEmoji().equalsIgnoreCase("✅")) || (event.getReactionEmote().getEmoji().equalsIgnoreCase("❌")))){

                    //Process through OPEN suggestions to see if the reaction came from a known approval request
                    for (Map.Entry<Integer, String> entry : suggestionHandler.openSuggestionMap(0).entrySet()) {
                        if (event.getMessageId().equalsIgnoreCase(entry.getValue())) {

                            //Request was APPROVED
                            if (event.getReactionEmote().getEmoji().equalsIgnoreCase("✅")) {
                                suggestionHandler.postSuggestion(entry.getKey(), event.getGuild());

                            } else if (event.getReactionEmote().getEmoji().equalsIgnoreCase("❌")) {
                                // Request was REJECTED
                                suggestionHandler.updateRowInt(entry.getKey(), "status", 5);
                            }

                            //Delete the request message
                            removeApprovalRequest(event.getChannel(), entry.getValue());
                        }
                    }
                } else {
                    try {
                        event.getReaction().removeReaction(event.getUser()).queue();
                    } catch(NullPointerException e){
                        System.out.println("Couldn't find reaction");
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Null found when sending karma via reaction");
        } catch (IllegalStateException e) {
            System.out.println("Exception: " + e.getCause());
        }
    }

    private void removeApprovalRequest(TextChannel channel, String messageID) {
        try {
            List<Message> messages = channel.getHistory().retrievePast(100).complete();

            //Remove the approval request
            Iterator it = messages.iterator();
            while (it.hasNext()) {
                Message tempMessage = (Message) it.next();
                if (tempMessage.getId().equalsIgnoreCase(messageID)) {
                    tempMessage.addReaction("\uD83D\uDCE8").queue();
                    tempMessage.delete().queueAfter(30, TimeUnit.SECONDS);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("couldn't find message");
        }
    }

}
