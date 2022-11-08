package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;
import java.time.OffsetDateTime;

public class StreamMode extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(), this.getDescription()).addOption(OptionType.BOOLEAN, "active", "Stream Mode. TRUE = Active, FALSE = Inactive", true);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();


        Boolean streamMode = event.getOption("active").getAsBoolean();

        HiveBot.streamHandler.setStreamActive(streamMode);
        reply(event, "Setting Stream Mode to: `" + streamMode + "`", isEphemeral());

        if(streamMode){

            // CREATE A SCHEDULED EVENT FOR THE STREAM
            HiveBot.mainGuild().createScheduledEvent("LIVE STREAM","https://youtube.com/drzzs", OffsetDateTime.now().plusMinutes(1),OffsetDateTime.now().plusHours(2).plusMinutes(30))
                    .setDescription("Join us with the crazy wacky Doctor we all love!\n\n" +
                            "YouTube: https://youtube.com/drzzs\n" +
                            "Twitch: https://twitch.com/drzzs\n\n" +
                            "Be sure to collect your stream nuts!  Type /here in discord DURING a live stream")
                    .queue(success -> {
                        HiveBot.streamHandler.postToStreamLog("Created event for stream.");
                    });
        } else {

            // LOOP THROUGH SCHEDULED EVENTS
            for(ScheduledEvent scheduledEvent:HiveBot.mainGuild().getScheduledEvents()){
                if(scheduledEvent.getStatus() == ScheduledEvent.Status.ACTIVE){
                    if(scheduledEvent.getCreatorIdLong() == HiveBot.mainGuild().getSelfMember().getIdLong()){
                        if(scheduledEvent.getName().equalsIgnoreCase("LIVE STREAM")){
                            scheduledEvent.getManager().setEndTime(OffsetDateTime.now().plusMinutes(1)).queue(success -> {
                                HiveBot.streamHandler.postToStreamLog("Ending created event for stream.\n\nThanks for watching!");
                            });
                        }
                    }
                }
            }
        }

        try {

            if(HiveBot.database.getKeyValue("EnableOBSConnect").equalsIgnoreCase("1")) {

                if (streamMode) {
                    HiveBot.obsRemoteController.connect();
                } else {
                    HiveBot.obsRemoteController.disconnect();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String getDescription() {
        return "Set the stream mode";
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public Integer getPermissionIndex() {
        return 8;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }
}
