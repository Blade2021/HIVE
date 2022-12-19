package rsystems.tasks;

import net.dv8tion.jda.api.entities.Guild;
import rsystems.HiveBot;
import rsystems.slashCommands.utility.Led;

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
