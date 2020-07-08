package rsystems.events;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class NicknameListener extends ListenerAdapter {

    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event){
        String newNick = event.getNewNickname();

        if(EmojiParser.extractEmojis(newNick).size() > 0){
            ArrayList<Emoji> emojiArrayList = new ArrayList<>();
            EmojiParser.extractEmojis(newNick).forEach(emoji -> {
                //if(emoji.equalsIgnoreCase())
            });
        }
    }
}
