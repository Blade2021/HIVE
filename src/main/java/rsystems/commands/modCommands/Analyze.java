package rsystems.commands.modCommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.ChannelObject;
import rsystems.adapters.RoleCheck;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.*;

import static rsystems.HiveBot.LOGGER;

public class Analyze extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        //Analyzer command

        if(HiveBot.commands.get(11).checkCommand(event.getMessage().getContentRaw())){
            //Check rank of user for authorization
            if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(11))){

                //Create the embed builder
                EmbedBuilder output = new EmbedBuilder();
                //Create date formatter to output date to a universal pattern
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY");
                //Get the current date
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

            }
        }


        /*

        //Analyzer command
        if(HiveBot.commands.get(11).checkCommand(event.getMessage().getContentRaw())){
            //Check rank of user for authorization
            if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(11))){

                //Create the embed builder
                EmbedBuilder output = new EmbedBuilder();
                //Create date formatter to output date to a universal pattern
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY");
                //Get the current date
                LocalDate currentDate = LocalDate.now();

                output.setTitle("Channel Analyzer");

                List<TextChannel> channels = event.getGuild().getTextChannels();
                List<ChannelObject> channelObjects = new ArrayList<>();

                Map<Long, OffsetDateTime> channelStats = new LinkedHashMap<>();

                for(TextChannel t:channels){
                    try{
                        if(t.hasLatestMessage()){
                            t.getIterableHistory().limit(1).queue(messages -> {
                                try{
                                    Message m = messages.get(0);
                                    ChannelObject tempObject = new ChannelObject(t.getIdLong(),m.getTimeEdited(),m.getMember());
                                    channelObjects.add(tempObject);
                                    channelStats.put(t.getIdLong(),m.getTimeEdited());
                                } catch (IndexOutOfBoundsException e){
                                    System.out.println("Could not get message");
                                }
                            });
                        }
                    } catch (IndexOutOfBoundsException e){
                        System.out.println("Something bad happened");
                    } catch (InsufficientPermissionException e){
                        System.out.println("missing permissions for channel");
                    }
                }

                StringBuilder channelString = new StringBuilder();
                StringBuilder datesString = new StringBuilder();
                StringBuilder daysPassed = new StringBuilder();
                try {
                    Thread.sleep(4000);

                channelStats.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(entry -> {
                    for(ChannelObject c:channelObjects){
                        if(c.channelID.equals(entry.getKey())){
                            channelString.append(event.getGuild().getGuildChannelById(c.channelID).getName()).append("\n");
                            datesString.append(formatter.format(entry.getValue())).append("\n");
                            daysPassed.append(ChronoUnit.DAYS.between(entry.getValue().toLocalDate(),currentDate)).append("\n");
                        }
                    }
                });

                /*
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

                 ///

                    output.addField("Channels",channelString.toString(),true);
                    output.addField("Last Message Date",datesString.toString(),true);
                    output.addField("Days Passed",daysPassed.toString(),true);
                    event.getChannel().sendMessage(output.build()).queue();
                    output.clear();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }*/

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
