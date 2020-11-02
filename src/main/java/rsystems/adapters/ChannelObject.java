package rsystems.adapters;

import net.dv8tion.jda.api.entities.Member;

import java.time.OffsetDateTime;

public class ChannelObject {

    public Long channelID;
    public OffsetDateTime lastMessageDate;
    public Member member;

    public ChannelObject(Long channelID, OffsetDateTime lastMessageDate, Member member) {
        this.channelID = channelID;
        this.lastMessageDate = lastMessageDate;
        this.member = member;
    }
}
