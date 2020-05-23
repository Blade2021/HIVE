package rsystems.events;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import rsystems.HiveBot;

import static rsystems.HiveBot.LOGGER;

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
                            });
                    channel.close();
                });
            } catch (NullPointerException e) {
                System.out.println("Could not find message for " + event.getGuild().getName());
            }
        }
    }

}
