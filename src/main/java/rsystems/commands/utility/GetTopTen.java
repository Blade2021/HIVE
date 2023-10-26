package rsystems.commands.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetTopTen extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Karma Top 10").setColor(HiveBot.getColor(HiveBot.colorType.GENERIC)).setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl());

        StringBuilder nameString = new StringBuilder();
        StringBuilder karmaString = new StringBuilder();

        LinkedHashMap<Long, Integer> karmaTopMap = HiveBot.karmaSQLHandler.getTopTen();

        for(Map.Entry<Long,Integer> entry:karmaTopMap.entrySet()){

            nameString.append(event.getGuild().getMemberById(entry.getKey()).getEffectiveName()).append("\n");
            karmaString.append(entry.getValue()).append("\n");

        }

        builder.addField("User:",nameString.toString(),true);
        builder.addField("Karma:",karmaString.toString(),true);

        reply(event,builder.build());

    }

    @Override
    public String getHelp() {
        return "This will return the top 10 for karma users.  It pets their ego..";
    }
}
