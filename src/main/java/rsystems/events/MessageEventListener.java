package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import rsystems.HiveBot;
import rsystems.handlers.ExceptionHandler;
import rsystems.objects.Poll;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class MessageEventListener extends ListenerAdapter {

    @Override
    public void onMessageUpdate(final MessageUpdateEvent event) {
        /*if (event.isFromGuild()) {
            if (event.getChannelType().isThread()) {
            } else {
                if (event.getMessage().isPinned()) {
                    MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
                    messageBuilder.addActionRow(
                            Button.primary(String.format("depin-7:%d:%d", event.getMessage().getIdLong(), event.getAuthor().getIdLong()), "7 Day De-Pin"),
                            Button.primary(String.format("depin-30:%d:%d", event.getMessage().getIdLong(), event.getAuthor().getIdLong()), "30 Day De-Pin")
                    );
                    messageBuilder.setContent("Would you like to automatically De-Pin this message?");

                    event.getMessage().reply(messageBuilder.build()).queue(success -> {
                        success.delete().queueAfter(15, TimeUnit.MINUTES);
                    });

                }
            }
        }

         */
    }

    @Override
    public void onMessageDelete(final MessageDeleteEvent event) {
        try {
            HiveBot.database.deleteRow("MessageTable", "MessageID", event.getMessageIdLong());
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionHandler.notifyException(e,this.getClass().getName());
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        try {

            final Poll poll = HiveBot.database.getPoll(event.getMessageIdLong());
            if (poll != null && poll.isAllowVoting()) {

                try {
                    // Don't allow custom emoji's on Polls
                    if (event.getReaction().getEmoji().asCustom() != null) {
                        event.getReaction().removeReaction(event.getUser()).queue();
                        return;
                    }
                }catch(IllegalStateException e){
                    // do nothing
                }

                final UnicodeEmoji unicodeEmoji = event.getReaction().getEmoji().asUnicode();
                if(unicodeEmoji == null){
                    event.getReaction().removeReaction(event.getUser()).queue();
                } else if(!unicodeEmoji.equals(Emoji.fromUnicode("1️⃣")) || !unicodeEmoji.equals(Emoji.fromUnicode("2️⃣")) || !unicodeEmoji.equals(Emoji.fromUnicode("1️⃣")) || !unicodeEmoji.equals(Emoji.fromUnicode("1️⃣"))){
                    event.getReaction().removeReaction(event.getUser()).queue();
                }

                if (event.getReaction().getEmoji().asUnicode().equals(Emoji.fromUnicode("1️⃣"))) {
                    if (HiveBot.database.addVote(event.getUserIdLong(), 1, event.getMessageIdLong()) == 200) {
                        if (poll.isHideResponses()) {
                            event.getReaction().removeReaction(event.getUser()).queue();
                        }
                        poll.addVote(1);
                    }
                } else if (event.getReaction().getEmoji().asUnicode().equals(Emoji.fromUnicode("2️⃣"))) {
                    if (HiveBot.database.addVote(event.getUserIdLong(), 2, event.getMessageIdLong()) == 200) {
                        if (poll.isHideResponses()) {
                            event.getReaction().removeReaction(event.getUser()).queue();
                        }
                        poll.addVote(2);
                    }
                } else if (event.getReaction().getEmoji().asUnicode().equals(Emoji.fromUnicode("U+0033 U+20E3"))) {
                    if(poll.getOptionCount() >= 3) {
                        if (HiveBot.database.addVote(event.getUserIdLong(), 3, event.getMessageIdLong()) == 200) {
                            if (poll.isHideResponses()) {
                                event.getReaction().removeReaction(event.getUser()).queue();
                            }
                            poll.addVote(3);
                        }
                    } else {
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                } else if (event.getReaction().getEmoji().asUnicode().equals(Emoji.fromUnicode("U+0034 U+20E3"))) {
                    if(poll.getOptionCount() >= 4) {
                        if (HiveBot.database.addVote(event.getUserIdLong(), 4, event.getMessageIdLong()) == 200) {
                            if (poll.isHideResponses()) {
                                event.getReaction().removeReaction(event.getUser()).queue();
                            }
                            poll.addVote(4);
                        }
                    } else {
                        event.getReaction().removeReaction(event.getUser()).queue();
                    }
                }

                event.getChannel().asTextChannel().retrieveMessageById(poll.getMessageID()).queue(foundMessage -> {
                    //final Poll updatedPoll = HiveBot.database.getPoll(event.getMessageIdLong());
                    MessageEmbed originalEmbed = foundMessage.getEmbeds().get(0);

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    for (MessageEmbed.Field field : originalEmbed.getFields()) {

                        Integer lookupFieldID = Integer.valueOf(field.getName().substring(7, 8));
                        embedBuilder.addField(String.format("Option %d - VOTES: %d", lookupFieldID, poll.getVoteCount(lookupFieldID)), field.getValue(), false);

                    }

                    embedBuilder.setTitle(originalEmbed.getTitle());
                    embedBuilder.setDescription(originalEmbed.getDescription());
                    embedBuilder.setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl());
                    embedBuilder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));

                    foundMessage.editMessageEmbeds(embedBuilder.build()).queue();

                });
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch(Exception e){
            e.printStackTrace();
            ExceptionHandler.notifyException(e,this.getClass().getName());
        }

    }
}
