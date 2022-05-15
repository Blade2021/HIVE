package rsystems.objects;

import java.time.Instant;

public class AnalyzeChannelObject {

    public Long channelID;
    private Instant lastMessageSent;

    private String channelName;

    public AnalyzeChannelObject(Long channelID, Instant lastMessageSent, String channelName) {
        this.channelID = channelID;
        this.lastMessageSent = lastMessageSent;
        this.channelName = channelName;
    }

    public Instant getLastMessageSent() {
        return lastMessageSent;
    }

    public void setLastMessageSent(Instant lastMessageSent) {
        this.lastMessageSent = lastMessageSent;
    }

    public String getChannelName() {
        return channelName;
    }
}
