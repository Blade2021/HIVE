package rsystems.events;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
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


                if ((reactionID.equalsIgnoreCase("717177145717424180")) || (reactionID.equalsIgnoreCase("717177177849724948"))) {
                    if (event.getMember().getId().equalsIgnoreCase(message.getMember().getId())) {
                        HiveBot.LOGGER.warning(event.getUser().getAsTag() + " attempted to send karma to themselves via reaction");
                        message.removeReaction(event.getReactionEmote().getEmote(), event.getUser()).queue();
                        return;
                    }

                    if (message.getMember().getUser().isBot()) {
                        HiveBot.LOGGER.warning(event.getUser().getAsTag() + " attempted to send karma to a BoT via reaction");
                        message.removeReaction(event.getReactionEmote().getEmote(), event.getUser()).queue();
                        return;
                    }

                    if (event.getReactionEmote().getId().equalsIgnoreCase("717177145717424180")) {

                        if (HiveBot.karmaSQLHandler.updateKarma(event.getMember().getId(), message.getAuthor().getId(), true)) {
                            HiveBot.LOGGER.info("Sending positive karma from: " + event.getMember().getId() + " to: " + message.getMember().getId());
                        } else {
                            message.removeReaction(":KU:717177145717424180", event.getUser()).queue();
                        }
                    } else if (event.getReactionEmote().getId().equalsIgnoreCase("717177177849724948")) {

                        if (HiveBot.karmaSQLHandler.updateKarma(event.getMember().getId(), message.getAuthor().getId(), false)) {
                            HiveBot.LOGGER.info("Sending negative karma from: " + event.getMember().getId() + " to: " + message.getMember().getId());
                        } else {
                            message.removeReaction(":KD:717177177849724948", event.getUser()).queue();
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("null found");
        } catch (IllegalStateException e) {
            System.out.println(e.getCause());
        }
    }
}
