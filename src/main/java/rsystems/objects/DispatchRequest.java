package rsystems.objects;

import rsystems.HiveBot;

import java.sql.SQLException;
import java.util.UUID;

public class DispatchRequest {

    private final Long requestingUserID;
    private final StreamAnimation selectedAnimation;
    private String requestMessage;

    private UUID requestID;

    public DispatchRequest(Long requestingUserID, Integer selectedAnimationID) throws SQLException {
        this.requestingUserID = requestingUserID;
        this.selectedAnimation = HiveBot.database.getAnimation(selectedAnimationID);
        this.requestID = UUID.randomUUID();
    }

    public Long getRequestingUserID() {
        return requestingUserID;
    }

    public StreamAnimation getSelectedAnimation() {
        return selectedAnimation;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public UUID getRequestID() {
        return requestID;
    }

    public String getID_String(){
        return requestID.toString().toUpperCase();
    }
}
