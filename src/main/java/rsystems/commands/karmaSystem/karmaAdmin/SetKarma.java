package rsystems.commands.karmaSystem.karmaAdmin;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class SetKarma extends Command {

	@Override
	public Integer getPermissionIndex() {
		return 32768;
	}
	
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) throws SQLException {
		
		String[] args = content.split("\\s+");
		if(args.length >= 2){
			int karmaAmount = Integer.parseInt(args[0]);
			
			Long userID = null;
				try{
					userID = Long.valueOf(args[1]);
					if(HiveBot.karmaSQLHandler.overrideKarma(String.valueOf(userID),karmaAmount))
						event.getMessage().addReaction("\uD83D\uDCE8").queue();
				} catch (NumberFormatException e){
					reply(event,"Bad number format");
				}
		} else 
			reply(event, "Not enough parameters supplied");
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) throws SQLException {

        String[] args = content.split("\\s+");
		if(args.length >= 2){
			int karmaAmount = Integer.parseInt(args[0]);
			
			if(event.getMessage().getMentionedMembers().size() > 0){
				for(Member m:event.getMessage().getMentionedMembers()){
					HiveBot.karmaSQLHandler.overrideKarma(m.getId(),karmaAmount);
				}
				event.getMessage().addReaction("\uD83D\uDCE8").queue();
			} else {
				Long userID = null;
				try{
					userID = Long.valueOf(args[1]);
					if(HiveBot.karmaSQLHandler.overrideKarma(String.valueOf(userID),karmaAmount))
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
        return null;
    }

    @Override
    public String getName() {
        return "SetKarma";
    }

}
