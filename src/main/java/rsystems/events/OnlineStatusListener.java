package rsystems.events;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.adapters.RoleCheck;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static rsystems.HiveBot.*;

public class OnlineStatusListener extends ListenerAdapter {

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        if (event.getNewOnlineStatus().equals(OnlineStatus.ONLINE)) {

            /*
            KARMA SYSTEM
             */

            //Initiate the formatter for formatting the date into a set format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            //Get the current date
            LocalDate currentDate = LocalDate.now();
            //Format the current date into a set format
            String formattedCurrentDate = formatter.format(currentDate);

            //Get the last date of karma increment
            String lastSeenKarma = karmaSQLHandler.getDate(event.getMember().getId());

            //Insert new user if not found in DB
            if (lastSeenKarma.isEmpty()) {
                if (karmaSQLHandler.insertUser(event.getMember().getId(), event.getUser().getAsTag(), formattedCurrentDate, "KARMA")) {
                    karmaLogger.severe("Failed to add " + event.getUser().getAsTag() + " to honeyCombDB");
                } else {
                    karmaLogger.info("Added " + event.getUser().getAsTag() + " to honeyCombDB. Table: KARMA");
                    karmaSQLHandler.overrideKarmaPoints(event.getMember().getId(), 5);
                }
            } else {
                long daysPassed = ChronoUnit.DAYS.between(LocalDate.parse(lastSeenKarma, formatter), currentDate);
                if (daysPassed >= 1) {
                    if(RoleCheck.getRank(event.getGuild(),event.getMember().getId()) >= 1){
                        karmaSQLHandler.addKarmaPoints(event.getMember().getId(), formattedCurrentDate,true);
                    } else {
                        karmaSQLHandler.addKarmaPoints(event.getMember().getId(), formattedCurrentDate, false);
                    }
                } else if (event.getMember().getId().equalsIgnoreCase("313832264792539142")) {
                    System.out.println("Days Passed: " + daysPassed);
                }
            }


            /*
            STAFF AUTO REMOVE FUNCTIONS
             */

            try {
                // Only check for users on Doc's guild
                if (event.getGuild().getId().equalsIgnoreCase(docGuild.getId())) {

                    //Check to see if user is privileged
                    if (RoleCheck.getRank(event.getGuild(), event.getMember().getId()) >= 1) {

                        //Query the DB here
                        String lastSeen = sqlHandler.getDate(event.getMember().getId());

                        //User doesn't exist in DB
                        if (lastSeen.isEmpty()) {
                            if (sqlHandler.insertUser(event.getMember().getId(), event.getUser().getAsTag(), formattedCurrentDate)) {
                                LOGGER.severe("Failed to add " + event.getUser().getAsTag() + " to honeyCombDB");
                            } else {
                                LOGGER.info("Added " + event.getUser().getAsTag() + " to honeyCombDB");
                            }
                        } else {
                            //Member already exists in DB

                            //Last seen date does not equal current
                            if (!formattedCurrentDate.equals(lastSeen)) {

                                //Set date to current
                                sqlHandler.setDate("LastSeenTable", event.getMember().getId(), formattedCurrentDate);
                            }
                        }
                    }
                }
            } catch (NullPointerException e){

            }
        }
    }
}
