package rsystems.commands;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class Ping extends ListenerAdapter{

        public void onGuildMessageReceived(GuildMessageReceivedEvent event){
            //Escape if message came from a bot account
            if(event.getMessage().getAuthor().isBot()){
                return;
            }

            String[] args = event.getMessage().getContentRaw().split("\\s+");

            if(args[0].equalsIgnoreCase((HiveBot.prefix + HiveBot.commands.get(4).getCommand()))){
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Pong " + event.getGuild().getJDA().getGatewayPing() + " ms").queue();
            }
        }

}
