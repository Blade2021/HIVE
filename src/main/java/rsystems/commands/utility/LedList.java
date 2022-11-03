package rsystems.commands.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class LedList extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        try {
            ArrayList<String> ledList = HiveBot.database.getLEDList();
            String ledListString = ledList.toString();
            ledListString = ledListString.replaceFirst("\\[","");
            ledListString = ledListString.replaceFirst("\\]","");

            Random random = new Random();
            int randInt = random.nextInt(ledList.size());

            int randQty = random.nextInt(1000);

            String outputString = ledListString + "\n\n" +
                    "**Example Usage:** " + HiveBot.prefix + "led " + ledList.get(randInt) + " " + randQty;

            reply(event, outputString);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return "{prefix}{commmand}\n" +
                "\n" +
                "Get a list of supported LED Types for the LED command.";
    }
}
