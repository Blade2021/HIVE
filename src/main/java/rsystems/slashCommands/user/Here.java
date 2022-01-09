package rsystems.slashCommands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;

public class Here extends SlashCommand {
    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {
        event.deferReply(this.isEphemeral()).queue();

        if(HiveBot.streamHandler.isStreamActive()) {

            try {
                Integer statusCode = HiveBot.database.acceptHereStatus(sender.getIdLong());

                if (statusCode == 200) {

                    MessageEmbed embed = messageEmbed(String.format("%s\nYou have been counted!  Thanks for joining us!", event.getMember().getAsMention()), HiveBot.colorType.USER);
                    reply(event, embed,isEphemeral());

                } else if (statusCode == 401) {

                    MessageEmbed embed = messageEmbed(String.format("%s\nYou have already been counted for this stream.  Try again during the next stream!", event.getMember().getAsMention()), HiveBot.colorType.GENERIC);
                    reply(event, embed, isEphemeral());

                } else {
                    reply(event, "Something went wrong.  Sorry", this.isEphemeral());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            reply(event,"There is no active stream at this time.",isEphemeral());
        }
    }

    private MessageEmbed messageEmbed(String message, HiveBot.colorType colorType){
        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(message);
        builder.setColor(HiveBot.getColor(colorType));

        return builder.build();
    }

    @Override
    public String getDescription() {
        return "Tell HIVE that you attended the live stream";
    }
}
