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
import rsystems.objects.KarmaUserInfo;

import java.awt.*;

public class KUserInfo extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {
        reply(event,karmaMessage(sender));
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {

        if(!event.getMessage().getMentionedMembers().isEmpty()){
            reply(event,karmaMessage(event.getMessage().getMentionedMembers().get(0).getUser()));
        } else
            reply(event,karmaMessage(sender));
    }

    @Override
    public String getHelp() {
        return null;
    }

    private Message karmaMessage(User sender){
        MessageBuilder messageBuilder = new MessageBuilder();

        KarmaUserInfo karmaUserInfo;
        Member member = HiveBot.drZzzGuild().getMemberById(sender.getIdLong());
        EmbedBuilder userInfo = new EmbedBuilder();

        if(member != null) {
            karmaUserInfo = HiveBot.karmaSQLHandler.userInfo(sender.getId());
            userInfo.setTitle("Karma Stats");
            userInfo.addField("\uD83D\uDC65 Name: ", member.getEffectiveName(), true);
            userInfo.addField("✨ Current Karma: ", String.format("\uD83D\uDD39 %d", karmaUserInfo.getKarma()), true);
            userInfo.addField("\uD83D\uDC51 Current Rank: ", String.valueOf(HiveBot.karmaSQLHandler.getRank(sender.getId()) + 1), true);
            userInfo.addField("\uD83D\uDC4D Positive Karma Sent: ", String.format("```diff\n+%d```", karmaUserInfo.getKsent_pos()), true);
            userInfo.addField("\uD83D\uDC4E Negative Karma Sent: ", String.format("```diff\n-%d```", karmaUserInfo.getKsent_neg()), true);
            userInfo.setColor(Color.ORANGE);
            userInfo.setThumbnail(sender.getEffectiveAvatarUrl());
        }

        messageBuilder.setEmbed(userInfo.build());
        userInfo.clear();

        return messageBuilder.build();
    }
}
