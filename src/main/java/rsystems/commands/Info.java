package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;

public class Info extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if((args[0].equalsIgnoreCase((HiveBot.prefix + "info")) || (args[0].equalsIgnoreCase(((HiveBot.prefix + "help")))))){
            EmbedBuilder info = new EmbedBuilder();
            info.setTitle("HIVE BoT Information");
            info.setDescription("BoT Prefix: " + HiveBot.prefix + "\n**All commands ignore case for your convenience.**");
            info.setThumbnail(event.getGuild().getIconUrl());
            info.addField("`Notify`","Enable/Disable notification channel for stream events",false);
            info.addField("`Ping`","Grab the latest latency between the bot and Discord servers",false);
            info.addField("`Helpdoc`","Post a link to the Helpful Documents Page",false);
            info.addField("`Who`","Display information about HIVE",false);
            info.addField("`TwitchSub`","Awesome Twitch Subscriber information",false);
            info.addField("`Admin`","Admin menu - RESTRICTED",false);
            info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
            info.setColor(Color.CYAN);
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(info.build()).queue();
            info.clear();
        }

        if((args[0].equalsIgnoreCase(HiveBot.prefix + "request")) || (args[0].equalsIgnoreCase(HiveBot.prefix + "requests")) || (args[0].equalsIgnoreCase(HiveBot.prefix + "bug"))){
            event.getChannel().sendMessage("Request new features and notify of a bug on GitHub: https://github.com/Blade2021/HIVEWasp/issues").queue();
        }
    }

}
