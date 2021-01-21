package rsystems.commands.adminCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;

public class Embed extends Command {

    @Override
    public Integer getPermissionIndex() {
        return 512;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        String[] args = content.split("\\s+");

        // UPDATE MESSAGE
        if (args[0].equalsIgnoreCase("update")) {
            if (content.toLowerCase().contains("-m")) {
                Long messageID = Long.parseLong(grabData("-m", content));

                Long channelID = HiveBot.sqlHandler.getEmbedChannel(messageID);
                if (channelID != null) {
                    updateMessage(channelID, messageID, content);
                }
            }
            return;
        }

        // SET COLOR
        if (args[0].equalsIgnoreCase("setColor")) {
            if (content.toLowerCase().contains("-m")) {

                Long messageID = Long.parseLong(grabData("-m",content));
                Long channelID = HiveBot.sqlHandler.getEmbedChannel(messageID);
                if (channelID != null) {
                    setColor(messageID, grabData("-color",content));
                }
            }
            return;
        }

        /*
        INITIALIZE THE EMBEDDED MESSAGE
         */

        TextChannel outChannel = null;
        if(!message.getMentionedChannels().isEmpty()){
            outChannel = message.getMentionedChannels().get(0);
        }

        String messageText = content;

        if(content.contains("-text")) {
            int messageStartIndex = content.indexOf("-text") + 5;
            messageText = content.substring(messageStartIndex);
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setDescription(messageText);

        String color = grabData("-color",content);
        if(color != null){
            embedBuilder.setColor(Color.decode(color));
        }

        MessageEmbed embed = embedBuilder.build();

        if(outChannel == null) {
            channel.sendMessage(embed).queue(success -> {
                HiveBot.sqlHandler.storeEmbedData(success.getChannel().getIdLong(), success.getIdLong());
            });
        } else {
            outChannel.sendMessage(embed).queue(success -> {
                HiveBot.sqlHandler.storeEmbedData(success.getChannel().getIdLong(), success.getIdLong());
            });
        }


    }

    @Override
    public String getHelp() {

        String returnString = ("{prefix}{command} [Sub-Command] [args]\n\n" +
                "**Create Embed**\n`{prefix}{command} [MentionChannel] [-color #000000] [-text]`\nCreate an embed in the mentioned channel.  Color is optional however, Any text after the -text will be posted in embed form.\n\n" +
                "**Update**\n`{prefix}{command} update [-m (MessageID)] [-text]`\nUpdate an embed by grabbing the **POSTED** embed's messageID and add it after the -m tag.  This function will replace all the text from the posted embed so be sure to copy any text you would like to keep back into the text field.\n\n" +
                "**setColor**\n`{prefix}{command} setColor [-m (MessageID)] [-color #000000]`\nChange the color of an embed **without** affecting the text.\n");
        returnString = returnString.replaceAll("\\{prefix}", Config.get("prefix"));
        returnString = returnString.replaceAll("\\{command}",this.getName());
        return returnString;
    }

    private String grabData(String identifier, String content) {

        String dataOutput = null;

        if (content.toLowerCase().contains(identifier)) {
            int localIndex = content.toLowerCase().indexOf(identifier) + identifier.length()+1;
            int endingIndex = content.indexOf(" ", localIndex + 1);

            if (endingIndex >= 1) {
                dataOutput = content.substring(localIndex, endingIndex);
            } else {
                dataOutput = content.substring(localIndex);
            }

        }

        return dataOutput;
    }

    /**
     * @param content The content of the message to parse through to grab the message ID
     * @param channel The original channel of the message
     * @return The original message
     */

    private Message getMessage(String content, TextChannel channel) {

        if (content.toLowerCase().contains("-m")) {
            int localIndex = content.toLowerCase().indexOf("-m") + 3;
            int endingIndex = content.indexOf(" ", localIndex + 1);

            String messageID = null;
            if (endingIndex >= 1) {
                messageID = content.substring(localIndex, endingIndex);
            } else {
                messageID = content.substring(localIndex);
            }

            if (channel.retrieveMessageById(messageID) != null) {
                return channel.retrieveMessageById(messageID).complete();
            }

        }

        return null;
    }

    private void updateMessage(Long channelID, Long messageID, String content) {
        if (HiveBot.mainGuild().getTextChannelById(channelID) != null) {
            TextChannel embedChannel = HiveBot.mainGuild().getTextChannelById(channelID);
            Message originalMessage = getMessage(content, embedChannel);
            if (originalMessage != null) {
                int messageStartIndex = content.indexOf("-text") + 5;
                String messageText = content.substring(messageStartIndex);

                MessageEmbed originEmbed = originalMessage.getEmbeds().get(0);

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setDescription(messageText);
                embedBuilder.setColor(originEmbed.getColor());

                MessageEmbed embed = embedBuilder.build();

                originalMessage.editMessage(embed).override(true).queue();
            }
        }
    }

    /*
    SET COLOR SUB-COMMAND
     */

    private void setColor(Long messageID, String colorCode) {
        TextChannel embedChannel = HiveBot.mainGuild().getTextChannelById(HiveBot.sqlHandler.getEmbedChannel(messageID));
        if (embedChannel != null) {
            Message embedMessage = embedChannel.retrieveMessageById(messageID).complete();

            if (embedMessage != null) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                MessageEmbed originEmbed = embedMessage.getEmbeds().get(0);

                embedBuilder.setDescription(originEmbed.getDescription());
                embedBuilder.setTitle(originEmbed.getTitle());

                for (MessageEmbed.Field field : originEmbed.getFields()) {
                    embedBuilder.addField(field);
                }

                embedBuilder.setColor(Color.decode(colorCode));

                embedMessage.editMessage(embedBuilder.build()).override(true).queue();
            }
        }
    }

}
