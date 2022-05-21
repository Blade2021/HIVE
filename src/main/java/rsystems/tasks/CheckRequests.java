package rsystems.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rsystems.HiveBot;
import rsystems.objects.StreamHandler;

import java.time.Instant;
import java.util.TimerTask;

public class CheckRequests extends TimerTask {
    @Override
    public void run() {
        Logger logger = LoggerFactory.getLogger(CheckRequests.class);
        if(HiveBot.streamHandler.isStreamActive()){
            if(!HiveBot.streamHandler.isHandlingRequest()){
                if(HiveBot.streamHandler.getAdvertCooldown().isBefore(Instant.now())) {
                    logger.debug("Stream Mode: active | Handling Requests: false | Cooldown is satisfied  - Calling next request");
                    HiveBot.streamHandler.acceptNextRequest();
                }
            }
        }
    }
}
