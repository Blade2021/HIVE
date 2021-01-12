package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.objects.Command;

import java.util.List;

public class Clear extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 32;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        String[] args = content.split("\\s+");

        Integer msgCount = null;
        
        try{
            msgCount = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
        }
        
        if(msgCount != null) {
            List<Message> messages = event.getChannel().getHistory().retrievePast(msgCount + 1).complete();
            event.getChannel().deleteMessages(messages).queue();
        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
