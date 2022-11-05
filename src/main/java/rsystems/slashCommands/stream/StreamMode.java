package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

public class StreamMode extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription()).addOption(OptionType.BOOLEAN,"active","Stream Mode. TRUE = Active, FALSE = Inactive",true);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();


        Boolean streamMode = event.getOption("active").getAsBoolean();

        HiveBot.streamHandler.setStreamActive(streamMode);
        reply(event,"Setting Stream Mode to: `" + streamMode + "`",isEphemeral());

        if(streamMode) {
            HiveBot.obsRemoteController.connect();
        } else {
            HiveBot.obsRemoteController.disconnect();
        }

    }

    @Override
    public String getDescription() {
        return "Set the stream mode";
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public Integer getPermissionIndex() {
        return 8;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }
}