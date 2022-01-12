package rsystems.tasks;

import rsystems.HiveBot;
import rsystems.objects.MessageAction;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.TimerTask;

public class CheckDatabase extends TimerTask {
    @Override
    public void run() {
        try {
            ArrayList<MessageAction> actions = HiveBot.database.getExpiredMessageActions(Timestamp.from(Instant.now()));

            if(actions.size() > 0){
                for(MessageAction action:actions){

                    System.out.println("Initiating request for MessageAction. ID: " + action.getMessageID());
                    HiveBot.mainGuild().getTextChannelById(action.getChannelID()).retrieveMessageById(action.getMessageID()).queue(message -> {
                        if(action.getActionType() == 1){
                            if(message.isPinned()){
                                message.unpin().queue();
                            }
                        }
                    });

                    HiveBot.database.deleteRow("MessageTable","MessageID",action.getMessageID());
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
