package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.events.References;
import rsystems.objects.Command;

public class Reload extends Command {

    @Override
    public Integer getPermissionIndex() {
        return 128;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        reply(event, "Reloading all data...\nPlease standby.");

        References.loadReferences();
    }

    @Override
    public String getHelp() {

        String returnString ="`{prefix}{command}`\n" +
                "This command will soft-reset the bot, reloading all references and refreshing the database.";

        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }
}
