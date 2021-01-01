package rsystems.events;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class NicknameListener extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event){

        String name = event.getNewNickname();
        name = processName(name);
        //event.getGuild().modifyNickname(event.getMember(),name).queue();
    }

    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        System.out.println(processName(event.getNewName()));
    }

    private String processName(String name){
        String newName = name;

        List<String> acceptedEmoji = new ArrayList<>();
        acceptedEmoji.add(":toolbox:");
        acceptedEmoji.add(":snowman:");

        System.out.println("name:"+name);

        if(!EmojiParser.extractEmojis(name).isEmpty()){
            for(String s: EmojiParser.extractEmojis(name)){
                String emojiAlias = EmojiParser.parseToAliases(s);
                if(acceptedEmoji.contains(emojiAlias))
                    continue;
                else
                    newName = newName.replace(s,"");

            }
        }
        if(name.equalsIgnoreCase(newName)) {
            System.out.println("null");
            return null;
        }
        return newName;
    }
}
