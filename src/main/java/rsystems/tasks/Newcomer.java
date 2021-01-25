package rsystems.tasks;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import rsystems.Config;
import rsystems.HiveBot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TimerTask;

public class Newcomer extends TimerTask {

    @Override
    public void run() {

        Role newComerRole = HiveBot.mainGuild().getRoleById(Config.get("NewComerRoleID"));

        if(newComerRole != null) {
            List<Member> members = HiveBot.mainGuild().getMembersWithRoles(newComerRole);
            for (Member m : members) {
                try {
                    LocalDateTime localDateTime = LocalDateTime.now();

                    if (m.getTimeJoined().toLocalDateTime().plusDays(30).isBefore(localDateTime)) {
                        HiveBot.mainGuild().removeRoleFromMember(m, newComerRole).queue();
                        System.out.println(String.format("USER:%d Removing newcomer role", m.getIdLong()));
                    }
                }catch(NullPointerException | IllegalArgumentException e){
                    System.out.println("An error occured for Member:" + m.getEffectiveName());
                }

            }
        }
    }
}
