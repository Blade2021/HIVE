package rsystems.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class Who extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if((args[0].equalsIgnoreCase((HiveBot.prefix + "who")) || (args[0].equalsIgnoreCase((HiveBot.prefix + "hive"))))){
            event.getMessage().addReaction("\uD83D\uDC4B ").queue();
            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "I am HIVE! A buzzy little bot that is here to help!\nWanna see a list of commands just type: `" + HiveBot.prefix + "help`  \n\nIf you run into any issues please contact my creator: `Blade2021#8727`").queue();
        }
    }
}
