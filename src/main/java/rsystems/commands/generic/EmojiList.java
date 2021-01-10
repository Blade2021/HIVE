package rsystems.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import javax.naming.directory.InvalidSearchControlsException;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmojiList extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        handleEvent(sender);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        reply(event,handleEvent(sender));
    }

    @Override
    public String getHelp() {
        return null;
    }

    private Message handleEvent(User sender){
        Member member = HiveBot.drZzzGuild().getMemberById(sender.getIdLong());
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
