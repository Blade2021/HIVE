package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import rsystems.HiveBot;
import rsystems.objects.Reference;
import rsystems.objects.SlashCommand;

import java.sql.Ref;
import java.sql.SQLException;
import java.util.ArrayList;

public class LibraryModify extends SlashCommand {

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription())
                .addOption(OptionType.STRING, "reference", "The reference to modify",true);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        final String lookupReference = event.getOption("reference").getAsString().toLowerCase();

        boolean referenceFound = false;

        try {
            Reference reference = HiveBot.database.getReference(lookupReference);
            if (reference != null) {
                referenceFound = true;
            }

            if (referenceFound) {
                TextInput libAliases = null;
                String modalId = "libm-" + reference.getReferenceCommand().toLowerCase();

                ArrayList<String> AliasList = reference.getAliases();

                if(AliasList != null && !AliasList.isEmpty()) {
                    libAliases = TextInput.create("lib-aliases", "Reference Aliases", TextInputStyle.SHORT)
                            .setPlaceholder("This can be left blank")
                            .setMinLength(5)
                            .setMaxLength(500)
                            .setValue(AliasList.toString().substring(1,AliasList.toString().length()-1))
                            .setRequired(false)
                            .build();
                } else {
                    libAliases = TextInput.create("lib-aliases", "Reference Aliases", TextInputStyle.SHORT)
                            .setPlaceholder("This can be left blank")
                            .setMinLength(5)
                            .setMaxLength(500)
                            .setRequired(false)
                            .build();
                }

                TextInput libTitle = TextInput.create("lib-title", "Reference Title", TextInputStyle.SHORT)
                        .setPlaceholder("Title here")
                        .setMinLength(2)
                        .setMaxLength(100)
                        .setRequired(false)
                        .setValue(reference.getTitle())
                        .build();

                TextInput libBody = TextInput.create("lib-body", "Reference Body", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Insert body here")
                        .setMinLength(1)
                        .setMaxLength(2000)
                        .setRequired(true)
                        .setValue(reference.getDescription())
                        .build();

                Modal modal = Modal.create(modalId, "Library Modification")
                        .addComponents(ActionRow.of(libAliases), ActionRow.of(libTitle), ActionRow.of(libBody))
                        .build();

                event.replyModal(modal).queue();
            } else {
                event.reply("No reference found with that name").queue();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "Modify a reference";
    }
}
