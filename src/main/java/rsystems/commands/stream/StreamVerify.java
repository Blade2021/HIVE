package rsystems.commands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class StreamVerify extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
        embedBuilder.setTitle("Stream Settings");

        TextChannel streamChatChannel = event.getGuild().getTextChannelById(HiveBot.streamHandler.getStreamChatChannelID());
        String streamChatMention = "⚠ Something is wrong here!";
        if(streamChatChannel != null){
            streamChatMention = streamChatChannel.getAsMention();
        }
        embedBuilder.addField("Stream Chat Channel:",streamChatMention,false);


        TextChannel streamQuestionsChannel = event.getGuild().getTextChannelById(HiveBot.streamHandler.getStreamQuestionChannelID());
        String streamQuestionsMention = "⚠ Something is wrong here!";
        if(streamQuestionsChannel != null){
            streamQuestionsMention = streamQuestionsChannel.getAsMention();
        }
        embedBuilder.addField("Stream Questions Channel:",streamQuestionsMention,false);


        TextChannel streamLinksChannel = event.getGuild().getTextChannelById(HiveBot.streamHandler.getStreamLinksPostChannelID());
        String streamLinksMention = "⚠ Something is wrong here!";
        if(streamLinksChannel != null){
            streamLinksMention = streamLinksChannel.getAsMention();
        }
        embedBuilder.addField("Stream Links Channel:",streamLinksMention,false);

        reply(event,embedBuilder.build());
        embedBuilder.clear();

    }

    @Override
    public String getHelp() {
        return "Get a printout of the stream channel settings";
    }

    @Override
    public Integer getPermissionIndex() {
        return 8;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.MESSAGE_MANAGE;
    }
}
