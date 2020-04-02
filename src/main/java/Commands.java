import Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        String command = args[0];

        if(command.equalsIgnoreCase(Main.prefix + "info")){

            EmbedBuilder info = new EmbedBuilder();
            info.setTitle(" ☕  This is info");
            info.setDescription("Standard BOT Information");
            info.addField("Creator","Blade2021",false);
            info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
            //info.setColor(0xf45642);
            event.getChannel().sendTyping().queue();
            //event.getChannel().sendMessage("Hey there, I'm alive.").queue();
            event.getChannel().sendMessage(info.build()).queue();
            info.clear();
        }

        if(command.equalsIgnoreCase(Main.prefix + "quit")){
            //
        }
    }

}
