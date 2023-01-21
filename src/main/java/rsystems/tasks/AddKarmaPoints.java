package rsystems.tasks;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.TimerTask;

public class AddKarmaPoints extends TimerTask {

    @Override
    public void run() {

        for (final Member member : HiveBot.mainGuild().getMembers()) {
            if (member.getUser().isBot()) {
                return;
            }


            if (member.getOnlineStatus().equals(OnlineStatus.ONLINE)) {
                try {
                    //Get the last date of karma increment
                    Timestamp lastSeenKarma = HiveBot.karmaSQLHandler.getTimestamp(member.getIdLong());

                    if (lastSeenKarma != null) {
                        long daysPassed = ChronoUnit.DAYS.between(lastSeenKarma.toInstant(), Instant.now());
                        if (daysPassed >= 1) {
                            Logger logger = LoggerFactory.getLogger(this.getClass());
                            logger.info("Incrementing karma point for User: {}  ID:{}", member.getUser().getAsTag(), member.getIdLong());
                            HiveBot.karmaSQLHandler.addKarmaPoints(member.getIdLong(), Timestamp.from(Instant.now()), false);

                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
