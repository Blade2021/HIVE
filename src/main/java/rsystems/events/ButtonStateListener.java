package rsystems.events;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ButtonStateListener extends ListenerAdapter {

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {

        //Post source of a reference test
        if(event.getButton().getId().equalsIgnoreCase("source")){
            event.getChannel().sendMessage(event.getMessage().getEmbeds().get(0).getDescription().replace("\n","\\n")).queue(message -> {
                message.suppressEmbeds(true).queue();
            });
            event.editButton(event.getButton().asDisabled()).queue();
        }


    }
}
