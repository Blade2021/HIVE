package rsystems.slashCommands.moderation;

import com.github.twitch4j.helix.domain.Highlight;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

public class StreamMarker extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription()).addOption(OptionType.STRING, "description", "Stream Marker Description", true);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        if (HiveBot.streamHandler.isStreamActive()) {

            String markerDescription = event.getOption("description").getAsString();
            if (!markerDescription.isEmpty()) {


                HiveBot.twitchBot.getClient().getHelix().createStreamMarker(HiveBot.twitchBot.getCredential().getAccess_Token(), new Highlight(Config.get("TWITCH_BROADCASTER_ID"), markerDescription));
                reply(event, "Stream Marker created!");

            }
        } else {
            reply(event, "There is no active stream at this time");
        }
    }

    @Override
    public String getDescription() {
        return "Submit a stream marker on Twitch";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public Integer getPermissionIndex() {
        return 32;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }
}
