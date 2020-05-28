package rsystems.adapters;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.TimerTask;

public class UptimeStatus extends TimerTask {
    private JDA jda;

    public UptimeStatus(JDA jda){
        this.jda = jda;
    }

    @Override
    public void run() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();
        long uptimeinSeconds = uptime / 1000;
        long uptimeHours = uptimeinSeconds / (60 * 60);
        long uptimeMinutes = (uptimeinSeconds / 60) - (uptimeHours * 60);
        long uptimeSeconds = uptimeinSeconds % 60;
        jda.getPresence().setActivity(Activity.playing("Uptime| "+uptimeHours + " hrs "+uptimeMinutes + " mins"));

    }
}
