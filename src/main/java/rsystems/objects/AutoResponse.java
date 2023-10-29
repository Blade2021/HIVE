package rsystems.objects;

import java.util.ArrayList;

public class AutoResponse {

    private String name = null;

    private String title = null;

    private ArrayList<String> triggerWords = new ArrayList<>();
    private int minTriggerCount = 1;

    private int minHoursBetweenResponse = 0;

    private int minMinutesBetweenResponse = 10;

    private String response = null;

    private ArrayList<Long> ignoreChannelList = new ArrayList<>();
    private ArrayList<Long> watchChannelList = new ArrayList<>();

    private boolean useBlacklist = true;
    private boolean useWhitelist = false;

    public AutoResponse(String name, String response, int minTriggerCount, int minHoursBetweenResponse, int minMinutesBetweenResponse) {
        this.name = name;
        this.minTriggerCount = minTriggerCount;
        this.response = response;
        this.minHoursBetweenResponse = minHoursBetweenResponse;
        this.minMinutesBetweenResponse = minMinutesBetweenResponse;
    }

    public AutoResponse(String name, String response, int minTriggerCount, int minHoursBetweenResponse, int minMinutesBetweenResponse, String title) {
        this.name = name;
        this.title = title;
        this.minTriggerCount = minTriggerCount;
        this.minHoursBetweenResponse = minHoursBetweenResponse;
        this.minMinutesBetweenResponse = minMinutesBetweenResponse;
        this.response = response;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ArrayList<String> getTriggerWords(){
        return triggerWords;
    }

    public void setTriggerWords(ArrayList<String> triggerWords) {
        this.triggerWords = triggerWords;
    }

    public int getMinTriggerCount() {
        return minTriggerCount;
    }

    public void setMinTriggerCount(int minTriggerCount) {
        this.minTriggerCount = minTriggerCount;
    }

    public int getMinHoursBetweenResponse() {
        return minHoursBetweenResponse;
    }

    public void setMinHoursBetweenResponse(int minHoursBetweenResponse) {
        this.minHoursBetweenResponse = minHoursBetweenResponse;
    }

    public int getMinMinutesBetweenResponse() {
        return minMinutesBetweenResponse;
    }

    public void setMinMinutesBetweenResponse(int minMinutesBetweenResponse) {
        this.minMinutesBetweenResponse = minMinutesBetweenResponse;
    }

    public ArrayList<Long> getIgnoreChannelList() {
        return ignoreChannelList;
    }

    public void setIgnoreChannelList(ArrayList<Long> ignoreChannelList) {
        this.ignoreChannelList = ignoreChannelList;
    }

    public ArrayList<Long> getWatchChannelList() {
        return watchChannelList;
    }

    public void setWatchChannelList(ArrayList<Long> watchChannelList) {
        this.watchChannelList = watchChannelList;
    }

    public boolean isUseBlacklist() {
        return useBlacklist;
    }

    public void setUseBlacklist(boolean useBlacklist) {
        this.useBlacklist = useBlacklist;
    }

    public boolean isUseWhitelist() {
        return useWhitelist;
    }

    public void setUseWhitelist(boolean useWhitelist) {
        this.useWhitelist = useWhitelist;
    }

}
