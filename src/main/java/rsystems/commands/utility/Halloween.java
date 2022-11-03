package rsystems.commands.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.objects.Command;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Halloween extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {

        final LocalDate date = LocalDate.now();
        final LocalDate holidayDate = LocalDate.of(date.getYear(),10,31);


        final long days = ChronoUnit.DAYS.between(date,holidayDate);
        final String jackoLantern = "\uD83C\uDF83";
        final String scareFace = "\uD83D\uDE31";

        if(days < 30){
            reply(event,String.format("%s%s%s ooooo... Sharpen those fangs!  Its time for Halloween season *(%d days left)*!  %s%s%s",jackoLantern,jackoLantern,jackoLantern,ChronoUnit.DAYS.between(date,holidayDate),jackoLantern,jackoLantern,jackoLantern));
        } else {
            reply(event, String.format("%s There are %d days till Halloween %s", jackoLantern, ChronoUnit.DAYS.between(date, holidayDate),scareFace));
        }

    }

    @Override
    public String getHelp() {
        return "Get the amount of days till the next Halloween!";
    }
}
