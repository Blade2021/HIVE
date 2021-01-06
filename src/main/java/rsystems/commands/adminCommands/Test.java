package rsystems.commands.adminCommands;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.events.NicknameListener;
import rsystems.objects.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Test extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        String nickname = event.getMember().getEffectiveName();
        if(EmojiParser.extractEmojis(nickname).contains("\uD83D\uDD28")){

            Collection<Emoji> collection = new ArrayList<>();
            collection.add(EmojiManager.getByUnicode("\uD83D\uDD28"));

            nickname = EmojiParser.removeEmojis(nickname,collection);
        } else {
            nickname = nickname + " \uD83D\uDD28";
        }

        event.getGuild().modifyNickname(event.getMember(),nickname).queue();

    }

    @Override
    public String getHelp() {
        return null;
    }
}
