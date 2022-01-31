package rsystems.slashCommands.user;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;

public class Mini extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());
        //commandData.addOptions(new OptionData(OptionType.STRING,"this","this").addChoice("choice","choice 1").);

        commandData.addOption(OptionType.STRING,"description","The text you want to display when someone calls your mini",true);

        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {
        event.deferReply(isEphemeral()).queue();

        String miniText = event.getOption("description").getAsString();
        if(miniText.length() > 50){
            reply(event,"Sorry, your text was too long to process.\nMax character count: 50");
        } else {
            try {
                if(HiveBot.database.insertUserMini(event.getUser().getIdLong(),miniText)){
                    reply(event,"Your mini has been updated");
                } else {
                    reply(event, "An error occurred.  Please try again later");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getDescription() {
        return "Set your little mini snippet of info.";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
