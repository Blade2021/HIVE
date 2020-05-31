package rsystems.adapters;

import net.dv8tion.jda.api.entities.*;
import rsystems.HiveBot;
import rsystems.handlers.SQLHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import static rsystems.HiveBot.LOGGER;

public class AutoRemove extends TimerTask {

    private static SQLHandler sqlHandler = new SQLHandler();

    @Override
    public void run() {

        //Initiate a hashmap for ID and Dates to be stored
        HashMap<String,String> dateMap = new HashMap<>();

        //Grab all values from DB
        dateMap.putAll(sqlHandler.getAllUserDates());

        //Parse through each KeyEntry
        for (Map.Entry<String, String> entry : dateMap.entrySet()) {

            try {
                //Parse the String back into a date
                LocalDate date = LocalDate.parse(entry.getValue(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                LocalDate currentDate = LocalDate.now();

                long daysPassed = ChronoUnit.DAYS.between(date, currentDate);

                //If the date is over 30 days passed
                if (daysPassed > 45) {
                    LOGGER.info("Attempting to put user in inactive status ID: " + entry.getKey());

                    //Place user into inactive status
                    removeUser(entry.getKey());
                } else {
                    System.out.println("ID: " + entry.getKey() + " Days Passed: " + daysPassed);
                }
            } catch (DateTimeParseException e){
                System.out.println("Could not find date for: " + entry.getKey());
            }
        }

    }


    public void removeUser(String id) {
        System.out.println("Inactive status method starting....");

        Guild guild = HiveBot.docGuild;

        try {
            // If user has honorary role, ignore
            if (guild.getMemberById(id).getRoles().contains(guild.getRoleById("716171458623307796"))) {
                return;
            }
        } catch(NullPointerException e){
            LOGGER.severe("Could not find honorary guild role");
        }

        ArrayList<Role> rolesToRemove = new ArrayList<>();
        try {
            rolesToRemove.add(guild.getRoleById("469334775354621973")); // Radiant
            rolesToRemove.add(guild.getRoleById("698343546037731429")); //Yeoman
            rolesToRemove.add(guild.getRoleById("698369672646623242")); // Bannerman
        } catch (NullPointerException e) {

        }

        try {
            String previousRole = "";

            for (Role role : guild.getMemberById(id).getRoles()) {
                if(role.getId().equalsIgnoreCase("469334775354621973")){
                    previousRole = role.getName();
                } else if(role.getId().equalsIgnoreCase("698343546037731429")){
                    previousRole = role.getName();
                } else if(role.getId().equalsIgnoreCase("698369672646623242")){
                    previousRole = role.getName();
                }
            }

            final String finalPreviousRole = previousRole;
            guild.modifyMemberRoles(guild.getMemberById(id), guild.getRolesByName("Inactive Staff", false), rolesToRemove).queue(success -> {

                User user = guild.getMemberById(id).getUser();
                TextChannel logChannel = guild.getTextChannelById(HiveBot.dataFile.getDatafileData().get("LogChannelID").toString());

                LOGGER.warning("Set " + user.getAsTag() + " as inactive staff from Role: " + finalPreviousRole);
                try {
                    logChannel.sendMessage("\uD83D\uDD10 " +  user.getAsTag() + " was set to inactive status from Role: " + finalPreviousRole).queue();
                } catch (NullPointerException e) {
                    LOGGER.severe("Failed to send message to log channel");
                }

                // Open a private message with the user to tell them
                user.openPrivateChannel().queue((channel) ->
                {
                    channel.sendMessage("Hello " + user.getAsMention() + "\nI'm sending you this message to alert you that you have been put into INACTIVE Staff status on DrZzz's discord server.  Please do know this is only being done as a security precaution.  Please message one of the staff for instructions on how to return to your position when ready.\n\nThank you.").queue(null, onFailure -> {
                        try {
                            logChannel.sendMessage("⚠ Failed to send inactive alert to " + user.getAsTag()).queue();
                        } catch (NullPointerException e) {
                            LOGGER.severe("Failed to send message to log channel");
                        }
                    });
                });

                //Remove user from DB
                sqlHandler.removeUser(id);

            });
        } catch (NullPointerException e) {
            //could not find user
        }

    }
}