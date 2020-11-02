package rsystems.adapters;

import java.util.ArrayList;

public class GuildObject {
    public String guildID;
    public String prefix;
    public String botNick;
    public boolean welcomeEnable;
    public boolean languageFilter;
    public ArrayList<String> badWords;
    public String welcomeMessage;
    public String alternativeWelcomeMessage;
    public String questionsPostChannelID;
    public String logChannelID;
    public String suggestionPostChannelID;
    public String suggestionReviewChannelID;
    public ArrayList<String> assignableRoles;
    public String botSpamChannelID;

    public GuildObject(){
    }

    public GuildObject(String guildID, String prefix, String botNick, boolean welcomeEnable, boolean languageFilter, ArrayList<String> badWords, String welcomeMessage, String alternativeWelcomeMessage, String questionsPostChannelID, String logChannelID, String suggestionPostChannelID, String suggestionReviewChannelID, ArrayList<String> assignableRoles, String botSpamChannelID) {
        this.guildID = guildID;
        this.prefix = prefix;
        this.botNick = botNick;
        this.welcomeEnable = welcomeEnable;
        this.languageFilter = languageFilter;
        this.badWords = badWords;
        this.welcomeMessage = welcomeMessage;
        this.alternativeWelcomeMessage = alternativeWelcomeMessage;
        this.questionsPostChannelID = questionsPostChannelID;
        this.logChannelID = logChannelID;
        this.suggestionPostChannelID = suggestionPostChannelID;
        this.suggestionReviewChannelID = suggestionReviewChannelID;
        this.assignableRoles = assignableRoles;
        this.botSpamChannelID = botSpamChannelID;
    }

    public String getGuildID() {
        return guildID;
    }

    public void setGuildID(String guildID) {
        this.guildID = guildID;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getBotNick() {
        return botNick;
    }

    public void setBotNick(String botNick) {
        this.botNick = botNick;
    }

    public boolean isWelcomeEnable() {
        return welcomeEnable;
    }

    public void setWelcomeEnable(boolean welcomeEnable) {
        this.welcomeEnable = welcomeEnable;
    }

    public boolean isLanguageFilter() {
        return languageFilter;
    }

    public void setLanguageFilter(boolean languageFilter) {
        this.languageFilter = languageFilter;
    }

    public ArrayList<String> getBadWords() {
        return badWords;
    }

    public void setBadWords(ArrayList<String> badWords) {
        this.badWords = badWords;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getAlternativeWelcomeMessage() {
        return alternativeWelcomeMessage;
    }

    public void setAlternativeWelcomeMessage(String alternativeWelcomeMessage) {
        this.alternativeWelcomeMessage = alternativeWelcomeMessage;
    }

    public String getQuestionsPostChannelID() {
        return questionsPostChannelID;
    }

    public void setQuestionsPostChannelID(String questionsPostChannelID) {
        this.questionsPostChannelID = questionsPostChannelID;
    }

    public String getLogChannelID() {
        return logChannelID;
    }

    public void setLogChannelID(String logChannelID) {
        this.logChannelID = logChannelID;
    }

    public String getSuggestionPostChannelID() {
        return suggestionPostChannelID;
    }

    public void setSuggestionPostChannelID(String suggestionPostChannelID) {
        this.suggestionPostChannelID = suggestionPostChannelID;
    }

    public String getSuggestionReviewChannelID() {
        return suggestionReviewChannelID;
    }

    public void setSuggestionReviewChannelID(String suggestionReviewChannelID) {
        this.suggestionReviewChannelID = suggestionReviewChannelID;
    }

    public ArrayList<String> getAssignableRoles() {
        return assignableRoles;
    }

    public void setAssignableRoles(ArrayList<String> assignableRoles) {
        this.assignableRoles = assignableRoles;
    }

    public String getBotSpamChannelID() {
        return botSpamChannelID;
    }

    public void setBotSpamChannelID(String botSpamChannelID) {
        this.botSpamChannelID = botSpamChannelID;
    }
}
