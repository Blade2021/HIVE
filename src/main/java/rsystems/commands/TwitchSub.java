package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;

public class TwitchSub extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if((args[0].equalsIgnoreCase((HiveBot.prefix + "twitchsub")))) {
            try {
                EmbedBuilder info = new EmbedBuilder();
                info.setTitle("Twitch Subscriber Information");
                info.setDescription("To receive the twitch sub role, you **MUST** be a subscriber to the DrZzz's Twitch Channel.  This is **100% Free** if you have Amazon Prime.\n\n");
                info.appendDescription("Find out more about Twitch Prime Here: https://twitch.amazon.com/tp");
                info.appendDescription("\nDrZzz's Twitch Channel: https://www.twitch.tv/drzzs");
                info.appendDescription("\n\n**Please remember to link your discord account to twitch to receive this role.**\n");
                info.appendDescription("Twitch Integration Help: (Scroll down to `For Viewers`): https://support.discordapp.com/hc/en-us/articles/212112068-Twitch-Integration-FAQ");
                info.appendDescription("\n\nRemember the Twitch subscriber roles are updated every hour automatically.");
                info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                info.setColor(Color.CYAN);
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage(info.build()).queue();
                info.clear();
            } catch (InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }
    }

}
