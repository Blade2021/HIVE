package rsystems.commands.generic;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class Who extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) throws SQLException {
        Member lookupMember = null;
        if(!event.getMessage().getMentionedMembers().isEmpty()){
            lookupMember = event.getMessage().getMentionedMembers().get(0);
        } else {
            String[] args = content.split("\\s+");

            if((args.length >= 1) && (HiveBot.mainGuild().getMemberById(args[0]) != null)){
                lookupMember = HiveBot.mainGuild().getMemberById(args[0]);
            }
        }

        if(lookupMember != null){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("User Info:")
                    .setColor(Color.decode("#5742f5"))
                    .addField("User:",lookupMember.getAsMention(),true)
                    .addField("Tag",lookupMember.getUser().getAsTag(),true)
                    .addField("UserID",lookupMember.getId(),true)
                    .addField("Joined Server",lookupMember.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_DATE),true)
                    .addField("Joined Discord",lookupMember.getTimeCreated().format(DateTimeFormatter.ISO_LOCAL_DATE),true)
                    .addField("Karma:","🔹 " + HiveBot.karmaSQLHandler.getKarma(lookupMember.getId()).toString(),true)
                    .setThumbnail(lookupMember.getUser().getEffectiveAvatarUrl());

            reply(event,embedBuilder.build());
            embedBuilder.clear();
        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
