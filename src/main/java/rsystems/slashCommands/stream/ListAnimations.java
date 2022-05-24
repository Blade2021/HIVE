package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;
import rsystems.objects.StreamAnimation;

import java.sql.SQLException;
import java.util.Map;

public class ListAnimations extends SlashCommand {

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        event.deferReply(this.isEphemeral()).queue();

        try {
            Map<Integer, StreamAnimation> animationMap = HiveBot.database.getAnimations();

            StringBuilder sb = new StringBuilder();
            boolean replied = false;

            String starterLine = ("```   ID: |   Scene Name    |        Source Name        | Cost |  Cooldown  \n" +
                                "    ----------------------------------------------------------------------\n");

            sb.append(starterLine);

            int x = 0;
            for(Map.Entry<Integer, StreamAnimation> entry:animationMap.entrySet()){
                x++;
                String additionalLine = String.format("%6d | %-15s | %-25s | %-4d | %-2d minute(s)\n",entry.getValue().getId(),entry.getValue().getSceneName(),entry.getValue().getSourceName(),entry.getValue().getCost(),entry.getValue().getCooldown());

                if(((sb.length() + additionalLine.length() + 3) > 2000) && (x < animationMap.size())){
                    sb.append("```");
                    if(replied) {
                        channelReply(event, sb.toString());
                    } else {
                        reply(event,sb.toString());
                        replied = true;
                    }
                    sb.setLength(0);
                    sb.append(starterLine);
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
        return "List the registered animations with their respective information";
    }
}
