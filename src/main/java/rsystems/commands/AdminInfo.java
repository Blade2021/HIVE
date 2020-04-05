package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;

public class AdminInfo extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase((HiveBot.prefix + "admin"))){
            try{
                if(event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)){
                    EmbedBuilder ainfo = new EmbedBuilder();
                    ainfo.setTitle("HIVE Admin Commands");
                    ainfo.setDescription("Prefix: " + HiveBot.prefix+ "\nCurrent User Count: " + event.getGuild().getMemberCount());
                    ainfo.setThumbnail(event.getGuild().getIconUrl());
                    ainfo.addField("Clear [int]","Clears x amount of lines of chat.",false);
                    ainfo.addField("Status [String]","Sets the status activity of the BOT",false);
                    ainfo.addField("Shutdown","Shuts down the BOT.  Only use if REQUIRED!",false);
                    ainfo.addField("Role [String]","Grabs current user count for specified role", false);
                    ainfo.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                    ainfo.setColor(Color.RED);
                    event.getChannel().sendMessage(ainfo.build()).queue();
                    ainfo.clear();
                }
            }
            catch(NullPointerException e){
                System.out.println("Null permission found");
            }
        }
    }

}
