package rsystems.slashCommands.development;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.objects.SlashCommand;

import java.util.ArrayList;

public class Dispatch extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = Commands.slash(this.getName().toLowerCase(),this.getDescription());

        ArrayList<SubcommandData> subList = new ArrayList<>();

        subList.add(new SubcommandData("blink","Blink Dr Zzs's Lights"));
        subList.add(new SubcommandData("animation","Display a message on the stream").addOption(OptionType.STRING,"message","The message to be displayed.",true));

        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        event.deferReply(isEphemeral()).queue();

        

    }

    @Override
    public String getDescription() {
        return "Spend your pixels";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
