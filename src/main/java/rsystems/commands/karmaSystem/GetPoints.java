package rsystems.commands.karmaSystem;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.time.temporal.ChronoUnit;

public class GetPoints extends Command {
	
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
        reply(event, pointsString(event.getAuthor().getId()));
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        reply(event, pointsString(event.getAuthor().getId()));
		
    }
	
	private String pointsString(String userID){
		
		int karmaPoints = HiveBot.karmaSQLHandler.getAvailableKarmaPoints(userID);
        return " You have " + karmaPoints + " available points";
		
	}

    @Override
    public String getHelp() {
        return "Just a test";
    }

}
