package rsystems.events;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.util.ArrayList;
import java.util.List;


public class NicknameListener extends ListenerAdapter {

    static List<Member> inProcess = new ArrayList<>();

    @Override
    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event){
        final Member member = event.getMember();
        if(member != null) {
            String name = member.getEffectiveName();
            String newNick = processName(member, name);

            if (newNick != null) {
                event.getGuild().modifyNickname(event.getMember(), newNick).queue();
            }
        }
    }

    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        final Member member = HiveBot.drZzzGuild().getMemberById(event.getUser().getIdLong());
        if(member != null){
            if(member.getEffectiveName().equalsIgnoreCase(event.getNewName())){
                String newNick = processName(member, event.getNewName());
                if(newNick != null){
                    HiveBot.drZzzGuild().modifyNickname(member,newNick).queue();
                }
            }
        }
    }

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event){
        final Member member = HiveBot.drZzzGuild().getMemberById(event.getUser().getIdLong());
        if(member != null){
            String nickname = member.getEffectiveName();
            String processedNickname = processName(member, nickname);
            if(processedNickname != null){
                HiveBot.drZzzGuild().modifyNickname(member,processedNickname).queue();
            }
        }
    }

    public static String processName(Member member, String name) {
        if (HiveBot.drZzzGuild().getSelfMember().canInteract(member)) {

            if (inProcess.contains(member)) {
                return null;
            } else {
                inProcess.add(member);

                String newName = name;

                if (newName == null)
                    return null;

                List<String> acceptedEmoji = new ArrayList<>();
                acceptedEmoji.add(":toolbox:");
                acceptedEmoji.add(":snowman:");


                if (EmojiParser.extractEmojis(name).size() >= 1) {
                    for (String s : EmojiParser.extractEmojis(name)) {

                        String emojiAlias = EmojiParser.parseToAliases(s);

                        if (acceptedEmoji.contains(emojiAlias))
                            continue;
                        else
                            //System.out.println("Not allowing: " + emojiAlias);
                        newName = newName.replaceAll(s, "");

                    }
                }
                if (name.equalsIgnoreCase(newName)) {
                    //System.out.println("null");
                    inProcess.remove(member);
                    return null;
                }

                inProcess.remove(member);
                return newName;
            }
        }
        return null;
    }
}
