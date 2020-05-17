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

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Analyze extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(HiveBot.prefix + "analyze")) {
            if(event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                System.out.println("ANALYZER CALLED BY " + event.getAuthor().getName());
                EmbedBuilder output = new EmbedBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

                output.setTitle("Channel Analyzer");

                List<TextChannel> channels = event.getGuild().getTextChannels();
                for (TextChannel t : channels) {
                    try {
                        if (t.hasLatestMessage()) {
                            t.getIterableHistory().limit(1).queue(messages -> {
                                try {
                                    Message m = messages.get(0);
                                    output.appendDescription(formatter.format(m.getTimeCreated()) + "   |   `" + t.getName() + "`\n");
                                } catch (IndexOutOfBoundsException e) {
                                    System.out.println("Could not get message on channel: " + t.getName());
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
                    Thread.sleep(1000);
                    event.getChannel().sendMessage(output.build()).queue();
                    output.clear();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
