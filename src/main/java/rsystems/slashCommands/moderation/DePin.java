package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class DePin extends SlashCommand {

    @Override
    public CommandData getCommandData() {
        CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());

        commandData
                .addOption(OptionType.CHANNEL,"channel","The channel that has the Message",true)
                .addOption(OptionType.STRING,"messageid","The messageID of the message to be unpinned",true)
                .addOption(OptionType.NUMBER,"days","How many days to leave the message pinned",false);

        return commandData;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event) {
        event.deferReply(isEphemeral()).queue();

        MessageChannel messageChannel = event.getOption("channel").getAsMessageChannel();
        Long messageID = Long.parseLong(event.getOption("messageid").getAsString());
        Integer days = Integer.parseInt(event.getOption("days").getAsString());

        if(event.getGuild().getTextChannelById(messageChannel.getIdLong()) != null){
            TextChannel textChannel = event.getGuild().getTextChannelById(messageChannel.getIdLong());
            textChannel.retrieveMessageById(messageID).queue(success -> {

                if(success.isPinned()) {

                    Instant unPinDate = Instant.now().plus(days, ChronoUnit.DAYS);
                    try {
                        if (HiveBot.database.insertMessageAction(Timestamp.from(unPinDate), messageChannel.getIdLong(), messageID, 1) > 0) {

                            reply(event, String.format("Message ID: `%d` will automatically be **unpinned** on `%s`",messageID, DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.US).withZone(ZoneId.systemDefault()).format((unPinDate))), isEphemeral());
                        } else {
                            reply(event,"Something went wrong",isEphemeral());
                        }
                    } catch (SQLException e) {
                        //e.printStackTrace();

                        reply(event,"Something went wrong",isEphemeral());
                    }
                } else {
                    reply(event,"That message isn't pinned",isEphemeral());
                }

            }, failure -> {
                reply(event,"Sorry something went wrong",isEphemeral());
            });
        }
    }

    @Override
    public String getDescription() {
        return "Un-Pin a Message automatically after x amount of days";
    }
}
