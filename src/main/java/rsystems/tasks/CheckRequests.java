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

        logger.debug("Checking for stream requests");

        if(HiveBot.streamHandler.isStreamActive()){
            if(!HiveBot.streamHandler.isHandlingRequest()){
                if(HiveBot.streamHandler.getAnimationCooldown().isBefore(Instant.now())) {

                    try {
                        logger.debug("Stream Mode: active | Handling Requests: false | Cooldown is satisfied  - Calling next request");
                        HiveBot.streamHandler.acceptNextRequest();
                    } catch (Exception e){
                        logger.info("Stream Handler ran into an error.  Trying again in 60 seconds");
                    }
                }
            }
        }
    }
}
