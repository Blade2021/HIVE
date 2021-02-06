package rsystems.events;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;

import java.awt.*;


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

        String greetingMessage = HiveBot.sqlHandler.grabRandomGreeting();
        if(greetingMessage != null){

            greetingMessage = greetingMessage.replace("{user}",String.format("**%s**",event.getUser().getAsMention()));

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Welcome")
                    .setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl())
                    .setColor(Color.decode("#69f591"))
                    .setDescription(greetingMessage);

            TextChannel welcomeChannel = HiveBot.mainGuild().getTextChannelById(Config.get("WELCOME_CHANNEL"));
            if(welcomeChannel != null){
                welcomeChannel.sendMessage(embedBuilder.build()).queue();
            }
            embedBuilder.clear();
        }

    }
}
