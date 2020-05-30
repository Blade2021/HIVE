package rsystems.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import rsystems.HiveBot;

import java.util.ArrayList;

import static rsystems.HiveBot.LOGGER;

public class AutoRemove {

    public void removeUser(String id) {

        Guild guild = HiveBot.docGuild;

        // If user has honorary role, ignore
        if (guild.getMemberById(id).getRoles().contains(guild.getRoleById("716171458623307796"))) {
            return;
        }

        ArrayList<Role> rolesToRemove = new ArrayList<>();
        try {
            rolesToRemove.add(guild.getRoleById("469334775354621973")); // Radiant
            rolesToRemove.add(guild.getRoleById("698343546037731429")); //Yeoman
            rolesToRemove.add(guild.getRoleById("698369672646623242")); // Bannerman
        } catch (NullPointerException e) {

        }

        try {
            guild.modifyMemberRoles(guild.getMemberById(id), guild.getRolesByName("Inactive Staff", false), rolesToRemove).queue(success -> {

                User user = guild.getMemberById(id).getUser();
                TextChannel logChannel = guild.getTextChannelById(HiveBot.dataFile.getDatafileData().get("LogChannelID").toString());

                LOGGER.warning("Set " + user.getAsTag() + " as inactive staff");
                try {
                    logChannel.sendMessage("\uD83D\uDD10 " +  user.getAsTag() + " was set to inactive status").queue();
                } catch (NullPointerException e) {
                    LOGGER.severe("Failed to send message to log channel");
                }

                // Open a private message with the user to tell them
                user.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage("Hello " + user.getAsMention() + "\nI'm sending you this message to alert you that you have been put into INACTIVE status on DrZzz's discord server.  Please do know this is only being done as a security precaution.  Please message one of the staff for instructions on how to return to your position when ready.\n\nThank you.").queue(null, onFailure -> {
                        try {
                            logChannel.sendMessage("⚠ Failed to send inactive alert to " + user.getAsTag()).queue();
                        } catch (NullPointerException e) {
                            LOGGER.severe("Failed to send message to log channel");
                        }
                    });
                });
            });
        } catch (NullPointerException e) {
            //could not find user
        }

    }
}