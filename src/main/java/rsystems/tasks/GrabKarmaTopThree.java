package rsystems.tasks;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import rsystems.Config;
import rsystems.HiveBot;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class GrabKarmaTopThree extends TimerTask {

    @Override
    public void run() {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("EST"));
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if (day == Calendar.TUESDAY) {

            ZoneId etZoneId = ZoneId.of("America/New_York");

            LocalDateTime currentDateTime = LocalDateTime.now();
            ZonedDateTime currentETime = currentDateTime.atZone(etZoneId);

            if (currentETime.getHour() == 14) {

                if (currentETime.getMinute() <= 10) {
                    try {
                        LinkedHashMap<Integer, Long> topThreeList = HiveBot.karmaSQLHandler.getTopThree();

                        final Role weeklyTop3Role = HiveBot.mainGuild().getRoleById(Config.get("WeeklyTopThree_RoleID"));

                        // REMOVE TOP 3 FROM MEMBERS WHO DIDNT QUALIFY
                        for (Member member : HiveBot.mainGuild().getMembersWithRoles(weeklyTop3Role)) {
                            if (topThreeList.containsValue(member.getIdLong())) {
                                continue;
                            } else {
                                HiveBot.mainGuild().modifyMemberRoles(member, null, weeklyTop3Role).queue();
                            }


                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setTitle("Weekly Top 3");
                            builder.setDescription("Congrats to this weeks Top 3 Karma receivers!");
                            builder.setColor(HiveBot.getColor(HiveBot.colorType.GENERIC));

                            for (Map.Entry<Integer, Long> entry : topThreeList.entrySet()) {

                                Member lookupMember = HiveBot.mainGuild().retrieveMemberById(entry.getValue()).complete();
                                builder.addField(String.valueOf(entry.getKey()), lookupMember.getEffectiveName(), false);

                                if (!lookupMember.getRoles().contains(weeklyTop3Role)) {
                                    HiveBot.mainGuild().modifyMemberRoles(lookupMember, null, weeklyTop3Role).queue();
                                }
                            }

                            TextChannel announcementChannel = HiveBot.mainGuild().getTextChannelById(Config.get("announcementChannel"));
                            if (announcementChannel != null) {
                                announcementChannel.sendMessageEmbeds(builder.build()).queue();
                            }
                            builder.clear();


                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
