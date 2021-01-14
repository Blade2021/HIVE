package rsystems.commands.modCommands;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.objects.Command;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetEmoji extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event,getEmojis(message).toString());
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        int index = 0;

        List<String> emojiList = EmojiParser.extractEmojis(message.getContentDisplay());
        if(emojiList.isEmpty()){
            reply(event,"No emoji's found on that message. :potato:");
        } else {
            for (String emojiString : emojiList) {
                if (index < 5) {
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
    }

    @Override
    public String getHelp() {

        String returnString ="`{prefix}{command} [Emoji]`\n\n" +
                "Not all emoji's can be used by HIVE.  This is due to the library we use.  Also not all reactions are emojis.\n";

        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
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
