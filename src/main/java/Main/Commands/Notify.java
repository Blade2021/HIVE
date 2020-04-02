package Main.Commands;

import Main.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Notify extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase(Main.prefix + "notify")) {
            try {
                //Check to see if user has notifications role
                if(event.getMember().getRoles().toString().contains("Notify")){
                    //User already has role
                    event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + " You already have the notify role.").queue();

                } else {
                    //User does not have role
                    event.getGuild().modifyMemberRoles(event.getMember(),event.getGuild().getRolesByName("Notify", false),null).queue();
                    event.getChannel().sendMessage("Hello " + event.getMessage().getAuthor().getAsMention() + ", I have added the notify role to you.").queue();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
