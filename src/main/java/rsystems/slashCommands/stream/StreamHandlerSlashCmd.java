package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.util.ArrayList;

public class StreamHandlerSlashCmd extends SlashCommand {

    @Override
    public String getName() {
        return "StreamHandler";
    }

    @Override
    public SlashCommandData getCommandData() {
        SlashCommandData slashCommandData = Commands.slash(this.getName().toLowerCase(),this.getDescription());

        ArrayList<SubcommandData> subCommands = new ArrayList<>();
        subCommands.add(new SubcommandData("advert-killswitch","Enable/Disable the dispatch of adverts").addOption(OptionType.BOOLEAN,"allowance","True = Adverts allowed / False = Adverts Disabled",true));
        subCommands.add(new SubcommandData("status","Get the status of the Stream Handler"));

        slashCommandData.addSubcommands(subCommands);

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

        if(event.getSubcommandName().equalsIgnoreCase("status")){
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Stream Handler");
            builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
            builder.setThumbnail(event.getGuild().getIconUrl());

            builder.addField("LIVE:",String.valueOf(HiveBot.streamHandler.isStreamActive()).toUpperCase(),false);

            // ADVERTS KILL SWITCH SETTING
            String advertKS = "Allowed";
            if(!HiveBot.streamHandler.allowAdverts()){
                advertKS = "Disabled";
            }
            builder.addField("Advert\nKillSwitch",advertKS,true);

            reply(event,builder.build());
        }
    }

    @Override
    public String getDescription() {
        return "This command is used to help set HIVE stream settings";
    }
}
