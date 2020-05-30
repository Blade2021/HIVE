package rsystems.events;

import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import static rsystems.HiveBot.LOGGER;

public class OnlineStatusListener extends ListenerAdapter {

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event){
        if(event.getUser().isBot()){
            return;
        }

        //Check to see if user is privileged
        if(RoleCheck.getRank(event.getGuild(),event.getMember().getId()) >= 1){
            if(event.getGuild().getId().equalsIgnoreCase("469330414121517056")) {

                //Query the DB here
                String lastSeen = HiveBot.sqlHandler.getDate(event.getMember().getId());

                //User doesn't exist in DB
                if (lastSeen.isEmpty()) {
                    if (HiveBot.sqlHandler.insertUser(event.getMember().getId(), event.getUser().getAsTag(), "some date")) {
                        LOGGER.severe("Failed to add " + event.getUser().getAsTag() + " to honeyCombDB");
                    } else {
                        LOGGER.info("Added " + event.getUser().getAsTag() + " to honeyCombDB");
                    }
                }
            }
        }
    }

}
