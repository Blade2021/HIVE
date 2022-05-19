package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.objects.SlashCommand;

public class StreamHandler extends SlashCommand {
    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData slashCommandData = Commands.slash(this.getName().toLowerCase(),this.getDescription());

        SubcommandData subcommandData = new SubcommandData("allow-adverts","Enable/Disable the dispatch of adverts").addOption(OptionType.BOOLEAN,"allowance","Enable/Disable",true,true);

        return slashCommandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

    }

    @Override
    public String getDescription() {
        return "This command is used to help set HIVE stream settings";
    }
}
