package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import rsystems.objects.SlashCommand;

public class RegisterAnimation extends SlashCommand {

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        TextInput scene = TextInput.create("scene","Scene",TextInputStyle.SHORT)
                .setPlaceholder("Please enter the Scene that the source is on.  Case Sensitive!")
                .setMaxLength(30)
                .build();

        TextInput source = TextInput.create("source","Source",TextInputStyle.SHORT)
                .setPlaceholder("Please enter the Source to be registered.  Case Sensitive")
                .setMaxLength(30)
                .build();

        Modal modal = Modal.create("obsanimationreg", "OBS Animation Registration")
                .addActionRows(ActionRow.of(scene), ActionRow.of(source))
                .build();

        event.replyModal(modal).queue();
    }

    @Override
    public String getDescription() {
        return "Register an OBS animation to the database";
    }
}
