package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import rsystems.objects.SlashCommand;

public class Devour extends SlashCommand {
    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

    }

    @Override
    public String getDescription() {
        return "Devour your cashews!";
    }
}
