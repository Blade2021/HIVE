package rsystems.slashCommands.utility;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
import java.util.ArrayList;

public class LedList extends SlashCommand {
    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        try{

            ArrayList<String> ledList = HiveBot.database.getLEDList();

            StringBuilder stringBuilder = new StringBuilder();

            for(int x = 0; x < ledList.size(); x++){
                stringBuilder.append(ledList.get(x));

                if(x < ledList.size()){
                    stringBuilder.append(", ");
                }
            }

            reply(event,stringBuilder.toString(),isEphemeral());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "Get a list of supported LED types for the LED Command";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
