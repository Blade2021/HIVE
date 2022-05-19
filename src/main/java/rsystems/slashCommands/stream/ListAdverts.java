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
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        event.deferReply(this.isEphemeral()).queue();

        try {
            Map<Integer, StreamAdvert> advertTreeMap = HiveBot.database.getAdverts();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
            builder.setTitle("List of Adverts");
            //builder.setDescription("Here is a list of registered Adverts\n");

            //builder.appendDescription("**ID:**   |  Scene Name  |    Source Name    |  Cost  |  Cooldown  \n");

            StringBuilder sb = new StringBuilder();
            boolean replied = false;

            sb.append("```   ID: |         Scene Name        |        Source Name        | Cost |  Cooldown  \n");

            for(Map.Entry<Integer,StreamAdvert> entry:advertTreeMap.entrySet()){


                String additionalLine = String.format("%6d | %-25s | %-25s | %-4d | %-2d minute(s)\n",entry.getValue().getId(),entry.getValue().getSceneName(),entry.getValue().getSourceName(),entry.getValue().getCost(),entry.getValue().getCooldown());

                if((sb.length() + additionalLine.length() + 3) > 2000){
                    sb.append("```");
                    if(replied) {
                        channelReply(event, sb.toString());
                    } else {
                        reply(event,sb.toString());
                        replied = true;
                    }
                    sb.setLength(0);
                    sb.append("```   ID: |         Scene Name        |        Source Name        | Cost |  Cooldown  \n");
                } else {
                    sb.append(additionalLine);
                }
            }

            sb.append("```");

            if(replied) {
                channelReply(event, sb.toString());
            } else {
                reply(event,sb.toString());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "List the registered adverts with their respective information";
    }
}
