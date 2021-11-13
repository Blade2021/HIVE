package rsystems.commands.adminCommands;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.events.NicknameListener;
import rsystems.objects.Command;


public class Test extends Command {
    @Override
    public Integer getPermissionIndex() {
        return 2;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

    }

    @Override
    public String getHelp() {
        return null;
    }
}
