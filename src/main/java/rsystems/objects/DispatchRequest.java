package rsystems.objects;

import rsystems.HiveBot;

import java.sql.SQLException;

public class DispatchRequest {

    private final Long requestingUserID;
    private final StreamAnimation selectedAnimation;
    private String requestMessage;

    public DispatchRequest(Long requestingUserID, Integer selectedAnimationID) throws SQLException {
        this.requestingUserID = requestingUserID;
        this.selectedAnimation = HiveBot.database.getAnimation(selectedAnimationID);
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
}
