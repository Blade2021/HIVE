package rsystems.slashCommands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;

public class Here extends SlashCommand {
    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {
        event.deferReply(this.isEphemeral()).queue();

        if (HiveBot.streamHandler.isStreamActive()) {

            try {

                Integer statusCode;

                if (!HiveBot.streamHandler.isFirstHereClaimed()) {

                    HiveBot.streamHandler.setFirstHereClaimed(true);
                    statusCode = HiveBot.database.acceptHereStatus(sender.getIdLong(), Integer.parseInt(Config.get("BONUS_HERE_INCREMENT_AMOUNT")));
                    handleResponse(event,statusCode,true);

                } else {

                    statusCode = HiveBot.database.acceptHereStatus(sender.getIdLong(), null);
                    handleResponse(event,statusCode,false);

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            reply(event, "There is no active stream at this time.", isEphemeral());
        }
    }

    @Override
    public String getDescription() {
        return "Tell HIVE that you attended the live stream";
    }

    private void handleResponse(final SlashCommandEvent event, Integer statusCode, Boolean firstHere){

        EmbedBuilder builder = new EmbedBuilder();

        if (statusCode == 200) {
            builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
            builder.setThumbnail(event.getMember().getUser().getEffectiveAvatarUrl());

            if(firstHere){
                builder.setDescription(String.format("%s\n**CONGRATS! YOU MADE IT FIRST!**\n\nI've sent your rewards + bonus points for being here first!\n**Thanks for joining us!**", event.getMember().getAsMention()));
            } else {
                builder.setDescription(String.format("%s\nI've sent your rewards!  **Thanks for joining us!**", event.getMember().getAsMention()));
            }

            builder.appendDescription("\n\nUse `/streamPoints` to see how many nuts you have");

            reply(event, builder.build(), isEphemeral());

            builder.clear();

        } else if (statusCode == 401) {

            builder.setColor(HiveBot.getColor(HiveBot.colorType.NOVA));
            builder.setDescription(String.format("%s\nYou have already been counted for this stream.  Try again during the next stream!", event.getMember().getAsMention()));

            reply(event, builder.build(), isEphemeral());
            builder.clear();

        } else {
            reply(event, "Something went wrong.  Sorry", this.isEphemeral());
        }

    }
}
