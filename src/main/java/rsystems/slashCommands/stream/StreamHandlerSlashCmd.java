package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

public class StreamHandlerSlashCmd extends SlashCommand {

    @Override
    public String getName() {
        return "StreamHandler";
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData slashCommandData = Commands.slash(this.getName().toLowerCase(),this.getDescription());

        SubcommandData subcommandData = new SubcommandData("advert-killswitch","Enable/Disable the dispatch of adverts").addOption(OptionType.BOOLEAN,"allowance","True = Adverts allowed / False = Adverts Disabled",true);
        slashCommandData.addSubcommands(subcommandData);

        return slashCommandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        if(event.getSubcommandName().equalsIgnoreCase("advert-killswitch")){
            final boolean result = event.getOption("allowance").getAsBoolean();

            HiveBot.streamHandler.setAllowAdverts(result);

            if(result){
                reply(event,"Adverts are now allowed");
            } else {
                reply(event,"Adverts are now disabled from requesting");
            }
        }
    }

    @Override
    public String getDescription() {
        return "This command is used to help set HIVE stream settings";
    }
}
