package rsystems.tasks;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;

import static rsystems.HiveBot.*;
import static rsystems.HiveBot.karmaSQLHandler;

public class AddKarmaPoints extends TimerTask {

    @Override
    public void run() {

        for (Member member : HiveBot.mainGuild().getMembers()) {

            if (member.getUser().isBot()) {
                return;
            }

            if (member.getOnlineStatus().equals(OnlineStatus.ONLINE)) {

                //Initiate the formatter for formatting the date into a set format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                //Get the current date
                LocalDate currentDate = LocalDate.now();
                //Format the current date into a set format
                String formattedCurrentDate = formatter.format(currentDate);

                //Get the last date of karma increment
                String lastSeenKarma = null;
                try {
                    lastSeenKarma = karmaSQLHandler.getDate(member.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //Insert new user if not found in DB
                if (lastSeenKarma.isEmpty()) {
                    try {
                        if (karmaSQLHandler.insertUser(member.getId(), member.getUser().getAsTag(), formattedCurrentDate, "KARMA")) {
                            LOGGER.severe("Failed to add " + member.getUser().getAsTag() + " to honeyCombDB");
                        } else {
                            LOGGER.info("Added " + member.getUser().getAsTag() + " to honeyCombDB. Table: KARMA");
                            karmaSQLHandler.overrideKarmaPoints(member.getId(), 5);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    long daysPassed = ChronoUnit.DAYS.between(LocalDate.parse(lastSeenKarma, formatter), currentDate);
                    if (daysPassed >= 1) {
                        try {
                            karmaSQLHandler.addKarmaPoints(member.getId(), formattedCurrentDate, false);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Adding point to user: " + member.getUser().getAsTag());
                }
            }

        }

    }


}
