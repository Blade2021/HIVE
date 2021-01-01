package rsystems.commands.karmaSystem.karmaAdmin;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.time.temporal.ChronoUnit;

public class SetPoints extends Command {

	@Override
	public Integer getPermissionIndex() {
		return 2;
	}

	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
		String[] args = content.split("\\s+");
		if(args.length >= 2){
			int karmaPoints = Integer.parseInt(args[0]);
			
			Long userID;
				try{
					userID = Long.valueOf(args[1]);
					if(HiveBot.karmaSQLHandler.overrideKarmaPoints(String.valueOf(userID),karmaPoints))
						event.getMessage().addReaction("\uD83D\uDCE8").queue();
				} catch (NumberFormatException e){
					reply(event,"Bad number format");
				}
		} else 
			reply(event, "Not enough parameters supplied");
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        String[] args = content.split("\\s+");
		if(args.length >= 2){
			int karmaPoints = Integer.parseInt(args[0]);
			
			if(event.getMessage().getMentionedMembers().size() > 0){
				for(Member m:event.getMessage().getMentionedMembers()){
					HiveBot.karmaSQLHandler.overrideKarmaPoints(m.getId(),karmaPoints);
				}
				event.getMessage().addReaction("\uD83D\uDCE8").queue();
			} else {
				Long userID;
				try{
					userID = Long.valueOf(args[1]);
					if(HiveBot.karmaSQLHandler.overrideKarmaPoints(String.valueOf(userID),karmaPoints))
						event.getMessage().addReaction("\uD83D\uDCE8").queue();
				} catch (NumberFormatException e){
					reply(event,"Bad number format");
				}
			}
		} else 
			reply(event, "Not enough parameters supplied");
		
    }

    @Override
    public String getHelp() {
        return "Just a test";
    }

}
