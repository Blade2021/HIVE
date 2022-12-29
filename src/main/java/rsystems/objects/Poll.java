package rsystems.objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import rsystems.HiveBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Poll {

    private int id;
    private boolean allowVoting = true;
    private boolean multipleChoice = false;
    private boolean hideResponses = false;
    private Long starterID;
    private Long messageID;
    private  Long channelID;
    private int optionCount;

    private Map<Integer,Integer> countMap = new HashMap<>();

    public Poll(int id, int allowVoting, int multipleChoice, int hideResponses, Long starterID, Long messageID, Long channelID, int optionCount, Integer countA, Integer countB, Integer countC, Integer countD) {
        this.id = id;

        if(allowVoting == 1){
            this.allowVoting = true;
        } else {
            this.allowVoting = false;
        }

        if(multipleChoice == 1){
            this.multipleChoice = true;
        } else {
            this.multipleChoice = false;
        }

        if(hideResponses == 1){
            this.hideResponses = true;
        } else {
            this.hideResponses = false;
        }

        this.starterID = starterID;
        this.messageID = messageID;
        this.channelID = channelID;
        this.optionCount = optionCount;

        this.countMap.putIfAbsent(1,countA);
        this.countMap.putIfAbsent(2,countB);
        this.countMap.putIfAbsent(3,countC);
        this.countMap.putIfAbsent(4,countD);

    }

    public Poll(int id, boolean allowVoting, boolean multipleChoice, boolean hideResponses, Long starterID, Long messageID, Long channelID, int optionCount, Integer countA, Integer countB, Integer countC, Integer countD) {
        this.id = id;
        this.allowVoting = allowVoting;
        this.multipleChoice = multipleChoice;
        this.hideResponses = hideResponses;
        this.starterID = starterID;
        this.messageID = messageID;
        this.channelID = channelID;
        this.optionCount = optionCount;
        this.countMap.putIfAbsent(1,countA);
        this.countMap.putIfAbsent(2,countB);
        this.countMap.putIfAbsent(3,countC);
        this.countMap.putIfAbsent(4,countD);
    }

    public Poll(int id, boolean allowVoting, boolean multipleChoice, boolean hideResponses, Long starterID, Long messageID, Long channelID, int optionCount) {
        this.id = id;
        this.allowVoting = allowVoting;
        this.multipleChoice = multipleChoice;
        this.hideResponses = hideResponses;
        this.starterID = starterID;
        this.messageID = messageID;
        this.channelID = channelID;
        this.optionCount = optionCount;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public boolean isHideResponses() {
        return hideResponses;
    }

    public Long getStarterID() {
        return starterID;
    }

    public void addVote(int index){
        int currentValue = this.countMap.get(index);
        this.countMap.replace(index,currentValue+1);
    }

    public Long getMessageID() {
        return messageID;
    }

    public int getOptionCount() {
        return optionCount;
    }

    public boolean isAllowVoting() {
        return allowVoting;
    }

    public Long getChannelID() {
        return channelID;
    }

    public Integer getVoteCount(Integer index){
        return this.countMap.get(index);
    }
}
