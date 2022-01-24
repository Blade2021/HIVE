package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

public class StreamMode extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription()).addOption(OptionType.BOOLEAN,"active","Stream Mode. TRUE = Active, FALSE = Inactive",true);
        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {
        event.deferReply(isEphemeral()).queue();


        Boolean streamMode = event.getOption("active").getAsBoolean();

        HiveBot.streamHandler.setStreamActive(streamMode);
        reply(event,"Setting Stream Mode to: `" + streamMode + "`",isEphemeral());

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
        return 16;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }
}
