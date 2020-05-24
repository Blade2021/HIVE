package rsystems.adapters;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import rsystems.Config;

import java.util.TimerTask;

public class AutoStatus extends TimerTask {
    private JDA jda;
    private String activityString = Config.get("activity");

    public AutoStatus(JDA jda, String activityString){
        this.jda = jda;
        this.activityString = activityString;
    }

    @Override
    public void run() {
        jda.getPresence().setActivity(Activity.playing(activityString));
    }
}
