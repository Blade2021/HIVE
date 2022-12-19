package rsystems.slashCommands.user;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;

public class SetYoutubeLink extends SlashCommand {

    @Override
    public Integer getPermissionIndex() {
        return 4;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription()).addOption(OptionType.STRING, "youtube", "The URL to your Youtube", true);
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        try {

            if (event.getOption("youtube") != null) {


                if (HiveBot.database.putPixelTubeLink(event.getUser().getIdLong(), event.getOption("youtube").getAsString()) > 0) {
                    reply(event,"Success");
                } else {
                    reply(event,"An error occurred");
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "Set your own youtube link";
    }
}
