package rsystems.tasks;

import rsystems.HiveBot;

import java.time.Instant;
import java.util.TimerTask;

public class CheckRequests extends TimerTask {
    @Override
    public void run() {
        if(HiveBot.streamHandler.isStreamActive()){
            if(!HiveBot.streamHandler.isHandlingRequest()){
                if(HiveBot.streamHandler.getAdvertCooldown().isAfter(Instant.now())) {
                    HiveBot.streamHandler.acceptNextRequest();
                }
            }
        }
    }
}
