package rsystems.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import rsystems.HiveBot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static rsystems.HiveBot.LOGGER;
import static rsystems.HiveBot.karmaSQLHandler;

public class WelcomeWagon extends ListenerAdapter {

    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if (HiveBot.dataFile.getData("WelcomeEnable").toString().equals("true")) {
            try {
                Object object = HiveBot.dataFile.getData("WelcomeMessage");
                JSONObject jsonObject = (JSONObject) object;
                String welcomeMessage = (String) jsonObject.get(event.getGuild().getId());
                welcomeMessage = welcomeMessage.replace("{user}", event.getMember().getEffectiveName());

                String finalWelcomeMessage = welcomeMessage;
                event.getUser().openPrivateChannel().queue((channel) -> {

                    channel.sendMessage(finalWelcomeMessage).queue(
                            success -> {
                                LOGGER.info("Sent WELCOME message to " + event.getUser().getAsTag());
                            },
                            failure -> {
                                LOGGER.warning("Failed to send WELCOME message to " + event.getUser().getAsTag());
                                try {
                                    Object channelObject = HiveBot.dataFile.getData("WelcomeChannel");
                                    JSONObject jsonChannelObject = (JSONObject) channelObject;
                                    String welcomeChannelID = (String) jsonChannelObject.get(event.getGuild().getId());

                                    rerouteWelcomeWagon(event.getGuild(), welcomeChannelID, event.getMember());
                                } catch(NullPointerException e){
                                    LOGGER.severe("Something went wrong when sending alternative welcome message");
                                }
                            });
                    channel.close();
                });
            } catch (NullPointerException e) {
                System.out.println("Could not find message for " + event.getGuild().getName());
            }
        }

        checkKarmaTable(event.getMember());

    }

    private void rerouteWelcomeWagon(Guild guild, String channelID, Member member) {
        try {
            if (!HiveBot.dataFile.getData("WelcomeEnable").toString().equals("true")) {
                return;
            }

            // Pull the alternative welcome message from the data file
            Object object = HiveBot.dataFile.getData("alternativeWelcomeMessage");
            // Convert message into a json object
            JSONObject jsonObject = (JSONObject) object;
            // Pull the appropriate message for the guild of the event
            String welcomeMessage = (String) jsonObject.get(guild.getId());
            // Replace user with the members name.
            welcomeMessage = welcomeMessage.replace("{user}", member.getAsMention());

            TextChannel welcomeChannel = guild.getTextChannelById(channelID);
            welcomeChannel.sendMessage(welcomeMessage).queue((m) -> {
                m.addReaction("\uD83D\uDC4B ").queue();
            });

        } catch (NullPointerException e) {
            LOGGER.severe("Something went wrong when sending alternative welcome message");
        } catch (PermissionException e) {
            LOGGER.severe("Something went wrong when sending alternative welcome message");
        }

    }

    private void checkKarmaTable(Member member){

        //Initiate the formatter for formatting the date into a set format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        //Get the current date
        LocalDate currentDate = LocalDate.now();

        //Format the current date into a set format
        String formattedCurrentDate = formatter.format(currentDate);

        //Get the last date of karma increment
        String lastSeenKarma = karmaSQLHandler.getDate(member.getId());

        //Insert new user if not found in DB
        if(lastSeenKarma.isEmpty()){
            if (karmaSQLHandler.insertUser(member.getId(), member.getUser().getAsTag(), formattedCurrentDate, "KARMA")) {
                LOGGER.severe("Failed to add " + member.getUser().getAsTag() + " to honeyCombDB");
            } else {
                LOGGER.info("Added " + member.getUser().getAsTag() + " to honeyCombDB. Table: KARMA");
                karmaSQLHandler.overrideKarmaPoints(member.getId(),5);
            }
        }
    }

}
