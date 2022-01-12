package rsystems.objects;

public class MessageAction {

    private Long messageID;
    private Long channelID;
    private Integer actionType;

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
