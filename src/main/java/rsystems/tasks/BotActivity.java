package rsystems.tasks;

import net.dv8tion.jda.api.entities.Activity;
import rsystems.HiveBot;

import java.util.TimerTask;

public class BotActivity extends TimerTask {

    @Override
    public void run() {
        final int currentIndex = HiveBot.activityStatusIndex;
        String newActivity = HiveBot.sqlHandler.nextActivity(currentIndex);

        newActivity = newActivity.replace("{usercount}",String.valueOf(HiveBot.mainGuild().getMemberCount()));


        String currentActivity = HiveBot.jda.getPresence().getActivity().getName();
        if(!currentActivity.equals(newActivity)) {
            HiveBot.jda.getPresence().setActivity(Activity.playing(newActivity));
        }
    }
}
