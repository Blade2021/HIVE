package rsystems.adapters;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;

import java.util.List;

public class RoleCheck {
    public static int getRank(GenericGuildMessageEvent event, String id){
        try {
            List<Role> memberRoles = event.getGuild().getMemberById(id).getRoles();

            if (event.getGuild().getMemberById(id).hasPermission(Permission.ADMINISTRATOR)) {
                return 3;
            }
            if (memberRoles.toString().contains("Yeoman")) {
                return 2;
            }
            if (memberRoles.toString().contains("Bannerman")) {
                return 1;
            }
        } catch(NullPointerException e){
            event.getChannel().sendMessage("Could not get roles of user").queue();
        }
        return 0;
    }

}
