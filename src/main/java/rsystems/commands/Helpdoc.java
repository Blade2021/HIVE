package rsystems.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class Helpdoc extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){

        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(HiveBot.prefix + "helpdoc")){
            event.getChannel().sendMessage("Find great articles on frequently asked questions at https://helpdoc.roots.systems").queue();
        }

    }

}
