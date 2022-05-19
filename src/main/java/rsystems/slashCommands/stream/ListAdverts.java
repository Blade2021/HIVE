package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;
import rsystems.objects.StreamAdvert;

import java.sql.SQLException;
import java.util.Map;

public class ListAdverts extends SlashCommand {
    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        try {
            Map<Integer, StreamAdvert> advertTreeMap = HiveBot.database.getAdverts();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
            builder.setTitle("List of Adverts");
            builder.setDescription("Here is a list of registered Adverts");

            for(Map.Entry<Integer,StreamAdvert> entry:advertTreeMap.entrySet()){
                builder.addField("**ID**",entry.getKey().toString(),true);

                String info = String.format("**Enabled?:** %s\n" +
                                "**Scene:** %s\n" +
                        "**Cost:** %d cashews\n" +
                        "**Cooldown:** %d minutes\n\n",
                        String.valueOf(entry.getValue().isEnabled()).toUpperCase(),
                        entry.getValue().getSceneName(),
                        entry.getValue().getCost(),
                        entry.getValue().getCooldown());

                builder.addField(entry.getValue().getSourceName(),info,true);
                builder.addBlankField(true);
            }

            reply(event,builder.build());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "List the registered adverts with their respective information";
    }
}
