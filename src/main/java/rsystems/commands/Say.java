package rsystems.commands;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class Say extends ListenerAdapter{

        public void onGuildMessageReceived(GuildMessageReceivedEvent event){
            //Escape if message came from a bot account
            if(event.getMessage().getAuthor().isBot()){
                return;
            }

            String[] args = event.getMessage().getContentRaw().split("\\s+");
            if(event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {
                if (args[0].equalsIgnoreCase((HiveBot.prefix + "say"))) {
                    try {
                        String message = event.getMessage().getContentDisplay().substring(args[0].length()+1);
                        event.getMessage().delete().queue();
                        event.getChannel().sendMessage(message).queue();
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println("Something went wrong");
                    }
                    catch (InsufficientPermissionException e) {
                        event.getChannel().sendMessage("I am lacking permissions to perform this action").queue();
                    }
                    catch(IndexOutOfBoundsException e){
                        event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + "! I see what you did there!").queue();
                    }
                }
            }
        }

}
