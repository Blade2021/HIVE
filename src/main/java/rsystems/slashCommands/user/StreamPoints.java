package rsystems.slashCommands.user;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;
import rsystems.objects.UserStreamObject;

import java.sql.SQLException;

public class StreamPoints extends SlashCommand {
    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        try {
            UserStreamObject userStreamObject = HiveBot.database.getStreamPoints(sender.getIdLong());

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Stream Currency");
            builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));
            builder.setDescription("Go collect some nuts!  `Cashews` are used to trigger advertisements, LED effects, and other cool things during a livestream.");
            builder.setThumbnail(event.getGuild().getSelfMember().getEffectiveAvatarUrl());

            if(userStreamObject != null){

                builder.addField("Available Cashews:",userStreamObject.getPoints().toString(),true);
                builder.addField("Devoured Cashews:",userStreamObject.getSpentPoints().toString(),true);

            } else {
                builder.appendDescription("\n\nIt looks like you don't have any points yet!  Type ~getMoreNuts to learn how to get some!");
            }

            reply(event,builder.build(),isEphemeral());
            builder.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "Get your current stream points";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }
}
