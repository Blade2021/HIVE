package rsystems.slashCommands.user;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.objects.SlashCommand;

import java.util.ArrayList;

public class Dispatch extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());

        ArrayList<SubcommandData> subList = new ArrayList<>();

        subList.add(new SubcommandData("blink","Blink Dr Zzs's Lights"));
        subList.add(new SubcommandData("advert","Display a message on the stream").addOption(OptionType.STRING,"message","The message to be displayed.",true));

        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {

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
