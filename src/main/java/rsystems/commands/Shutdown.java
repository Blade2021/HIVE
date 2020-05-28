package rsystems.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import static rsystems.HiveBot.LOGGER;

public class Shutdown extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }

        String message = event.getMessage().getContentRaw();
        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(!args[0].startsWith(HiveBot.prefix)){
            return;
        }


        if(HiveBot.commands.get(0).checkCommand(event.getMessage().getContentRaw())){
            try {
                if(RoleCheck.getRank(event,event.getMember().getId()) >= HiveBot.commands.get(0).getRank()){
                    LOGGER.severe(HiveBot.commands.get(0).getCommand() + " called by " + event.getAuthor().getAsTag());
                    event.getChannel().sendMessage("Shutting down...").queue();
                    System.out.println("Shut down called by " + event.getMessage().getAuthor().getName());
                    event.getJDA().shutdown();
                } else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
                System.out.println("Null permission found");
            }
        }
    }
}
