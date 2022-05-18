package rsystems.commands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.sql.SQLException;

public class Mini extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {

        Long lookupID = sender.getIdLong();

        if(event.getMessage().getMentions().getMembers().size() > 0){
            lookupID = event.getMessage().getMentions().getMembers().get(0).getUser().getIdLong();
        }

        final String userMiniMessage = HiveBot.database.getValue("HIVE_UserMessageTable","Message","UserID",lookupID);
        String color = "#5742f5";
        color = HiveBot.database.getValue("HIVE_UserMessageTable", "Color", "UserID", lookupID);

        if(userMiniMessage == null || userMiniMessage.isEmpty()){
            reply(event,"⚠ Sorry, that user doesn't have a custom mini setup yet.");
        } else {

            final Long finalLookupID = lookupID;
            String finalColor = color;
            event.getGuild().retrieveMemberById(lookupID).queue(member -> {

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setDescription(userMiniMessage)
                        .setTitle("User Mini:  " + member.getEffectiveName())
                        .setColor(Color.decode(finalColor));

                channelReply(event, embedBuilder.build());
            });
        }
    }

    @Override
    public String getHelp() {
        return "Display your or another user's mini message";
    }
}
