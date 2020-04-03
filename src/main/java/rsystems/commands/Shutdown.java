package rsystems.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class Shutdown extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase(HiveBot.prefix + "shutdown")) {
            try {
                if (event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getChannel().sendMessage("Shutting down...").queue();
                    System.out.println("Shut down called by " + event.getMessage().getAuthor().getName());
                    event.getGuild().getJDA().shutdown();
                } else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have permission for that").queue();
                }
            } catch (NullPointerException e) {
                System.out.println("Null permission found");
            }
        }
    }
}
