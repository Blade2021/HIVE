package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
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
        subCommands.add(new SubcommandData("animation-killswitch","Enable/Disable the dispatch of animations").addOption(OptionType.BOOLEAN,"allowance","True = Animations allowed / False = Animations Disabled",true));
        subCommands.add(new SubcommandData("status","Get the status of the Stream Handler"));
        subCommands.add(new SubcommandData("clear-queue","Clear the current request queue"));
        subCommands.add(new SubcommandData("reconnect","Reconnect to the Stream Controller"));

        slashCommandData.addSubcommands(subCommands);

        return slashCommandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        if(event.getSubcommandName().equalsIgnoreCase("animation-killswitch")){

            // ANIMATION KILLSWITCH SUB-COMMAND

            final boolean result = event.getOption("allowance").getAsBoolean();

            HiveBot.streamHandler.setAllowAnimations(result);

            if(result){
                reply(event,"Animations are now allowed");
            } else {
                reply(event,"Animation Requests are disabled");
            }
        } else if(event.getSubcommandName().equalsIgnoreCase("status")){

            // STATUS SUB-COMMAND

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Stream Handler");
            builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
            builder.setThumbnail(event.getGuild().getIconUrl());

            builder.addField("LIVE:",String.valueOf(HiveBot.streamHandler.isStreamActive()).toUpperCase(),false);

            // ANIMATIONS KILL SWITCH SETTING
            String animationKS = "Allowed";
            if(!HiveBot.streamHandler.allowAnimations()){
                animationKS = "Disabled";
            }
            builder.addField("Animations",animationKS,true);

            builder.addField("Request Queue",String.format("%d of %d",HiveBot.streamHandler.getQueueSize(),HiveBot.streamHandler.getMaxQueueSize()),true);

            builder.addField("First Here Claimed:", String.valueOf(HiveBot.streamHandler.isFirstHereClaimed()).toUpperCase(),true);

            builder.addField("Animations Called",String.valueOf(HiveBot.streamHandler.getAnimationsCalled()),true);

            builder.addField("Cashews Spent",String.valueOf(HiveBot.streamHandler.getSpentCashews()),true);

            reply(event,builder.build());
        } else if(event.getSubcommandName().equalsIgnoreCase("clear-queue")){

            //CLEAR QUEUE SUB-COMMAND

            //This method will refund all points to request initiators
            //Integer amount = HiveBot.streamHandler.clearRequestQueue();

            //reply(event,String.format("`%d` requests have been refunded/removed.",amount));

            HiveBot.streamHandler.clearRequestQueue();
            reply(event,"Request queue cleared");
        } else if(event.getSubcommandName().equalsIgnoreCase("reconnect")){

            HiveBot.obsRemoteController.connect();
            reply(event,"Attempting to reconnect to Stream Controller");

        }
    }

    @Override
    public String getDescription() {
        return "This command is used to help set HIVE stream settings";
    }
}
