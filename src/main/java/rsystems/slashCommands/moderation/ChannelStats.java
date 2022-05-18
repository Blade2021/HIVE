package rsystems.slashCommands.moderation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.AnalyzeChannelObject;
import rsystems.objects.SlashCommand;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ChannelStats extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {

        return Commands.slash(this.getName().toLowerCase(),this.getDescription().toLowerCase()).addOption(OptionType.NUMBER,"query","How many days to subtract for activity",false);
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        event.deferReply().queue();

        ArrayList<AnalyzeChannelObject> channelObjects = new ArrayList<>();

        List<TextChannel> guildChannels = event.getGuild().getTextChannels();
        for(TextChannel queryChannel:guildChannels){

                System.out.println("Latest message was found for " + queryChannel.getName());
                queryChannel.getIterableHistory().takeAsync(1).thenAcceptAsync(messages -> {
                    Message m = messages.get(0);
                    Instant lastMessageTime = m.getTimeCreated().toInstant();

                    System.out.println("Instant: " + lastMessageTime);

                    channelObjects.add(new AnalyzeChannelObject(queryChannel.getIdLong(),lastMessageTime,queryChannel.getName()));
                });
            }


        try {

            Thread.sleep(3000);

            int queryDays = 30;

            if(event.getOption("query") != null){

                queryDays = (int) event.getOption("query").getAsDouble();
            }

            final Instant compareDate = Instant.now().minus(queryDays, ChronoUnit.DAYS);

            StringBuilder activeChannels = new StringBuilder();
            StringBuilder inactiveChannels = new StringBuilder();
            for (AnalyzeChannelObject object : channelObjects) {
                if ((object.getLastMessageSent() != null) && (object.getLastMessageSent().isAfter(compareDate))) {
                    if(activeChannels.toString().isEmpty() || activeChannels.toString().isBlank()){
                        activeChannels.append(object.getChannelName());
                    } else {
                        activeChannels.append(", ").append(object.getChannelName());
                    }
                } else {
                    if(inactiveChannels.toString().isEmpty() || inactiveChannels.toString().isBlank()){
                        inactiveChannels.append(object.getChannelName());
                    } else {
                        inactiveChannels.append(", ").append(object.getChannelName());
                    }
                }
            }

            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle(String.format("Channel Query - %d days",queryDays));
            builder.setDescription(String.format("Active / Inactive channels are listed below.\nChannels that have messages sent prior to the query days cuttoff while be in the **Active** list.  Channels that did not have messages sent within the cuttoff will be in the **Inactive** list.\n\n```Query Days: %d```",queryDays));

            builder.setColor(HiveBot.getColor(HiveBot.colorType.GENERIC));

            builder.addField("Active", activeChannels.toString(), false);
            builder.addField("Inactive", inactiveChannels.toString(), false);

            reply(event, builder.build());

            builder.clear();
        } catch (InterruptedException e){

        }

    }

    @Override
    public String getDescription() {
        return "Run some analytics on the channels";
    }
}
