package rsystems.tasks;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
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

                //Get the last date of karma increment
                Timestamp lastSeenKarma = null;
                try {
                    lastSeenKarma = karmaSQLHandler.getTimestamp(member.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //Insert new user if not found in DB
                if (lastSeenKarma == null) {
                    try {
                        if (karmaSQLHandler.insertUser(member.getId(), member.getUser().getAsTag(), null, "KARMA")) {
                            LOGGER.severe("Failed to add " + member.getUser().getAsTag() + " to honeyCombDB");
                        } else {
                            LOGGER.info("Added " + member.getUser().getAsTag() + " to honeyCombDB. Table: KARMA");
                            karmaSQLHandler.overrideKarmaPoints(member.getId(), 5);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {

                    long daysPassed = ChronoUnit.DAYS.between(lastSeenKarma.toLocalDateTime(), Instant.now());
                    if (daysPassed >= 1) {
                        try {
                            karmaSQLHandler.addKarmaPoints(member.getIdLong(), Timestamp.from(Instant.now()), false);
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
