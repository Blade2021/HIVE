package rsystems.commands.modCommands;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.objects.Command;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GetEmoji extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event,getEmojis(message).toString());
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        int index = 0;

        for(String emojiString:EmojiParser.extractEmojis(message.getContentDisplay())) {
            if(index < 5) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setDescription(String.format("Emoji: %s\n\nUnicode: `%s`\nHTML Decimal: `%s`\nAliases: `%s`", emojiString, emojiToUnicode(emojiString), EmojiParser.parseToHtmlDecimal(emojiString), EmojiParser.parseToAliases(emojiString)));
                embedBuilder.setColor(Color.green);
                reply(event, embedBuilder.build());
                embedBuilder.clear();
                index++;
            } else
                break;
        }
    }

    @Override
    public String getHelp() {
        return null;
    }

    private String emojiToUnicode(String emoji){
        StringBuilder outputString = new StringBuilder();
        emoji.codePoints().forEachOrdered(code -> {
            String hex = Integer.toHexString(code).toUpperCase();

            if (hex.length() < 4)
                hex = '0' + hex;
            outputString.append("\\u").append(hex);
        });
        return outputString.toString();
    }

    private Map<String,String> getEmojis(Message message){
        Map<String,String> emojiMap = new HashMap<>();

        for(String emojiString:EmojiParser.extractEmojis(message.getContentDisplay())){
            StringBuilder builder = new StringBuilder();
            emojiString.codePoints().forEachOrdered(code -> {
                char[] charArray = Character.toChars(code);
                String hex = Integer.toHexString(code).toUpperCase();

                if(hex.length() < 4)
                    hex = '0' + hex;

                builder.append("\\u").append(hex);

                if(charArray.length>1)
                {
                    String hex0 = Integer.toHexString(charArray[0]).toUpperCase();
                    String hex1 = Integer.toHexString(charArray[1]).toUpperCase();
                    while(hex0.length()<4)
                        hex0 = "0"+hex0;
                    while(hex1.length()<4)
                        hex1 = "0"+hex1;

                    builder.append("\\u").append(hex0).append("\\u").append(hex1);
                }

                emojiMap.putIfAbsent(emojiString,builder.toString());
            });
        }

        return emojiMap;
    }
}
