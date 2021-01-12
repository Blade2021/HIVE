package rsystems.commands.funCommands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.objects.Command;

import java.util.Random;

public class Order66 extends Command {

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

        return;

    }

	@Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

		String[] rand = {" Yes my lord.", " Yes My lord, The troops have been notified.",
                    " Yes my lord, Alright troops, move out!", " Right away my lord"};

		String[] gifLinks = {"https://tenor.com/view/heading-in-stormtrooper-starwars-gif-4902440", "https://tenor.com/view/dancing-darth-vader-storm-troopers-gif-5595478",
		"https://tenor.com/view/cooking-storm-trooper-pancakes-star-wars-gif-15568625", "https://tenor.com/view/roast-squad-star-wars-order-gif-10141605", "https://tenor.com/view/star-wars-rey-gif-14874010", "https://tenor.com/view/star-wars-pizza-darth-vader-han-solo-gif-4826490"};

		int index = new Random().nextInt(rand.length);
		int gifIndex = new Random().nextInt(gifLinks.length);

        reply(event, rand[index] + "\n\n" + gifLinks[gifIndex]);

    }

    @Override
    public String getHelp() {
        return "Just a test";
    }

}
