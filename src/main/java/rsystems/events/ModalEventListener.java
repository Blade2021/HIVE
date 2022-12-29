package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.objects.Poll;

import java.sql.SQLException;
import java.util.ArrayList;

public class ModalEventListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        if (event.getModalId().contains("newpoll")) {

            Integer optionCount = 0;

            final String pollQuery = event.getValue("pollquery").getAsString();

            ArrayList<String> options = new ArrayList<>();
            options.add(event.getValue("polloption1").getAsString());
            options.add(event.getValue("polloption2").getAsString());
            options.add(event.getValue("polloption3").getAsString());
            options.add(event.getValue("polloption4").getAsString());
            int multipleChoice = 0;
            int hideResponses = 0;

            if(event.getModalId().contains("mc")){
                multipleChoice = 1;
            }

            if(event.getModalId().contains("hr")){
                hideResponses = 1;
            }

            for(String option:options){
                if(option != null && !option.isBlank() && !option.isEmpty()){
                    optionCount++;
                }
            }

            final int finalOptionCount = optionCount;

            try {
                final Integer createdPollID = HiveBot.database.createPoll(event.getMember().getIdLong(), optionCount, multipleChoice, hideResponses);

                if (createdPollID != null) {

                    createPollEmbed(createdPollID,pollQuery,options);

                    event.reply("Successfully created Poll with ID: " + createdPollID).setEphemeral(true).queue();

                    //final int finalOptionCount = optionCount;
                    event.getMessageChannel().sendMessageEmbeds(createPollEmbed(createdPollID,pollQuery,options)).queue(createdMessage -> {
                        createdMessage.pin().queue();
                        createdMessage.addReaction(Emoji.fromUnicode("1️⃣")).queue();
                        createdMessage.addReaction(Emoji.fromUnicode("2️⃣")).queue();
                        if (finalOptionCount >= 3) {
                            createdMessage.addReaction(Emoji.fromUnicode("U+0033 U+20E3")).queue();
                        }
                        if (finalOptionCount == 4) {
                            createdMessage.addReaction(Emoji.fromUnicode("U+0034 U+20E3")).queue();
                        }

                        try {
                            HiveBot.database.putLong("PollTracker","PollMessageID",createdMessage.getIdLong(),"id",createdPollID);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }
    }

    private MessageEmbed createPollEmbed(final int pollID, final String description, final ArrayList<String> options){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("HIVE Poll | ID: " + pollID)
                .setDescription(description);

        builder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));
        builder.setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl());

        int optionID = 1;
        for(String option:options){
            if(option != null && !option.isEmpty() && !option.isBlank()) {
                builder.addField("Option " + optionID, option, false);
            }
            optionID++;
        }

        return builder.build();
    }
}