package rsystems.objects;

public class MessageAction {

    private final Long messageID;
    private final Long channelID;
    private final Integer actionType;

    public MessageAction(Long messageID, Long channelID, Integer actionType) {
        this.messageID = messageID;
        this.channelID = channelID;
        this.actionType = actionType;
    }

    public Long getMessageID() {
        return messageID;
    }

    public Long getChannelID() {
        return channelID;
    }

    public Integer getActionType() {
        return actionType;
    }
}
