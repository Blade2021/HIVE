package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.objects.StreamAnimation;

import java.sql.SQLException;

public class ModalEventListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("obsanimationreg")) {

            String sceneName = event.getValue("scene").getAsString();
            String sourceName = event.getValue("source").getAsString();

            try {
                Integer result = HiveBot.database.registerOBSAnimation(sceneName,sourceName);

                if(result != null){
                   event.reply(String.format("Your submission has been registered with the ID: %d",result)).setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if(event.getModalId().equalsIgnoreCase("animation-mod")){
            try {
                Integer animationID = Integer.parseInt(event.getValue("animationID").getAsString());
                String sceneName = event.getValue("scene").getAsString();
                String sourceName = event.getValue("source").getAsString();
                Integer cost = Integer.parseInt(event.getValue("cost").getAsString());
                Integer cooldown = Integer.parseInt(event.getValue("cooldown").getAsString());

                StreamAnimation animation = new StreamAnimation(animationID,sceneName,sourceName,cost,cooldown);

                Integer result = HiveBot.database.modifyAnimation(animationID,animation);
                if(result >= 1){
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Animation Modification Request");
                    builder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));

                    builder.setDescription(String.format("Animation ID: `%d` has been successfully modified.\n\nScene: `%s`\n" +
                            "Source: `%s`\n" +
                            "Cost: `%d` cashews\n" +
                            "Cooldown: `%d` minutes",animationID,sceneName,sourceName,cost,cooldown));

                    event.replyEmbeds(builder.build()).queue();
                    builder.clear();
                } else {
                    event.reply("Something went wrong.").queue();
                }

            } catch(NumberFormatException | SQLException e){
                // fail
            }
        }
    }
}
