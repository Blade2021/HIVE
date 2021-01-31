package rsystems.commands.karmaSystem;

import net.dv8tion.jda.api.entities.Member;
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

        Member member = HiveBot.mainGuild().getMemberById(sender.getIdLong());
        if(member != null) {
            reply(event, karmaString(member));
        }
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        Member member = HiveBot.mainGuild().getMemberById(sender.getIdLong());
        if(member != null) {
            reply(event, karmaString(member));
        }
		
    }
	
	private String karmaString(Member member){
		
		int currentKarma = HiveBot.karmaSQLHandler.getKarma(member.getId());
		return String.format("%s, You currently have %d Karma",member.getEffectiveName(),currentKarma);
		
	}

    @Override
    public String getHelp() {
        return null;
    }

}
