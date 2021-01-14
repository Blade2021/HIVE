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

import java.awt.*;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Help extends Command {

    @Override
    public void dispatch(final User sender, final MessageChannel channel, final Message message, final String content, final GuildMessageReceivedEvent event)
    {
        final EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.decode("#21ff67"));

        final String[] args = content.split("\\s+");
        if(args[0].isEmpty()){
            builder.setTitle("Help Command");
            builder.setDescription(this.getHelp());
        } else {

            builder.setDescription("No help file found for that command.\n\nCheck back later or fill out a request [here](https://github.com/blade2021/HIVE).");
            for (Command c : HiveBot.dispatcher.getCommands()) {
                if (c.getHelp() != null) {

                    if (c.getName().equalsIgnoreCase(args[0])) {
                        builder.setTitle("Help | " + c.getName());
                        builder.setDescription(c.getHelp());
                    } else {
                        if(c.getAliases().length >= 1){
                            for(String alias:c.getAliases()){
                                if(args[0].equalsIgnoreCase(alias)){
                                    builder.setTitle("Help | " + c.getName());
                                    builder.setDescription(c.getHelp());
                                }
                            }
                        }
                    }
                }
            }
        }

        channelReply(event, new MessageBuilder().setEmbed(builder.build()).build());
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public String getHelp()
    {
        return "Prints helpful information about a command.\n\n"+
                "**Helpful Notes**:\n"+
                "All required arguments to a command are wrapped in \"[ ]\"\n"+
                "Any \"optional\" arguments are wrapped in \"{ }\"\n\n"+
                "Please use this help function as needed to understand what each command does.  Want to improve one or more commands?  Submit a FR to the HIVE Repo!";
    }

    @Override
    public String getName()
    {
        return "help";
    }
}
