package rsystems.commands.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;
import rsystems.objects.Reference;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

public class ReferenceList extends Command {

    private static final String[] ALIASES = new String[] {"refList"};

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        int index = 0;
        StringBuilder output4 = new StringBuilder();
        StringBuilder output5 = new StringBuilder();
        StringBuilder output6 = new StringBuilder();

        ArrayList<String> refList = HiveBot.database.getReferenceList();
        refList.sort(String::compareToIgnoreCase);

        //TreeMap<String, Reference> sortedRefMap = new TreeMap<>(HiveBot.referenceHandler.getRefMap());

        //Iterate through each Reference and grab the main ref code
        for (String r : refList) {
            switch (index) {
                case 0:
                    output4.append(r).append("\n");
                    break;
                case 1:
                    output5.append(r).append("\n");
                    break;
                case 2:
                    output6.append(r).append("\n");
                    break;
            }
            index++;
            if (index > 2) {
                index = 0;
            }
        }
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("HIVE Reference List");
        builder.setDescription("[Public Repo](https://github.com/Blade2021/HIVE-RefData)\n\n**How do I use these?**\n" +
                "Just type ~ then the reference you'd like to grab. \n" +
                "HIVE will send you a DM (Direct Message) with the information if its an extended reference.\n" +
                "```diff\nExample: ~mqtt\n```");
        builder.setColor(Color.CYAN);
        builder.addField("References",output4.toString(),true);
        builder.addField("",output5.toString(),true);
        builder.addField("",output6.toString(),true);
        builder.setFooter("Called by " + message.getAuthor().getName(), message.getAuthor().getAvatarUrl());

        reply(event,builder.build());
        builder.clear();
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String[] getAliases(){
        return ALIASES;
    }
}
