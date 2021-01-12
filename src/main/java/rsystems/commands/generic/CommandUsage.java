package rsystems.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

public class CommandUsage extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        if(handleEvent(content) != null){
            reply(event,"That command has been used " + handleEvent(content) + " times.");
        }
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        if(handleEvent(content) != null){
            reply(event,"That command has been used " + handleEvent(content) + " times.");
        }
    }

    @Override
    public String getHelp() {
        return null;
    }

    private Integer handleEvent(String content){
        Integer output = null;
        String[] args = content.split("\\s+");
        output = HiveBot.sqlHandler.checkUsage(args[0]);

        return output;
    }
}
