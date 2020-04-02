package Main.Commands;

import Main.Main;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Unfollow extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase(Main.prefix + "unfollow")) {
            try{
                //Check to see if user has notifications role
                if(event.getMember().getRoles().toString().contains("Notify")){
                    event.getGuild().modifyMemberRoles(event.getMember(),null,event.getGuild().getRolesByName("Notify", false)).queue();
                    //event.getGuild().getController().removeRolesFromMember(event.getMember(), event.getGuild().getRolesByName("Notify", true)).queue();
                    event.getChannel().sendMessage("Hello " + event.getMessage().getAuthor().getAsMention() + ", I have removed the notify role from you.").queue();
                }
                else{
                    event.getChannel().sendMessage("Sorry " + event.getMessage().getAuthor().getAsMention() + ", You do not have the notify role.").queue();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
