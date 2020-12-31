package rsystems.commands.karmaSystem;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.time.temporal.ChronoUnit;

public class Karma extends Command {
	
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
        reply(event, karmaString());
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        reply(event, karmaString());
		
    }
	
	private String karmaString(){
		
		String karmaExplanation = (String) HiveBot.dataFile.getData("KarmaExplanationShort");
        karmaExplanation = karmaExplanation.replace("{kPosIcon}", "<:KU:717177145717424180>");
        karmaExplanation = karmaExplanation.replace("{kNegIcon}", "<:KD:717177177849724948> ");
		
		return karmaExplanation;
	}

    @Override
    public String getHelp() {
        return "Just a test";
    }

}
