package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static rsystems.HiveBot.LOGGER;

public class Analyze extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        //Analyzer command
        if (args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(11).getCommand())) {
            if(RoleCheck.getRank(event,event.getMember().getId()) >= HiveBot.commands.get(11).getRank()){
                LOGGER.warning(HiveBot.commands.get(11).getCommand() + " called by " + event.getAuthor().getAsTag());
                EmbedBuilder output = new EmbedBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY");
                LocalDate currentDate = LocalDate.now();

                output.setTitle("Channel Analyzer");

                List<TextChannel> channels = event.getGuild().getTextChannels();

                StringBuilder channelString = new StringBuilder();
                StringBuilder datesString = new StringBuilder();
                StringBuilder daysPassed = new StringBuilder();

                for (TextChannel t : channels) {
                    try {
                        if (t.hasLatestMessage()) {
                            t.getIterableHistory().limit(1).queue(messages -> {
                                try {
                                    Message m = messages.get(0);
                                    datesString.append(formatter.format(m.getTimeCreated())).append("\n");
                                    daysPassed.append(ChronoUnit.DAYS.between(m.getTimeCreated().toLocalDate(),currentDate)).append("\n");
                                    channelString.append(t.getName()).append("\n");
                                } catch (IndexOutOfBoundsException e) {
                                    System.out.println("Could not get message on channel: " + t.getName());
                                } catch(UnsupportedTemporalTypeException e){
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Found null on channel: " + t.getName());
                    } catch (PermissionException e){
                        System.out.println("Missing permissions for channel: " + t.getName());
                    }
                }

                try {
                    Thread.sleep(2000);
                    output.addField("Channels",channelString.toString(),true);
                    output.addField("Last Message Date",datesString.toString(),true);
                    output.addField("Days Passed",daysPassed.toString(),true);
                    event.getChannel().sendMessage(output.build()).queue();
                    output.clear();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
            }
        }
    }

    private EmbedBuilder guildAnalyze(Guild guild){
        List<TextChannel> channels = guild.getTextChannels();
        EmbedBuilder analyzerData = new EmbedBuilder();
        for(TextChannel t:channels) {
            try {
                if (t.hasLatestMessage()) {
                    t.getIterableHistory().limit(1).queue(messages -> {
                        try {
                            Message m = messages.get(0);
                            analyzerData.addField(t.getName(), String.valueOf(m.getTimeCreated()),false);
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("Could not get message on channel: " + t.getName());
                        }
                    });
                }
            } catch(NullPointerException e){
                System.out.println("Found null on channel: " + t.getName());
            }

        }
        return analyzerData;
    }
}
