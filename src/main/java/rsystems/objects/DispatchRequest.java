package rsystems.objects;

import rsystems.HiveBot;

import java.sql.SQLException;

public class DispatchRequest {

    private Long requestingUserID;
    private StreamAdvert selectedAdvert;
    private String requestMessage;

    public DispatchRequest(Long requestingUserID, Integer selectedAdvertID) throws SQLException {
        this.requestingUserID = requestingUserID;
        this.selectedAdvert = HiveBot.database.getAdvert(selectedAdvertID);
    }

    public Long getRequestingUserID() {
        return requestingUserID;
    }

    public StreamAdvert getSelectedAdvert() {
        return selectedAdvert;
    }

    public String getRequestMessage() {
        return requestMessage;
    }
}
