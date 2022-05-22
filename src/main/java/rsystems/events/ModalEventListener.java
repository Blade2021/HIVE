package rsystems.events;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.sql.SQLException;

public class ModalEventListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("obsadvertreg")) {

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
        }
    }
}
