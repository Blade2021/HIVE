package rsystems.events;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.HiveBot;

import javax.annotation.Nonnull;

public class ActivityListener extends ListenerAdapter {

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
            HiveBot.setStreamMode(false);
        } else {
            HiveBot.setStreamMode(true);
        }

    }
}
