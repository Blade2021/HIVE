package rsystems.commands.generic;

import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmojiList extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) throws SQLException {
        handleEvent(sender);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) throws SQLException {
        reply(event,handleEvent(sender));
    }

    @Override
    public String getHelp() {

        String returnString = ("{prefix}{command}\n\n" +
                "This will tell you what emoji's you have access to, to attach to your name.\n\n");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    private Message handleEvent(User sender) throws SQLException {
        Member member = HiveBot.mainGuild().getMemberById(sender.getIdLong());
        if(member != null){

            Map<Role,ArrayList<String>> emojiMap = new HashMap<>();
            for(Role r:member.getRoles()){
                if(HiveBot.emojiPerkMap.containsKey(r.getIdLong())){
                    emojiMap.putIfAbsent(r,new ArrayList<>());
                    emojiMap.get(r).addAll(HiveBot.emojiPerkMap.get(r.getIdLong()));
                }
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Authorized Emojis");
            embedBuilder.setDescription("You may use these emojis in your name here on discord!  These are perks to roles you have!");
            embedBuilder.setColor(Color.decode("#00b2e7"));
            for(Map.Entry<Role,ArrayList<String>> entry:emojiMap.entrySet()){
                embedBuilder.addField(entry.getKey().getName(),entry.getValue().toString(),false);
            }

            String allowedKarmaSymbol = HiveBot.karmaSQLHandler.getKarmaSymbol(member.getId());

            embedBuilder.addField("KARMA:", EmojiParser.parseToUnicode(allowedKarmaSymbol),false);

            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.setEmbed(embedBuilder.build());
            embedBuilder.clear();
            return messageBuilder.build();

        } else {
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append("I could not find your information on the DrZzs Discord server.").build();
            return messageBuilder.build();
        }
    }
}
