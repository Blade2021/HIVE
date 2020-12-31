package rsystems.commands.karmaSystem;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

public class GetKarma extends Command {
	
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
        reply(event, karmaString(sender));
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        reply(event, karmaString(sender));
		
    }
	
	private String karmaString(User user){
		
		int currentKarma = HiveBot.karmaSQLHandler.getKarma(user.getId());
		return String.format("%s, You currently have %d Karma",user.getName(),currentKarma);
		
	}

    @Override
    public String getHelp() {
        return "Just a test";
    }

}
