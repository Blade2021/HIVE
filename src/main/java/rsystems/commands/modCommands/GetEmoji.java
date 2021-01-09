package rsystems.commands.modCommands;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.objects.Command;

import java.util.HashMap;
import java.util.Map;

public class GetEmoji extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event,getEmojis(message).toString());
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        reply(event,getEmojis(message).toString());
    }

    @Override
    public String getHelp() {
        return null;
    }

    private Map<String,String> getEmojis(Message message){
        Map<String,String> emojiMap = new HashMap<>();

        for(String emojiString:EmojiParser.extractEmojis(message.getContentDisplay())){
            String unicode = EmojiParser.parseToUnicode(emojiString);

            System.out.println(unicode);
            emojiMap.putIfAbsent(emojiString,"\\"+ EmojiParser.parseToUnicode(emojiString));
        }

        return emojiMap;
    }
}
