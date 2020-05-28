package rsystems.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

public class Twitch extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(HiveBot.prefix + "tsay")){
            if(event.getMember().hasPermission(Permission.ADMINISTRATOR)){
                //ChannelSendMessage.sendMessage(args[1]);
            }
        }
        /*
        if(args[0].equalsIgnoreCase(HiveBot.prefix + "marker")) {
            TwitchGetVideoID.getMarkers(args[1]);

            String authToken = Config.get("TWITCH-TOKEN");

            TwitchClient twitchClient = TwitchHandler.getClient();

            EmbedBuilder info = new EmbedBuilder();

            StreamMarkersList resultList = twitchClient.getHelix().getStreamMarkers(authToken, "", "", null, Config.get("Twitch-DOC-UserID"), args[1]).execute();

            resultList.getStreamMarkers().forEach(stream -> {
                stream.getVideos().forEach(videoMarker -> {
                    videoMarker.getMarkers().forEach(marker -> {
                        info.addField(marker.getPosition_seconds(),marker.getDescription(),false);
                        System.out.println(marker.getPosition_seconds() + ":" + marker.getDescription());
                    });
                });
            });

            event.getChannel().sendMessage(info.build()).queue();
        }*/

    }


}
