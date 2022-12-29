package rsystems.slashCommands.utility;

import net.dv8tion.jda.api.entities.User;
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
import rsystems.objects.SlashCommand;

public class CreatePoll extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription())
                .addOption(OptionType.BOOLEAN, "multiple", "Allow multiple choice?", true)
                .addOption(OptionType.BOOLEAN, "hide-responses","Hide responses from users?");
    }

    @Override
    public String getName() {
        return "Poll-Create";
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        TextInput pollQuery = TextInput.create("pollquery", "What are you voting on?", TextInputStyle.SHORT)
                .setPlaceholder("What are you voting for?")
                .setMinLength(7)
                .setMaxLength(50)
                .setRequired(true)
                .build();

        TextInput pollOption1 = TextInput.create("polloption1", "Option 1", TextInputStyle.SHORT)
                .setPlaceholder("Option 1")
                .setMinLength(1)
                .setMaxLength(20)
                .setRequired(true)
                .build();

        TextInput pollOption2 = TextInput.create("polloption2", "Option 2", TextInputStyle.SHORT)
                .setPlaceholder("Option 2")
                .setMinLength(1)
                .setMaxLength(20)
                .setRequired(true)
                .build();

        TextInput pollOption3 = TextInput.create("polloption3", "Option 3", TextInputStyle.SHORT)
                .setPlaceholder("Option 3")
                .setMinLength(1)
                .setMaxLength(20)
                .setRequired(false)
                .build();

        TextInput pollOption4 = TextInput.create("polloption4", "Option 4", TextInputStyle.SHORT)
                .setPlaceholder("Option 4")
                .setMinLength(1)
                .setMaxLength(20)
                .setRequired(false)
                .build();

        String id = "newpoll";
        if(event.getOption("multiple").getAsBoolean()){
            id = "newpollmc";
        }

        if(event.getOption("hide-responses") != null && (event.getOption("hide-responses").getAsBoolean())){
            id = id + "hr";
        }

        Modal modal = Modal.create(id, "HIVE Poll Creation")
                .addActionRows(ActionRow.of(pollQuery), ActionRow.of(pollOption1), ActionRow.of(pollOption2), ActionRow.of(pollOption3), ActionRow.of(pollOption4))
                .build();

        event.replyModal(modal).queue();
    }

    @Override
    public String getDescription() {
        return "Create a poll";
    }

}
