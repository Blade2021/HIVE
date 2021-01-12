package rsystems.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.util.Comparator;
import java.util.stream.Collectors;

public class Help extends Command {

    @Override
    public void dispatch(final User sender, final MessageChannel channel, final Message message, final String content, final GuildMessageReceivedEvent event)
    {
        final EmbedBuilder builder = new EmbedBuilder();

        final String prefix = Config.get("prefix");
        final String[] args = content.split("\\s+");

        for(Command c:HiveBot.dispatcher.getCommands()){
            if(c.getHelp() != null) {
                if (c.getName().equalsIgnoreCase(args[0])) {

                    builder.setTitle("Help | " + c.getName());
                    builder.setDescription(c.getHelp());
                }
            }
        }
        reply(event, new MessageBuilder().setEmbed(builder.build()).build());
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public String getHelp()
    {
        return "Prints a list of commands";
    }

    @Override
    public String getName()
    {
        return "help";
    }
}
