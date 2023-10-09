package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import rsystems.objects.SlashCommand;

public class LibraryAdd extends SlashCommand {

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

                TextInput libTrigger = TextInput.create("lib-name", "Reference Trigger", TextInputStyle.SHORT)
                        .setPlaceholder("Insert trigger here")
                        .setMinLength(5)
                        .setMaxLength(25)
                        .setRequired(true)
                        .build();

                TextInput libAliases = TextInput.create("lib-aliases", "Reference Aliases", TextInputStyle.SHORT)
                        .setPlaceholder("This can be left blank")
                        .setMinLength(5)
                        .setMaxLength(500)
                        .setRequired(false)
                        .build();

                TextInput libTitle = TextInput.create("lib-title", "Reference Title", TextInputStyle.SHORT)
                        .setPlaceholder("Title here")
                        .setMinLength(2)
                        .setMaxLength(100)
                        .setRequired(false)
                        .build();

                TextInput libBody = TextInput.create("lib-body", "Reference Body", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Insert body here")
                        .setMinLength(1)
                        .setMaxLength(2000)
                        .setRequired(true)
                        .build();

                Modal modal = Modal.create("lib-add", "Library Submission")
                        .addActionRows(ActionRow.of(libTrigger), ActionRow.of(libAliases), ActionRow.of(libTitle), ActionRow.of(libBody))
                        .build();

                event.replyModal(modal).queue();
    }

    @Override
    public String getDescription() {
        return "Start the creation of a reference for the library";
    }
}
