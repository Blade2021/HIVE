package rsystems.tasks;

import net.dv8tion.jda.api.entities.Activity;
import rsystems.HiveBot;

import java.util.TimerTask;

public class BotActivity extends TimerTask {

    @Override
    public void run() {
        final int currentIndex = HiveBot.activityStatusIndex;
        String newActivity = HiveBot.sqlHandler.nextActivity(currentIndex);
        HiveBot.jda.getPresence().setActivity(Activity.playing(newActivity));
        HiveBot.activityStatusIndex++;
    }
}
