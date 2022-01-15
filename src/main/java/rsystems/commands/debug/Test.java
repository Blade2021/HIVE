package rsystems.commands.debug;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.Reference;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class Test extends Command {

    @Override
    public boolean isOwnerOnly() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {


        TreeMap<Integer, Reference> comparedMap = new TreeMap<>(Collections.reverseOrder());

        for(Map.Entry<String, Reference> entry: HiveBot.referenceHandler.getRefMap().entrySet()){
            //System.out.println("Comparing:" + entry.getValue().getReferenceCommand());

            int totalCompareRate = 0;

            totalCompareRate = totalCompareRate + FuzzySearch.ratio(content.toLowerCase(),entry.getValue().getReferenceCommand().toLowerCase()) * 3;
            totalCompareRate = totalCompareRate + FuzzySearch.partialRatio(content.toLowerCase(),entry.getValue().getAliases().toString().toLowerCase());
            totalCompareRate = totalCompareRate + FuzzySearch.partialRatio(content.toLowerCase(),entry.getValue().getDescription().toLowerCase());


            comparedMap.put(totalCompareRate,entry.getValue());
        }

        StringBuilder resultString = new StringBuilder();
        StringBuilder ratioString = new StringBuilder();

        int x = 0;
        for(Map.Entry<Integer,Reference> entry:comparedMap.entrySet()){

                resultString.append(entry.getValue().getReferenceCommand()).append("\n");
                ratioString.append(entry.getKey()).append("\n");



        }

        EmbedBuilder builder = new EmbedBuilder();
        builder.addField("Command",resultString.toString(),true);
        builder.addField("Ratio",ratioString.toString(),true);

        reply(event,builder.build());


    }

    @Override
    public String getHelp() {
        return "Testing command";
    }
}
