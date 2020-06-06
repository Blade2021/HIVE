package rsystems.events;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;


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

                // Check only karma reactions
                if ((reactionID.equalsIgnoreCase("717177145717424180")) || (reactionID.equalsIgnoreCase("717177177849724948"))) {

                    // User attempted to give them self karma via reaction
                    if (event.getMember().getId().equalsIgnoreCase(message.getMember().getId())) {
                        HiveBot.karmaLogger.warning(event.getUser().getAsTag() + " attempted to send karma to themselves via reaction");
                        message.removeReaction(event.getReactionEmote().getEmote(), event.getUser()).queue();
                        return;
                    }

                    // User attempted to give a BOT karma via reaction
                    if (message.getMember().getUser().isBot()) {
                        HiveBot.karmaLogger.warning(event.getUser().getAsTag() + " attempted to send karma to a BoT via reaction");
                        message.removeReaction(event.getReactionEmote().getEmote(), event.getUser()).queue();
                        return;
                    }

                    if (event.getReactionEmote().getId().equalsIgnoreCase("717177145717424180")) {
                        int result = HiveBot.karmaSQLHandler.updateKarma(event.getMember().getId(), message.getAuthor().getId(), true);

                        // Success
                        if (result == 4) {
                            HiveBot.karmaLogger.info("Sending positive karma from: " + event.getMember().getId() + " to: " + message.getMember().getId());
                        } else {
                            // Failure
                            message.removeReaction(":KU:717177145717424180", event.getUser()).queue();
                            HiveBot.karmaLogger.warning("Failed to send karma from: " + event.getMember().getId() + " to: " + message.getMember().getId() + " RESULT: " + result);
                        }
                    } else if (event.getReactionEmote().getId().equalsIgnoreCase("717177177849724948")) {
                        int result = HiveBot.karmaSQLHandler.updateKarma(event.getMember().getId(), message.getAuthor().getId(), false);

                        // Success
                        if (result == 4) {
                            HiveBot.karmaLogger.info("Sending negative karma from: " + event.getMember().getId() + " to: " + message.getMember().getId());
                        } else {
                            // Failure
                            message.removeReaction(":KD:717177177849724948", event.getUser()).queue();
                            HiveBot.karmaLogger.warning("Failed to send karma from: " + event.getMember().getId() + " to: " + message.getMember().getId() + " RESULT: " + result);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Null found when sending karma via reaction");
        } catch (IllegalStateException e) {
            System.out.println(e.getCause());
        }
    }
}
