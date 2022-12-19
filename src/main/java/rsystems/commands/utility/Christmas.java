package rsystems.commands.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.objects.Command;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Christmas extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        Instant now = Instant.now();

        LocalDate date = LocalDate.now();
        LocalDate holidayDate = LocalDate.of(date.getYear(),12,25);

        reply(event,String.format("There are %d days till Christmas", ChronoUnit.DAYS.between(date,holidayDate)));
    }

    @Override
    public String getHelp() {
        return "Days till Christmas";
    }
}
