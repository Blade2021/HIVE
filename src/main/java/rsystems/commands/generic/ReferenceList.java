package rsystems.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.events.References;
import rsystems.objects.Command;
import rsystems.objects.ExtendedReference;
import rsystems.objects.Reference;

import java.awt.*;
import java.util.TreeMap;


public class ReferenceList extends Command {

    private static final String[] ALIASES = new String[] {"refList"};

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event,referenceListCommand(message));
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        channelReply(event,referenceListCommand(message));
    }

    @Override
    public String getHelp() {

        String returnString ="`{prefix}{command}`\n\n" +
                "`What are References!`\n"+
                "References are bits of information that have been stored for easy access from HIVE.  These snippets are written by many of the staff but can be submitted by anyone who wants to write one.\n\n"+
                "If you would like to help us out and submit one yourself.  See the instructions found [here](https://github.com/Blade2021/HIVE-RefData)";

        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    private Message referenceListCommand(Message message) {
        //Initalize stringbuilder objects to hold the list items
        StringBuilder output1 = new StringBuilder();
        StringBuilder output2 = new StringBuilder();
        StringBuilder output3 = new StringBuilder();

        //Track index to add to each list evenly
        int index = 0;

        TreeMap<String, ExtendedReference> sortedmap = new TreeMap<>(References.extendedReferenceMap);

        //Iterate through each Reference and grab the main ref code
        for (ExtendedReference r : sortedmap.values()) {
            switch (index) {
                case 0:
                    output1.append(r.getReferenceCommand()).append("\n");
                    break;
                case 1:
                    output2.append(r.getReferenceCommand()).append("\n");
                    break;
                case 2:
                    output3.append(r.getReferenceCommand()).append("\n");
                    break;
            }
            index++;
            if (index > 2) {
                index = 0;
            }
        }

        index = 0;
        StringBuilder output4 = new StringBuilder();
        StringBuilder output5 = new StringBuilder();
        StringBuilder output6 = new StringBuilder();

        TreeMap<String, Reference> sortedRefMap = new TreeMap<>(References.referenceMap);

        //Iterate through each Reference and grab the main ref code
        for (Reference r : sortedRefMap.values()) {
            switch (index) {
                case 0:
                    output4.append(r.getReferenceCommand()).append("\n");
                    break;
                case 1:
                    output5.append(r.getReferenceCommand()).append("\n");
                    break;
                case 2:
                    output6.append(r.getReferenceCommand()).append("\n");
                    break;
            }
            index++;
            if (index > 2) {
                index = 0;
            }
        }
            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("HIVE Reference List");
            info.setDescription("[Public Repo](https://github.com/Blade2021/HIVE-RefData)\n\n**How do I use these?**\n" +
                    "Just type ~ then the reference you'd like to grab. \n" +
                    "HIVE will send you a DM (Direct Message) with the information if its an extended reference.\n" +
                    "```diff\nExample: ~mqtt\n```");
            info.setColor(Color.CYAN);
            info.addField("References",output4.toString(),true);
            info.addField("",output5.toString(),true);
            info.addField("",output6.toString(),true);
            info.addField("Extended References", output1.toString(), true);
            info.addField("", output2.toString(), true);
            info.addField("", output3.toString(), true);
            info.setFooter("Called by " + message.getAuthor().getName(), message.getAuthor().getAvatarUrl());

            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.setEmbed(info.build());

            info.clear();
            return messageBuilder.build();
    }

    @Override
    public String[] getAliases(){
        return ALIASES;
    }
}
