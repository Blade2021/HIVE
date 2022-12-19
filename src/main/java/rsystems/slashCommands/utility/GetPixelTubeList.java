package rsystems.slashCommands.utility;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
import java.util.Map;

public class GetPixelTubeList extends SlashCommand {
    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        try {
            Map<Long, String> pixelMap = HiveBot.database.getPixelTubeList();

            if(pixelMap != null){

                EmbedBuilder builder = new EmbedBuilder().setThumbnail(HiveBot.jda.getSelfUser().getEffectiveAvatarUrl()).setTitle("Pixelhead Youtube List").setColor(HiveBot.getColor(HiveBot.colorType.GENERIC));

                StringBuilder userString = new StringBuilder();
                StringBuilder linkString = new StringBuilder();

                for(Map.Entry<Long,String> entry:pixelMap.entrySet()){

                    userString.append(event.getGuild().getMemberById(entry.getKey()).getEffectiveName()).append("\n");
                    linkString.append(String.format("[YouTube](%s)",entry.getValue())).append("\n");

                }

                builder.addField("User",userString.toString(),true);
                builder.addField("Link",linkString.toString(),true);

                reply(event,builder.build());
                builder.clear();

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "Get a list of Pixelhead's Youtube";
    }
}
