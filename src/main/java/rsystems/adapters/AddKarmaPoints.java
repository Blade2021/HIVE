package rsystems.adapters;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import rsystems.HiveBot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.TimerTask;

import static rsystems.HiveBot.*;
import static rsystems.HiveBot.karmaSQLHandler;

public class AddKarmaPoints extends TimerTask {

    private Guild guild;

    public AddKarmaPoints(Guild guild) {
        this.guild = guild;
    }

    @Override
    public void run() {

        for(Member member:guild.getMembers()){

            if(member.getUser().isBot()){
                return;
            }

            if(member.getOnlineStatus().equals(OnlineStatus.ONLINE)){

                //Initiate the formatter for formatting the date into a set format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                //Get the current date
                LocalDate currentDate = LocalDate.now();
                //Format the current date into a set format
                String formattedCurrentDate = formatter.format(currentDate);

                //Get the last date of karma increment
                String lastSeenKarma = karmaSQLHandler.getDate(member.getId());

                //Insert new user if not found in DB
                if (lastSeenKarma.isEmpty()) {
                    if (karmaSQLHandler.insertUser(member.getId(), member.getUser().getAsTag(), formattedCurrentDate, "KARMA")) {
                        LOGGER.severe("Failed to add " + member.getUser().getAsTag() + " to honeyCombDB");
                    } else {
                        LOGGER.info("Added " + member.getUser().getAsTag() + " to honeyCombDB. Table: KARMA");
                        karmaSQLHandler.overrideKarmaPoints(member.getId(), 5);
                    }
                } else {
                    long daysPassed = ChronoUnit.DAYS.between(LocalDate.parse(lastSeenKarma, formatter), currentDate);
                    if (daysPassed >= 1) {
                        karmaSQLHandler.addKarmaPoints(member.getId(),formattedCurrentDate);
                        System.out.println("Adding point to user: " + member.getUser().getAsTag());
                    }
                }
            }
        }

    }
}