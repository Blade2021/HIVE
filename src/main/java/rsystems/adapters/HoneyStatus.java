package rsystems.adapters;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

public class HoneyStatus extends TimerTask {
    private JDA jda;
    private Guild guild;

    public HoneyStatus(JDA jda){
        this.jda = jda;
    }

    public HoneyStatus(JDA jda, Guild guild){
        this.jda = jda;
            this.guild = guild;
    }

    @Override
    public void run() {
        if (guild != null) {
            ArrayList<Member> members = new ArrayList<>();
            try {
                guild.getMembers().forEach(member -> {
                    if (member.getOnlineStatus().toString().equalsIgnoreCase("ONLINE")) {
                        members.add(member);
                    }
                });

                Random random = new Random();
                random.nextInt(members.size());

                jda.getPresence().setActivity(Activity.playing("Collecting pollen from " + members.get(random.nextInt(members.size())).getUser().getAsTag()));
            } catch (NullPointerException e) {
                System.out.println("Couldn't find members from guild: " + guild.getName());
            }
        }
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
