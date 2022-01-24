package rsystems.tasks;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import rsystems.Config;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimerTask;

public class GrabKarmaTopThree extends TimerTask {

    @Override
    public void run() {

        try {

            LinkedHashMap<Integer, Long> topThreeList = HiveBot.karmaSQLHandler.getTopThree();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Weekly Top 3");
            builder.setDescription("Congrats to this weeks Top 3 Karma receivers!");
            builder.setColor(HiveBot.getColor(HiveBot.colorType.GENERIC));

            for(Map.Entry<Integer,Long> entry:topThreeList.entrySet()){
                if(HiveBot.mainGuild().getMemberById(entry.getValue()) != null){
                    builder.addField(String.valueOf(entry.getKey()),HiveBot.mainGuild().getMemberById(entry.getValue()).getEffectiveName(),false);
                }
            }

            TextChannel announcementChannel = HiveBot.mainGuild().getTextChannelById(Config.get("announcementChannel"));
            if (announcementChannel != null) {
                announcementChannel.sendMessageEmbeds(builder.build()).queue();
            }
            builder.clear();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
