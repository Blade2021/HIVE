package rsystems.commands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.KarmaUserInfo;

import java.sql.SQLException;

public class GetKarma extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        try {
            KarmaUserInfo karmaUserInfo = HiveBot.karmaSQLHandler.getKarmaUserInfo(sender.getIdLong());

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));
            builder.setTitle("Karma Info");
            builder.setThumbnail(sender.getEffectiveAvatarUrl());
            builder.setDescription("`This command is now a slash command if you would like to keep it private! /getKarma`\n\n");

            if(karmaUserInfo != null){

                builder.appendDescription("Points are used to give others karma to help show their helpfulness.  **ONE point is earned per day** that you are active here on discord.");

                builder.addField("Your Karma:", String.valueOf(karmaUserInfo.getKarma()),true);
                //builder.addBlankField(true);
                builder.addField("Your Points",String.valueOf(karmaUserInfo.getAvailable_points()),true);

            } else {
                builder.appendDescription("Sorry,\n\nLooks like your not in our database yet.  Help others to earn some karma!");
            }

            reply(event,builder.build());

            builder.clear();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getHelp() {
        return String.format("{prefix}%s\n" +
                "\n" +
                "Get your karma & points sent to you in the channel.\n" +
                "If you want to keep it private, use the `/getKarma` command instead!",this.getName());
    }
}
