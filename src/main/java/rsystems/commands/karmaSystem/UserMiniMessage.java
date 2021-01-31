package rsystems.commands.karmaSystem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;

public class UserMiniMessage extends Command {

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        String[] args = content.split("\\s+");

        if ((args != null) && (args.length >= 1)) {

            if((args[0].equalsIgnoreCase("insert")) || (args[0].equalsIgnoreCase("update")) || (args[0].equalsIgnoreCase("remove")) || (args[0].equalsIgnoreCase("delete"))) {
                final Integer karma = HiveBot.karmaSQLHandler.getKarma(sender.getId());
                if ((karma == null) || (karma < 20)) {
                    reply(event, "Sorry, you need to have a karma level of 1 or greater to use this function.");
                    return;
                }

                if ((args[0].equalsIgnoreCase("insert")) || args[0].equalsIgnoreCase("update")) {
                    boolean update = false;

                    if (HiveBot.sqlHandler.getValue("HIVE_UserMessageTable", "Message", "UserID", sender.getIdLong()) != null) {
                        update = true;
                    }

                    if (handleInsertMessage(sender.getIdLong(), content, update)) {
                        //success
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        //failure
                        event.getMessage().addReaction("\u274C").queue();
                    }
                }

                if ((args[0].equalsIgnoreCase("remove")) || args[0].equalsIgnoreCase("delete")) {
                    boolean update = false;

                    if (HiveBot.sqlHandler.deleteValue("HIVE_UserMessageTable", "UserID", sender.getIdLong()) >= 1) {
                        // success
                    }
                }
                return;
            }

            if(!message.getMentionedMembers().isEmpty()){
                final Member member = message.getMentionedMembers().get(0);
                final String userMessage = HiveBot.sqlHandler.getValue("HIVE_UserMessageTable","Message","UserID",member.getIdLong());
                if(userMessage != null){
                    postMessage(event,member,userMessage);
                } else {
                    reply(event,"⚠ Sorry, that user doesn't have a custom mini setup yet.");
                }
            } else {
                try{
                    final Long userID = Long.valueOf(args[0]);
                    if(HiveBot.mainGuild().getMemberById(userID) != null) {
                        Member member = HiveBot.mainGuild().getMemberById(userID);
                        final String userMessage = HiveBot.sqlHandler.getValue("HIVE_UserMessageTable", "Message", "UserID", userID);
                        if (userMessage != null) {
                            postMessage(event, member, userMessage);
                        } else {
                            reply(event,"⚠ Sorry, that user doesn't have a custom mini setup yet.");
                        }
                    }
                } catch(IllegalArgumentException e){

                }
            }

        }

    }

    @Override
    public String getName() {
        return "mini";
    }

    @Override
    public String getHelp() {
        String returnString = ("{prefix}{command} [Sub-Command] [args]\n\n" +
                "After reaching karma level 1, you can create a custom message for yourself that will be sent when someone types {prefix}{command} @YOU or uses your User ID.\n\n" +
                "**Call**\n`{prefix}{command} [@mention / UserID]`\nGet a user's custom mini message.\n\n" +
                "**Insert**\n`{prefix}{command} Insert [Custom Message]`\nSet a custom message for yourself.\n\n" +
                "**Update**\n`{prefix}{command} Update [Custom Message]`\nUpdate your custom message for yourself.\n\n" +
                "**Remove**\n`{prefix}{command} Remove [Custom Message]`\nDelete your custom message from the database.\n\n");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    private boolean handleInsertMessage(Long userID, String content, boolean update) {
        boolean returnValue = false;

        final String customMessage = content.substring(7);
        if (update) {
            returnValue = HiveBot.sqlHandler.updateUserMessage(userID, customMessage);
        } else {
            returnValue = HiveBot.sqlHandler.insertUserMessage(userID, customMessage);
        }

        return returnValue;
    }

    private void postMessage(GuildMessageReceivedEvent event, Member member, String userMessage){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(userMessage)
                .setTitle("User Mini:  " + member.getEffectiveName())
                .setColor(Color.decode("#5742f5"));

        channelReply(event, embedBuilder.build());
    }
}
