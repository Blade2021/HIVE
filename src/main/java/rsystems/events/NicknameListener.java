package rsystems.events;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class NicknameListener extends ListenerAdapter {

    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event){
        String newNick = event.getNewNickname();

        try {
            if (EmojiParser.extractEmojis(newNick).size() > 0) {

                for (String emoji : EmojiParser.extractEmojis(newNick)) {
                    newNick = newNick.replaceAll(emoji, "");
                }

                event.getGuild().modifyNickname(event.getMember(), newNick).queue();
            }
        }catch(NullPointerException e){
            System.out.println("Nickname presented NULL Value?");
        }
    }
}
