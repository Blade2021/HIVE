package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import rsystems.objects.SlashCommand;

import java.util.ArrayList;

public class Block extends SlashCommand {
    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData commandData = new CommandDataImpl(this.getName().toLowerCase(),this.getDescription());

        ArrayList<SubcommandData> subCommands = new ArrayList<>();
        subCommands.add(new SubcommandData("add","Add a user to the blacklist").addOption(OptionType.USER,"user","The user to be blocked",true));
        subCommands.add(new SubcommandData("remove","Remove a user from the blacklist").addOption(OptionType.USER,"user","The user to be unblocked",true));

        return commandData.addSubcommands(subCommands);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        if(event.getSubcommandName().equalsIgnoreCase("add")){

        }
    }

    @Override
    public String getDescription() {
        return null;
    }
}
