package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.awt.*;
import java.util.ArrayList;

public class Embed extends SlashCommand {


    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(this.getName().toLowerCase(), this.getDescription());

        ArrayList<SubcommandData> subCommands = new ArrayList<>();

        subCommands.add(new SubcommandData("create", "Creates an Embed in a target channel for management through the bot.").addOption(OptionType.CHANNEL, "channel", "Target channel for the embed", true));

        subCommands.add(new SubcommandData("edit", "Edit an existing Embedded message created by HIVE").addOption(OptionType.CHANNEL, "channel", "Target channel of the message", true).addOption(OptionType.STRING, "message", "Target message ID", true));

        commandData.addSubcommands(subCommands);

        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        final String channelID = event.getOption("channel").getAsString();
        Integer channelType = checkChannel(channelID);

        if (event.getSubcommandName().equalsIgnoreCase("create")) {

            if (channelType != null && channelType == 1 || channelType == 2) {
                // CREATE AN EMBEDDED MESSAGE

                event.replyModal(createModal(null,null, null,channelID, "new")).queue();

            }

        } else {
            // MODIFY AN EXISTING EMBEDDED MESSAGE

            if (channelType == 1) {

                TextChannel targetChannel = HiveBot.mainGuild().getTextChannelById(channelID);
                if (targetChannel != null) {
                    Message retrievedMessage = getMessage(targetChannel, event.getOption("message").getAsString());
                    if (retrievedMessage != null) {
                        // Found message

                        // Pull the title
                        String titleString = retrievedMessage.getEmbeds().get(0).getTitle();

                        // Pull the description
                        String messageString = retrievedMessage.getEmbeds().get(0).getDescription();

                        // Pull the color
                        Color color = retrievedMessage.getEmbeds().get(0).getColor();

                        String colorString = null;
                        if(color != null) {
                            colorString = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
                        }

                        event.replyModal(createModal(colorString,messageString,titleString, targetChannel.getId(), retrievedMessage.getId())).queue();
                    }
                }
            } else if(channelType == 2){

                NewsChannel targetChannel = HiveBot.mainGuild().getNewsChannelById(channelID);
                if (targetChannel != null) {
                    Message retrievedMessage = getMessage(targetChannel, event.getOption("message").getAsString());
                    if (retrievedMessage != null) {
                        // Found message

                        // Pull the title
                        String titleString = retrievedMessage.getEmbeds().get(0).getTitle();

                        // Pull the description
                        String messageString = retrievedMessage.getEmbeds().get(0).getDescription();

                        // Pull the color
                        Color color = retrievedMessage.getEmbeds().get(0).getColor();

                        String colorString = null;
                        if(color != null) {
                            colorString = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
                        }

                        event.replyModal(createModal(colorString,messageString,titleString, targetChannel.getId(), retrievedMessage.getId())).queue();
                    }
                }

            }else {
                reply(event, "Sorry, I cannot process that");
            }
        }
    }

    private Integer checkChannel(String channelID) {
        if (HiveBot.mainGuild().getTextChannelById(channelID) != null) {
            return 1;
        } else if (HiveBot.mainGuild().getNewsChannelById(channelID) != null) {
            return 2;
        } else {
            return null;
        }
    }

    @Override
    public String getDescription() {
        return "Create or modify an embedded message created by HIVE";
    }

    @Override
    public Integer getPermissionIndex() {
        return 16;
    }

    private Message getMessage(TextChannel channel, String messageID) {

        if (channel.retrieveMessageById(messageID) != null) {
            return channel.retrieveMessageById(messageID).complete();
        } else {
            return null;
        }
    }

    private Message getMessage(NewsChannel channel, String messageID) {

        if (channel.retrieveMessageById(messageID) != null) {
            return channel.retrieveMessageById(messageID).complete();
        } else {
            return null;
        }
    }

    private Modal createModal(final String colorString, final String messageString, final String titleString, final String channelID, final String messageID) {

        TextInput color = TextInput.create("color", "Color", TextInputStyle.SHORT)
                .setPlaceholder("The desired color of the embed")
                .setMinLength(7)
                .setMaxLength(7)
                .setValue(colorString)
                .setRequired(false)
                .build();

        TextInput title = TextInput.create("title", "Title", TextInputStyle.SHORT)
                .setPlaceholder("The title of the embed")
                .setMaxLength(30)
                .setValue(titleString)
                .setRequired(false)
                .build();

        TextInput message = TextInput.create("message", "Message", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Input your message here")
                .setMaxLength(1700)
                .setValue(messageString)
                .build();

        Modal modal = Modal.create(String.format("mid-%s-%s", channelID, messageID), "Embedded Message Builder")
                .addActionRows(ActionRow.of(title),ActionRow.of(message),ActionRow.of(color))
                .build();

        return modal;
    }

}