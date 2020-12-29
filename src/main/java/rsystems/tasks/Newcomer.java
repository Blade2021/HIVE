package rsystems.tasks;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import rsystems.HiveBot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimerTask;

public class Newcomer extends TimerTask {

    @Override
    public void run() {

        Role newComerRole = HiveBot.drZzzGuild().getRoleById("777160509715775519");

        if(newComerRole != null) {
            List<Member> members = HiveBot.drZzzGuild().getMembersWithRoles(newComerRole);
            for (Member m : members) {
                LocalDateTime localDateTime = LocalDateTime.now();

                if (m.getTimeJoined().toLocalDateTime().plusDays(30).isBefore(localDateTime))
                    HiveBot.drZzzGuild().removeRoleFromMember(m, newComerRole).queue();
                    System.out.println(String.format("USER:%d Removing newcomer role"));

            }
        }
    }
}
