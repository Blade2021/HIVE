package rsystems.events;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;


public class GuildMemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        // Remove emoji's from username on join
        final String currentNick = event.getMember().getEffectiveName();
        if(!EmojiParser.extractEmojis(currentNick).isEmpty()){
            if(HiveBot.drZzzGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE))
                event.getGuild().modifyNickname(event.getMember(),EmojiParser.removeAllEmojis(currentNick)).reason("Removing emoji's on join").queue();
            else
                System.out.println("Emoji's found for member: " + event.getMember().getId());
        }

        if(event.getMember().getUser().getAvatarUrl() == null){
            System.out.println("default avatar detected!");
        }

        if(HiveBot.karmaSQLHandler.getKarma(event.getMember().getId()) == null){
            System.out.println("Adding member: " + event.getMember().getId());
            HiveBot.karmaSQLHandler.insertUser(event.getMember().getId(),event.getMember().getUser().getAsTag());
        }
    }
}
