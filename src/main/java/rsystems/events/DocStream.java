package rsystems.events;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DocStream extends ListenerAdapter {
    @Override
    public void onUserActivityStart(UserActivityStartEvent event){
        // ALWAYS IGNORE BOTS
        if(event.getUser().isBot()){
            return;
        }

        if(event.getUser().getId().equalsIgnoreCase(HiveBot.docDUID)){
            if(event.getNewActivity().getType().equals(Activity.ActivityType.STREAMING)){
                HiveBot.setStreamMode(true);
                sendMarkers(event.getGuild());
            }
        }
    }

    @Override
    public void onUserActivityEnd(UserActivityEndEvent event){
        // ALWAYS IGNORE BOTS
        if(event.getUser().isBot()){
            return;
        }

        if(event.getUser().getId().equalsIgnoreCase(HiveBot.docDUID)){
            HiveBot.setStreamMode(false);
        }
    }

    public static void sendMarkers(Guild guild){
        ArrayList<String> markerChannels = new ArrayList<String>();
        markerChannels = HiveBot.dataFile.getArrayList("markerChannels");
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("MM.dd.yyyy 'at' hh:mm:ss a zzz");

        for(String s:markerChannels){
            try{
                TextChannel textChannel = guild.getTextChannelById(s);
                textChannel.sendMessage("`STREAM MARKER`\n" + ft.format(date)).queue();
            } catch(NullPointerException e){
                System.out.println("Could not find channel");
            }
        }
        System.out.println("All markers sent");
    }

}
