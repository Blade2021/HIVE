package rsystems.adapters;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import rsystems.Config;

import java.util.List;

import static rsystems.HiveBot.LOGGER;

public class RoleCheck {

    public static int getRank(GenericGuildMessageEvent event, String id){
        try {
            List<Role> memberRoles = event.getGuild().getMemberById(id).getRoles();

            if (id.equalsIgnoreCase(Config.get("OWNER_ID"))){
                return 4;
            }

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

    public static Boolean checkRank(Message message, Member member, Command command){
        try {
            List<Role> memberRoles = message.getGuild().getMemberById(member.getId()).getRoles();
            int highestRankFound = 0;

            if (member.getId().equalsIgnoreCase(Config.get("OWNER_ID"))){
                highestRankFound = 4;
            } else if (message.getGuild().getMemberById(member.getId()).hasPermission(Permission.ADMINISTRATOR)) {
                highestRankFound = 3;
            } else if (memberRoles.toString().contains("Yeoman")) {
                highestRankFound = 2;
            } else if (memberRoles.toString().contains("Bannerman")) {
                highestRankFound = 1;
            }

            if(highestRankFound >= command.getRank()){
                LOGGER.info(command.getCommand() + " called by " + member.getUser().getAsTag());
                return true;
            } else {
                LOGGER.warning(command.getCommand() + " attempted to be called by " + member.getUser().getAsTag() + " without access.  UserRank: " + highestRankFound);
                message.addReaction("🚫").queue();
                message.getChannel().sendMessage(member.getAsMention() + " You do not have access to that command").queue();
            }
        } catch(NullPointerException e){
            message.getChannel().sendMessage("Could not get roles of user").queue();
        } catch(PermissionException e){
            System.out.println("Missing permission: " + e.getPermission() + " in channel: " + message.getChannel());
        }
        return false;
    }

    public static int getRank(Guild guild, String id){
        try {
            List<Role> memberRoles = guild.getMemberById(id).getRoles();

            if (id.equalsIgnoreCase(Config.get("OWNER_ID"))){
                return 4;
            }

            if (guild.getMemberById(id).hasPermission(Permission.ADMINISTRATOR)) {
                return 3;
            }
            if (memberRoles.toString().contains("Yeoman")) {
                return 2;
            }
            if (memberRoles.toString().contains("Bannerman")) {
                return 1;
            }
        } catch(NullPointerException e){
        }
        return 0;
    }

}
