package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;

public class SubmitToken extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());

        commandData.addOption(OptionType.STRING,"tokenid","The Token ID to store with the key",true);
        commandData.addOption(OptionType.STRING,"accesstoken","The access token to store",true);
        commandData.addOption(OptionType.STRING,"refreshtoken","The refresh token to store",true);

        return commandData;
    }

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {

        event.deferReply(isEphemeral()).queue();

        Integer broadcasterID = Integer.parseInt(event.getOption("tokenid").getAsString());
        String accessToken = event.getOption("accesstoken").getAsString();
        String refreshToken = event.getOption("refreshtoken").getAsString();

        try {

            HiveBot.database.insertCredential(broadcasterID,accessToken,refreshToken);

            reply(event,"Success");
            return;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        reply(event,"Failure");

    }

    @Override
    public String getDescription() {
        return "Testing functions";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
