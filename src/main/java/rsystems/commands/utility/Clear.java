package rsystems.commands.utility;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.Config;
import rsystems.objects.Command;

import java.sql.SQLException;
import java.util.List;

public class Clear extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.MESSAGE_MANAGE;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        String[] args = content.split("\\s+");

        Integer msgCount = null;

        try{
            msgCount = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
        }

        if(msgCount != null) {
            List<Message> messages = event.getChannel().getHistory().retrievePast(msgCount + 1).complete();
            try{
                event.getChannel().purgeMessages(messages);
            } catch(Exception e){
                System.out.println("Something went wrong with the clear command.");
            }
        }
    }

    @Override
    public String getHelp() {
        String returnString = ("`{prefix}{command} [# of messages]`\n" +
                "Clear the number of messages provided (Less than 100) from the channel in reverse order.");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }
}
