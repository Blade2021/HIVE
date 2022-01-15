package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import rsystems.HiveBot;
import rsystems.objects.Reference;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;

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

        if(event.getButton().getId().startsWith("depin-7:")){
            handleDepinEvent(event,7);

        }

        if(event.getButton().getId().startsWith("depin-30:")){
            handleDepinEvent(event,30);

        }

        if(event.getButton().getId().startsWith("reference:")) {
            String buttonID = event.getButton().getId();

            String refKey = buttonID.substring(10, buttonID.lastIndexOf(':'));
            String originalPoster = buttonID.substring(buttonID.lastIndexOf(':') + 1);

            if (event.getUser().getId().equalsIgnoreCase(originalPoster)) {

                for (Map.Entry<String, Reference> referenceEntity : HiveBot.referenceHandler.getRefMap().entrySet()) {

                    if (referenceEntity.getKey().equalsIgnoreCase(refKey)) {
                        Reference reference = HiveBot.referenceHandler.getRefMap().get(refKey);
                        EmbedBuilder embedBuilder = new EmbedBuilder(HiveBot.referenceHandler.createEmbed(reference));

                        embedBuilder.setFooter("Reference: " + reference.getReferenceCommand());
                        embedBuilder.setColor(HiveBot.getColor(HiveBot.colorType.GENERIC));

                        MessageBuilder builder = new MessageBuilder();

                        builder.setEmbeds(embedBuilder.build());

                        event.getMessage().editMessage(builder.build()).queue();
                        break;
                    }
                }
            } else {
                event.deferEdit().queue();
            }
        }

    }

    private void handleDepinEvent(final ButtonClickEvent event, final int timeLength){
        event.deferEdit().queue();

        final String buttonID = event.getButton().getId();
        final String messageID = buttonID.substring(buttonID.indexOf(":")+1,buttonID.lastIndexOf(":"));
        final String authorID = buttonID.substring(buttonID.lastIndexOf(":")+1);

        if((event.getUser().getId().equalsIgnoreCase(authorID)) || (event.getMember().hasPermission(Permission.ADMINISTRATOR))){
            event.getChannel().retrieveMessageById(messageID).queue(messageFound -> {
                if(messageFound.isPinned()){

                    try {

                        Instant actionDate = Instant.now().plus(timeLength, ChronoUnit.DAYS);


                        if(HiveBot.database.insertMessageAction(Timestamp.from(actionDate),event.getChannel().getIdLong(),Long.parseLong(messageID),1) >= 1){
                            event.getHook().editOriginal(String.format("Message ID: `%d` will automatically be **unpinned** on `%s`",messageFound.getIdLong(), DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.US).withZone(ZoneId.systemDefault()).format(actionDate))).queue();
                            event.getHook().editOriginalComponents().queue();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
