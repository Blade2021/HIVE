package rsystems.commands.generic;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.Reference;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Search extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {

        TreeMap<Integer, Reference> comparedMap = new TreeMap<>(Collections.reverseOrder());

        for (Map.Entry<String, Reference> entry : HiveBot.referenceHandler.getRefMap().entrySet()) {
            //System.out.println("Comparing:" + entry.getValue().getReferenceCommand());

            int totalCompareRate = 0;

            totalCompareRate = totalCompareRate + FuzzySearch.partialRatio(content.toLowerCase(), entry.getValue().getReferenceCommand().toLowerCase()) * 2;
            totalCompareRate = totalCompareRate + FuzzySearch.partialRatio(content.toLowerCase(), entry.getValue().getAliases().toString().toLowerCase());
            totalCompareRate = totalCompareRate + FuzzySearch.partialRatio(content.toLowerCase(), entry.getValue().getDescription().toLowerCase());


            comparedMap.put(totalCompareRate, entry.getValue());
        }

        MessageEmbed embed = HiveBot.referenceHandler.createEmbed(comparedMap.firstEntry().getValue());

        EmbedBuilder builder = new EmbedBuilder(embed);
        builder.setFooter(String.format("Reference: %s",comparedMap.firstEntry().getValue().getReferenceCommand()));

        reply(event, builder.build());
    }


    @Override
    public String getHelp() {
        return null;
    }
}
