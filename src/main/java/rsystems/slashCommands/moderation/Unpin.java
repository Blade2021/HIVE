package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
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

public class Unpin extends SlashCommand {

    @Override
    public Integer getPermissionIndex() {
        return 16;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.MANAGE_CHANNEL;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription())
                .addOption(OptionType.CHANNEL,"channel","The channel that has the Message",true)
                .addOption(OptionType.STRING,"messageid","The messageID of the message to be unpinned",true)
                .addOption(OptionType.NUMBER,"days","How many days to leave the message pinned",true);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        MessageChannel messageChannel = event.getOption("channel").getAsChannel().asStandardGuildMessageChannel();
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

                        try {
                            Timestamp timestamp = HiveBot.database.getTimestamp("MessageTable","ActionDate","MessageID",messageID);

                            reply(event,String.format("That message is already set to be unpinned automatically on `%s`",DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.US).withZone(ZoneId.systemDefault()).format(timestamp.toInstant())),isEphemeral());
                        } catch (SQLException ex) {
                            ex.printStackTrace();

                            reply(event,"That message is already set to be unpinned automatically",isEphemeral());
                        }

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
