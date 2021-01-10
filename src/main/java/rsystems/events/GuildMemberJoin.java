package rsystems.events;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;


public class GuildMemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(event.getMember().getUser().getAvatarUrl() == null){
            System.out.println("default avatar detected!");
        }

        if(HiveBot.karmaSQLHandler.getKarma(event.getMember().getId()) == null){
            System.out.println("Adding member: " + event.getMember().getId());
            if(HiveBot.karmaSQLHandler.insertUser(event.getMember().getId(),event.getMember().getUser().getAsTag())){
                System.out.println("success!");
            } else {
                System.out.println("failed to add member");
            }
        }
    }
}
