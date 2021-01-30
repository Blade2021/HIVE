package rsystems.commands.karmaSystem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetTopTen extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        final LinkedHashMap<Long, Integer> topTenMap = HiveBot.karmaSQLHandler.getTopTen();

        StringBuilder userString = new StringBuilder();
        StringBuilder karmaString = new StringBuilder();

        for (Map.Entry<Long, Integer> entry : topTenMap.entrySet()) {
            if(HiveBot.mainGuild().getMemberById(entry.getKey()) != null){

                final Member member = HiveBot.mainGuild().getMemberById(entry.getKey());
                userString.append(member.getUser().getAsTag()).append("\n");
                karmaString.append(entry.getValue().toString()).append("\n");
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Karma Top 10")
                .setColor(Color.decode("#5742f5"))
                .addField("User:", userString.toString(), true)
                .addField("Karma:", karmaString.toString(), true);

        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setEmbed(embedBuilder.build());
        channelReply(event,messageBuilder.build());

        embedBuilder.clear();
        messageBuilder.clear();
    }

    @Override
    public String getHelp() {
        return null;
    }
}
