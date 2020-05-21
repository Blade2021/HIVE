package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static rsystems.HiveBot.LOGGER;


public class Mentionable extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }


        if(event.getMessage().getContentDisplay().equals("@HIVE")){
            LOGGER.info("HIVE Mentionable | Called by " + event.getAuthor().getAsTag());
            event.getMessage().getMentionedMembers().forEach(member -> {
                if (member.getId().equals("650410966130884629")) {
                    event.getMessage().addReaction("\uD83D\uDC1D ").queue(); // Bee Emoji
                    event.getMessage().addReaction("\uD83D\uDC4B ").queue(); // Waving hand emoji
                    try {
                        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                        long uptime = runtimeMXBean.getUptime();
                        long uptimeinSeconds = uptime/1000;
                        long uptimeHours = uptimeinSeconds / (60*60);
                        long uptimeMinutes = (uptimeinSeconds/60) - (uptimeHours * 60);
                        long uptimeSeconds = uptimeinSeconds % 60;

                        String prefix = HiveBot.prefix;

                        EmbedBuilder info = new EmbedBuilder();
                        if(HiveBot.prefix.equals("~")){
                            prefix = "~ (tilde)";
                        }
                        info.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
                        info.setDescription("I am HIVE! A buzzy little bot that is here to help!\nWanna see a list of commands just type: `" + HiveBot.prefix + "info`  \n\nIf you run into any issues please contact my creator: "+ event.getGuild().getMemberById("313832264792539142").getAsMention() + "\uD83E\uDDD9\u200D️ ");
                        info.addField("Prefix:",prefix,true);
                        info.addField("Build Info","Version: " + HiveBot.version,true);
                        info.addField("Uptime:",uptimeHours + " Hours " + uptimeMinutes + " Minutes " + uptimeSeconds + " Seconds",false);
                        info.setColor(Color.ORANGE);
                        event.getChannel().sendMessage(info.build()).queue();
                        info.clear();
                    } catch(PermissionException e){
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I am HIVE! A buzzy little bot that is here to help!\nWanna see a list of commands just type: `" + HiveBot.prefix + "help`  \n\nIf you run into any issues please contact my creator: Blade2021#8727" + "\uD83E\uDDD9\u200D♂️ ").queue();
                    }
                }});
        }
    }
}
