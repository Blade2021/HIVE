package rsystems.events;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;

import java.awt.*;
import java.sql.SQLException;


public class GuildMemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(event.getMember().getUser().getAvatarUrl() == null){
            System.out.println("default avatar detected!");
        }

        try {
            if(HiveBot.karmaSQLHandler.getKarma(event.getMember().getId()) == null){
                System.out.println("Adding member: " + event.getMember().getId());
                if(HiveBot.karmaSQLHandler.insertUser(event.getMember().getId(),event.getMember().getUser().getAsTag())){
                    System.out.println("success!");
                } else {
                    System.out.println("failed to add member");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String greetingMessage = null;
        try {
            greetingMessage = HiveBot.sqlHandler.grabRandomGreeting();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(greetingMessage != null){

            greetingMessage = greetingMessage.replace("{user}",String.format("**%s**",event.getMember().getEffectiveName()));

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Welcome")
                    .setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl())
                    .setColor(Color.decode("#69f591"))
                    .setDescription(greetingMessage);

            if(event.getUser().getAvatarUrl() == null){
                embedBuilder.appendDescription("\n\nWe encourage all members to use a custom avatar here on discord.  However this is __**NOT**__ required.");
            }

            TextChannel welcomeChannel = HiveBot.mainGuild().getTextChannelById(Config.get("WELCOME_CHANNEL"));
            if(welcomeChannel != null){
                welcomeChannel.sendMessage(embedBuilder.build()).queue();
            }
            embedBuilder.clear();
        }

    }
}
