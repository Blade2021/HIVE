package rsystems.events;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

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

        if(event.getButton().getId().startsWith("depin:")){
            event.getInteraction().deferEdit().queue();

            final String buttonID = event.getButton().getId();
            String messageID = buttonID.substring(buttonID.indexOf(":")+1);

            //event.getHook().editOriginal(String.format("Message will automatically be unpinned in 7 seconds",messageID)).queue();

            event.getChannel().retrieveMessageById(messageID).queue(messageFound -> {
                if(messageFound.isPinned()){

                    if(event.getMember().getIdLong() == messageFound.getAuthor().getIdLong()){
                        event.getHook().editOriginal(String.format("Message will automatically be **unpinned** in 7 seconds",messageID)).queue();
                        event.getInteraction().editButton(event.getButton().asDisabled()).queue();

                        messageFound.unpin().queueAfter(7, TimeUnit.SECONDS);
                    }
                }
            });

        }

    }
}
