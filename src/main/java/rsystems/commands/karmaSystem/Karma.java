package rsystems.commands.karmaSystem;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class Karma extends Command {

    private static final String[] ALIASES = new String[] {"ks"};
	
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
        reply(event, karmaString());
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        event.getMessage().delete().queue();
        channelReply(event, karmaString(), success -> {

            success.delete().queueAfter(60, TimeUnit.SECONDS);
        });
		
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

    @Override
    public String[] getAliases(){
        return ALIASES;
    }

}
