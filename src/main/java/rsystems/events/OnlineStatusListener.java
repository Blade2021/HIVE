package rsystems.events;

import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnlineStatusListener extends ListenerAdapter {

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event){
        if(event.getUser().isBot()){
            return;
        }

        System.out.println(event.getUser().getAsTag() + " changed to: " + event.getNewOnlineStatus());
    }

}
