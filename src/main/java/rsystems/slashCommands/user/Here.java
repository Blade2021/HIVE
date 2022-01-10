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

                EmbedBuilder builder = new EmbedBuilder();

                if (statusCode == 200) {

                    builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
                    builder.setThumbnail(sender.getEffectiveAvatarUrl());
                    builder.setDescription(String.format("%s\nI've sent your rewards!  **Thanks for joining us!**", event.getMember().getAsMention()));

                    builder.appendDescription("\n\nUse `/streamPoints` to see how many nuts you have");

                    reply(event, builder.build(),isEphemeral());

                    builder.clear();

                } else if (statusCode == 401) {

                    builder.setColor(HiveBot.getColor(HiveBot.colorType.NOVA));
                    builder.setDescription(String.format("%s\nYou have already been counted for this stream.  Try again during the next stream!", event.getMember().getAsMention()));

                    reply(event, builder.build(), isEphemeral());
                    builder.clear();

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

    @Override
    public String getDescription() {
        return "Tell HIVE that you attended the live stream";
    }
}
