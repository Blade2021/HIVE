package rsystems.objects;

import java.util.UUID;

public class TicketObject {

    private UUID ticketID;
    private boolean openStatus;
    private long authorID;

    private long ticketChannelID;


    public UUID getTicketID() {
        return ticketID;
    }

    public void setTicketID(UUID ticketID) {
        this.ticketID = ticketID;
    }

    public boolean isOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(boolean openStatus) {
        this.openStatus = openStatus;
    }

    public long getAuthorID() {
        return authorID;
    }

    public void setAuthorID(long authorID) {
        this.authorID = authorID;
    }

    public long getTicketChannelID() {
        return ticketChannelID;
    }

    public void setTicketChannelID(long ticketChannelID) {
        this.ticketChannelID = ticketChannelID;
    }

    public TicketObject(UUID ticketID, long authorID) {
        this.ticketID = ticketID;
        this.authorID = authorID;
        this.openStatus = true;
    }

    public TicketObject(UUID ticketID, long authorID, long ticketChannelID) {
        this.ticketID = ticketID;
        this.authorID = authorID;
        this.openStatus = true;
        this.ticketChannelID = ticketChannelID;
    }

    public TicketObject(UUID ticketID, long authorID, boolean status) {
        this.ticketID = ticketID;
        this.authorID = authorID;
        this.openStatus = status;
    }

}
