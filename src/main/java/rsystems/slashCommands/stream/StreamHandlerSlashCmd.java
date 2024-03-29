package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
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
        subCommands.add(new SubcommandData("settings","View/Change the settings for the stream handler"));
        subCommands.add(new SubcommandData("animation-killswitch","Enable/Disable the dispatch of animations").addOption(OptionType.BOOLEAN,"allowance","True = Animations allowed / False = Animations Disabled",true));
        subCommands.add(new SubcommandData("status","Get the status of the Stream Handler"));
        subCommands.add(new SubcommandData("clear-queue","Clear the current request queue"));
        subCommands.add(new SubcommandData("reconnect","Reconnect to the Stream Controller"));
        subCommands.add(new SubcommandData("pause","Pause animations from being accepted.  True = Pause active  False = Resume requests").addOption(OptionType.BOOLEAN,"pause","Requested pause status",true));

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
        } else
            if(event.getSubcommandName().equalsIgnoreCase("status")){

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
        } else
            if(event.getSubcommandName().equalsIgnoreCase("clear-queue")){

            //CLEAR QUEUE SUB-COMMAND

            //This method will refund all points to request initiators
            //Integer amount = HiveBot.streamHandler.clearRequestQueue();

            //reply(event,String.format("`%d` requests have been refunded/removed.",amount));

            HiveBot.streamHandler.clearRequestQueue();
            reply(event,"Request queue cleared");
        } else
            if(event.getSubcommandName().equalsIgnoreCase("reconnect")){
                reply(event,"Attempting to reconnect to Stream Controller");

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            HiveBot.obsRemoteController.disconnect();
                            Thread.sleep(3000);
                            HiveBot.obsRemoteController.connect();
                            event.getHook().sendMessage("Reconnected").queue();

                        } catch (InterruptedException ie) {
                        }
                    }
                }).start();

        } else
            if(event.getSubcommandName().equalsIgnoreCase("pause")){
                if(event.getOption("pause").getAsBoolean() == true){
                    HiveBot.streamHandler.setAnimationPause(true);
                    reply(event,"Pausing all animation requests");
                } else {
                    HiveBot.streamHandler.setAnimationPause(false);
                    reply(event,"Resuming all animation requests");
                }
            } else
            if(event.getSubcommandName().equalsIgnoreCase("settings")){
                try {
                    String animationsEnabled = HiveBot.database.getKeyValue("AnimationsEnabled");
                    String enableOBSConnect = HiveBot.database.getKeyValue("EnableOBSConnect");

                    TextInput animationsEnableInput = TextInput.create("animationenable", "Animations Enable Input", TextInputStyle.SHORT)
                            .setPlaceholder("0 = Disabled / 1 = Enabled")
                            .setMinLength(1)
                            .setMaxLength(1)
                            .setValue(animationsEnabled)
                            .setRequired(false)
                            .build();

                    TextInput obsConnectInput = TextInput.create("obsconnect", "OBS Connect Input", TextInputStyle.SHORT)
                            .setPlaceholder("Wether to connect to OBS with the bot.")
                            .setMinLength(1)
                            .setMaxLength(1)
                            .setValue(enableOBSConnect)
                            .setRequired(false)
                            .build();

                    Modal modal = Modal.create("streamhandler", "Stream Settings")
                            .addActionRows(ActionRow.of(animationsEnableInput),ActionRow.of(obsConnectInput))
                            .build();

                    event.replyModal(modal).queue();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
    }

    @Override
    public String getDescription() {
        return "This command is used to help set HIVE stream settings";
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.KICK_MEMBERS;
    }
}
