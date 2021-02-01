package rsystems.events;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ActivityListener extends ListenerAdapter {

    private boolean streamMode = false;

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        if(event.getMember().getId().equalsIgnoreCase(Config.get("DRZZS_UUID"))){
            checkStreaming(event.getNewActivity());
        }
    }

    @Override
    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        if(event.getMember().getId().equalsIgnoreCase(Config.get("DRZZS_UUID"))){
            checkStreaming(null);
        }
    }

    private void checkStreaming(final Activity activity){
        if(activity == null){
            //HiveBot.setStreamMode(false);
            setStreamMode(false);
        } else {
            //HiveBot.setStreamMode(true);
            setStreamMode(true);
        }

    }

    public boolean getStreamMode() {
        return streamMode;
    }

    public void setStreamMode(boolean streamMode) {
        this.streamMode = streamMode;

        if(!streamMode){
            clearQuestions(Long.valueOf(Config.get("QuestionPushChannel")));
        }
    }

    private void clearQuestions(Long channelID){
        try {
            List<Message> messages = new ArrayList<>();
            TextChannel channel = HiveBot.mainGuild().getTextChannelById(channelID);
            if(channel != null) {
                channel.getIterableHistory()
                        .cache(false)
                        .forEachAsync(messages::add)
                        .thenRun(() -> channel.purgeMessages(messages));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
