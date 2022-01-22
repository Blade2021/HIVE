package rsystems.tasks;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import rsystems.HiveBot;
import rsystems.slashCommands.generic.Led;

import java.sql.SQLException;
import java.util.TimerTask;

public class LedListCheck extends TimerTask {
    @Override
    public void run() {

        try {
            if(HiveBot.database.checkForLedUpsert()) {
                for (Guild guild : HiveBot.jda.getGuilds()) {
                    Led newLedInteraction = new Led();
                    guild.upsertCommand(newLedInteraction.getCommandData()).queue();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
