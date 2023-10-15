package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import rsystems.HiveBot;
import rsystems.handlers.Diff_match_patch;
import rsystems.objects.Reference;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

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

            if (event.getModalId().contains("mc")) {
                multipleChoice = 1;
            }

            if (event.getModalId().contains("hr")) {
                hideResponses = 1;
            }

            for (String option : options) {
                if (option != null && !option.isBlank() && !option.isEmpty()) {
                    optionCount++;
                }
            }

            final int finalOptionCount = optionCount;

            try {
                final Integer createdPollID = HiveBot.database.createPoll(event.getMember().getIdLong(), optionCount, multipleChoice, hideResponses);

                if (createdPollID != null) {

                    createPollEmbed(createdPollID, pollQuery, options);

                    event.reply("Successfully created Poll with ID: " + createdPollID).setEphemeral(true).queue();

                    //final int finalOptionCount = optionCount;
                    event.getMessageChannel().sendMessageEmbeds(createPollEmbed(createdPollID, pollQuery, options)).queue(createdMessage -> {
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
                            HiveBot.database.putLong("PollTracker", "PollMessageID", createdMessage.getIdLong(), "id", createdPollID);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        } else if (event.getModalId().contains("lib-add")) {
            event.deferReply().setEphemeral(true).queue();

            Reference tempReference = buildReference(event);
            StringBuilder returnMessage = new StringBuilder();

            if (tempReference.getReferenceCommand().contains(" ")) {
                returnMessage.append("Sorry, spaces are not allowed for the root command.\n\n");
                returnMessage.append(originalReferenceRequestString(tempReference));

                event.getHook().editOriginal(returnMessage.toString()).queue();

                return;
            }

            try {
                if (HiveBot.database.checkForReference(tempReference.getReferenceCommand())) {
                    returnMessage.append("That reference trigger is already taken");
                    returnMessage.append(originalReferenceRequestString(tempReference));

                    event.getHook().editOriginal(returnMessage.toString()).queue();
                } else {
                    if (HiveBot.database.insertReference(tempReference) == 200) {
                        event.getHook().editOriginal(String.format("Reference %s accepted", tempReference.getReferenceCommand())).queue();
                    } else {
                        returnMessage.append("Submission rejected\n\n");
                        returnMessage.append(originalReferenceRequestString(tempReference));
                        event.getHook().editOriginal(returnMessage.toString()).queue();
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (event.getModalId().startsWith("libm-")) {
            handleLibraryModificationEvent(event);
        }
    }

    private MessageEmbed createPollEmbed(final int pollID, final String description, final ArrayList<String> options) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("HIVE Poll | ID: " + pollID)
                .setDescription(description);

        builder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));
        builder.setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl());

        int optionID = 1;
        for (String option : options) {
            if (option != null && !option.isEmpty() && !option.isBlank()) {
                builder.addField("Option " + optionID, option, false);
            }
            optionID++;
        }

        return builder.build();
    }

    private Reference buildReference(final ModalInteractionEvent event) {
        return buildReference(event, event.getValue("lib-name").getAsString().toLowerCase());
    }

    private Reference buildReference(final ModalInteractionEvent event, final String referenceTrigger) {

        String refBody = event.getValue("lib-body").getAsString();
        String formattedrefBody = refBody.replaceAll("\"", "\\\\\"");


        Reference returnReference = new Reference(referenceTrigger.toLowerCase(), formattedrefBody);


        if (event.getValue("lib-title") != null && !event.getValue("lib-title").getAsString().isEmpty()) {

            returnReference.setTitle(event.getValue("lib-title").getAsString());

        }

        if (event.getValue("lib-aliases") != null && !event.getValue("lib-aliases").getAsString().isEmpty()) {

            // Grab all aliases and process them
            String aliases = event.getValue("lib-aliases").getAsString();
            ArrayList<String> aliasList = new ArrayList<>(Arrays.asList(aliases.split(",")));
            ArrayList<String> formattedAliasList = new ArrayList<>();
            for (String alias : aliasList) {
                if (alias.startsWith(" ")) {
                    formattedAliasList.add(alias.stripLeading().toLowerCase());
                } else {
                    formattedAliasList.add(alias.toLowerCase());
                }
            }

            if (formattedAliasList.size() > 0) {
                returnReference.setAliases(formattedAliasList);
            }
        }

        return returnReference;
    }

    private String originalReferenceRequestString(final Reference tempReference) {

        StringBuilder returnMessage = new StringBuilder(String.format("Sorry, spaces are not allowed for the root command.\n\n" +
                        "" +
                        "**Your Submission:**\n" +
                        "Trigger: %s\n" +
                        "Body: %s\n" +
                        "Title: %s\n",
                tempReference.getReferenceCommand(), tempReference.getDescription(), tempReference.getTitle()));

        if (tempReference.getAliases() != null && !tempReference.getAliases().isEmpty()) {
            returnMessage.append(String.format("Aliases: %s", tempReference.getAliases().toString()));
        }

        return returnMessage.toString();

    }

    private void handleLibraryModificationEvent(final ModalInteractionEvent event) {
        event.deferReply().setEphemeral(false).queue();

        // Get the Reference Lookup ID
        final String lookup = event.getModalId().substring(5);

        //Establish a variable to store the Reference that is currently loaded in the library
        Reference currentReference = null;

        //Build the proposed Reference
        Reference tempReference = buildReference(event, lookup);

        //Create a string builder for the returnMessage
        StringBuilder returnMessage = new StringBuilder();

        //Try to find the current reference
        try {
            currentReference = HiveBot.database.getReference(lookup);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (currentReference == null) {
            returnMessage.append("No Reference found with that command");
        } else {

            StringBuilder titleDifferences = new StringBuilder();
            StringBuilder bodyDifferences = new StringBuilder();
            StringBuilder aliasDifferences = new StringBuilder();

            if(currentReference.getTitle() != null && tempReference.getTitle() != null) {
                String difference = StringUtils.difference(currentReference.getTitle(),tempReference.getTitle());
                if(difference != null && !difference.isEmpty()) {
                    titleDifferences.append(difference);
                }
            }

            String currentBodyStrings[] = currentReference.getDescription().split("\\R");
            String newBodyStrings[] = tempReference.getDescription().split("\\R");

            ArrayList<String> currentBodyArray = new ArrayList<>(Arrays.asList(currentBodyStrings));
            ArrayList<String> newBodyArray = new ArrayList<>(Arrays.asList(newBodyStrings));

            for(String s:currentBodyArray){
                if(!newBodyArray.contains(s)){
                    //bodyDifferences.append("- ").append(s).append("\n");

                    System.out.println("Didn't find: " + s);
                }
            }

            //bodyDifferences.append("\n");

            for(String s:newBodyArray){
                if(!currentBodyArray.contains(s)){
                    //bodyDifferences.append("+ ").append(s).append("\n");

                    System.out.println("Didn't find: " + s);
                }
            }

            if(currentReference.getAliases() != null && tempReference.getAliases() != null){
                String difference = StringUtils.difference(currentReference.getAliases().toString(),tempReference.getAliases().toString());
                if(difference != null && !difference.isEmpty()){
                    aliasDifferences.append(difference);
                }
            }

            EmbedBuilder outputBuilder = new EmbedBuilder().setTitle("Library Modification")
                    .setColor(HiveBot.getColor(HiveBot.colorType.FRUIT))
                    .setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl())
                    .setFooter("Reference: " + currentReference.getReferenceCommand());

            if(titleDifferences != null && !titleDifferences.toString().isEmpty()) {
                outputBuilder.addField("Title Difference",titleDifferences.toString(),false);
            }


                Diff_match_patch dmp = new Diff_match_patch();
                LinkedList<Diff_match_patch.Diff> diff = dmp.diff_main(currentReference.getDescription(),tempReference.getDescription(),false);

                dmp.Diff_EditCost = 4;
                dmp.diff_cleanupEfficiency(diff);


                for(Diff_match_patch.Diff s:diff){
                    if(s.operation.equals(Diff_match_patch.Operation.EQUAL)){
                        bodyDifferences.append(s.text);
                    } else if(s.operation.equals(Diff_match_patch.Operation.INSERT)){

                        String[] strings = s.text.split("\\R");
                        for(int x = 0; x < strings.length; x++){
                            if(x < strings.length) {
                                bodyDifferences.append("+ ").append(strings[x]).append("\n");
                            } else {
                                bodyDifferences.append("+ ").append(strings[x]);
                            }
                        }
                    } else if(s.operation.equals(Diff_match_patch.Operation.DELETE)){
                        String[] strings = s.text.split("\\R");
                        for(int x = 0; x < strings.length; x++){
                            if(x < strings.length) {
                                bodyDifferences.append("- ").append(strings[x]).append("\n");
                            } else {
                                bodyDifferences.append("- ").append(strings[x]);
                            }
                        }
                    }
                }

                outputBuilder.addField("Body Differences","```diff\n" +bodyDifferences.toString() + "\n```",false);
            //}

            if(aliasDifferences != null && !aliasDifferences.toString().isEmpty()){
                outputBuilder.addField("Alias Differences",aliasDifferences.toString(),false);
            }

            try {
                if (HiveBot.database.modifyReference(tempReference) > 0) {

                    if(event.getGuild().getId().equalsIgnoreCase("469330414121517056")){
                        TextChannel logChannel = event.getGuild().getTextChannelById("469914813540204545");
                        logChannel.sendMessageEmbeds(outputBuilder.build()).queue();
                    }

                    event.getHook().editOriginalEmbeds(outputBuilder.build()).queue();
                } else {
                    event.getHook().editOriginal("Something went wrong").queue();
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            return;
        }

        event.getHook().editOriginal(returnMessage.toString()).queue();

    }
}