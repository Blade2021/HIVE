package rsystems.objects;

public class StreamHandler {

    private boolean streamActive = false;
    private String streamTopic = null;

    public StreamHandler() {
    }

    public boolean isStreamActive() {
        return streamActive;
    }

    public void setStreamActive(boolean streamActive) {
        this.streamActive = streamActive;
    }

    public String getStreamTopic() {
        return streamTopic;
    }

    public void setStreamTopic(String streamTopic) {
        this.streamTopic = streamTopic;
    }
}
