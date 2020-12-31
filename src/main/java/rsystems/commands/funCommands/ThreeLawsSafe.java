package rsystems.commands.funCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.objects.Command;

import java.awt.*;
import java.time.temporal.ChronoUnit;

public class ThreeLawsSafe extends Command {
	
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
		
        return;
		
    }
	
	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle("3 Laws of BoTs")
		.setThumbnail("http://marc-jennings.co.uk/wp-content/uploads/2020/04/robot_1f916.png")
		.addField("`Law 1`", "A BoT will **NOT** trigger another bot", false)
		.addField("`Law 2`", "A BoT will **NOT** trigger itself", false)
		.addField("`Law 3`", "A BoT will **NOT** kill hoomans", false)
		.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl())
		.setColor(Color.CYAN);

        reply(event, embed.build());
		
		embed.clear();
		
    }

    @Override
    public String getHelp() {
        return "Just a test";
    }

}
