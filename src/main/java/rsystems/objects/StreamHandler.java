package rsystems.objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.handlers.Dispatcher;
import rsystems.tasks.BotActivity;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class StreamHandler extends ListenerAdapter {

    private boolean streamActive = false;
    private String streamTopic = null;
    private final Long streamQuestionChannelID = Long.parseLong(Config.get("Stream_Question_Post_ChannelID"));
    private final Long streamChatChannelID = Long.parseLong(Config.get("Stream_Chat_ChannelID"));
    private final Long streamLinksPostChannelID = Long.parseLong(Config.get("Stream_Links_Post_ChannelID"));

    private final Long streamLogChannelID = Long.parseLong(Config.get("STREAM_REQUESTS_POST_CHANNELID"));
    private boolean firstHereClaimed = false;
    private boolean allowAnimations = true;
    private int currentStreamID = 0;
    private int spentCashews = 0;
    private int AnimationsCalled = 0;
    private final int maxQueueSize = Integer.parseInt(Config.get("STREAM_MAX_QUEUE_SIZE"));
    private boolean handlingRequest = false;
    private Instant animationCooldown = Instant.now().plus(1, ChronoUnit.MINUTES);
    private TreeMap<UUID,Long> MessageTreeMap = new TreeMap<>();

    private final LinkedList<DispatchRequest> requestsQueue = new LinkedList<>();

    /**
     *
     * @param dispatchRequest The request being attempted in being put in the queue
     * @return Place in the queue
     */
    public Integer submitRequest(DispatchRequest dispatchRequest) {
        if (this.requestsQueue.size() < maxQueueSize) {
            this.requestsQueue.add(dispatchRequest);

            createStreamLogMessage(dispatchRequest);

            return requestsQueue.indexOf(dispatchRequest);
        } else {
            return null;
        }
    }

    public Integer checkListForUser(Long userid) {
        for (DispatchRequest request : requestsQueue) {
            if (request.getRequestingUserID().equals(userid)) {
                return requestsQueue.indexOf(request);
            }
        }
        return null;
    }

    public void clearRequestQueue(){
        if(!this.requestsQueue.isEmpty()){
            this.handlingRequest = true;

            Logger logger = LoggerFactory.getLogger(StreamHandler.class);
            //logger.info("{} called by {} [{}]",c.getName(),event.getAuthor().getAsTag(),event.getAuthor().getIdLong());
            logger.debug("Handling request set to true");

            StringBuilder IDStringBuilder = new StringBuilder();
            StringBuilder RequesterStringBuilder = new StringBuilder();

            for(DispatchRequest request:this.requestsQueue){
                IDStringBuilder.append(request.getSelectedAnimation().getId()).append("\n");
                RequesterStringBuilder.append(request.getRequestingUserID()).append("\n");
            }

            this.requestsQueue.clear();

            EmbedBuilder builder = new EmbedBuilder();
            builder.addField("Animation ID",IDStringBuilder.toString(),true);
            builder.addField("Requester",RequesterStringBuilder.toString(),true);

            this.getStreamLogChannel().sendMessageEmbeds(builder.build()).queue();
            builder.clear();

            this.handlingRequest = false;
        }
    }

    public void setAnimationPause(boolean pause){
        this.handlingRequest = pause;
    }

    /**
     * Triggers the stream handler to check requests queue for processing
     */
    public void acceptNextRequest() {
        if (!this.requestsQueue.isEmpty()) {

            //Set the handling request to true so webhook call is not overlapped with another call.
            this.handlingRequest = true;

            Logger logger = LoggerFactory.getLogger(StreamHandler.class);
            //logger.info("{} called by {} [{}]",c.getName(),event.getAuthor().getAsTag(),event.getAuthor().getIdLong());
            logger.debug("Handling request set to true");

            // Get the first request from the list then remove it from the list
            final DispatchRequest request = this.requestsQueue.getFirst();
            this.requestsQueue.removeFirst();

            final StreamAnimation Animation = request.getSelectedAnimation();


            // Subtract allotted points from user
            try {
                if (HiveBot.database.consumePoints(request.getRequestingUserID(), request.getSelectedAnimation().getCost()) >= 1) {

                    logger.info("Animation request {} ID: {}", request.getRequestingUserID(), request.getSelectedAnimation().getId());
                    logger.info("New Queue Size: {}", requestsQueue.size());

                    try {
                        // Call webhook
                        HiveBot.obsRemoteController.setSceneItemEnabled(Animation.getSceneName(), Animation.getCallerID(), true, callback -> {

                            if (callback.isSuccessful()) {

                                this.animationCooldown = Instant.now().plus(Animation.getCooldown(), ChronoUnit.MINUTES).plus(Animation.getRuntime(),ChronoUnit.SECONDS);
                                notifyAcceptedAnimationRequest(request);

                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            Thread.sleep(Animation.getRuntime() * 1000);
                                            HiveBot.obsRemoteController.setSceneItemEnabled(Animation.getSceneName(),Animation.getCallerID(),false, downCallback -> {
                                                //do nothing
                                            });

                                        } catch (InterruptedException ie) {
                                        }
                                    }
                                }).start();

                                try {
                                    HiveBot.database.recordAnimationLog(this.currentStreamID, request);
                                } catch (SQLException e) {

                                }

                                //Set the handling request too false to allow another request
                                this.handlingRequest = false;
                            } else {

                                logger.error(callback.toString());

                                // Return points to user
                                try {
                                    //if(callback.getError().equalsIgnoreCase())
                                    HiveBot.database.refundPoints(request.getRequestingUserID(), request.getSelectedAnimation().getCost());
                                } catch (SQLException e) {
                                    logger.error("SQL Exception encountered - Refunding Points to User: {}",request.getRequestingUserID());
                                }
                            }

                        });
                    } catch (Exception e) {

                        logger.error("OBS Controller request failed, Attempting to refund points to user");

                        try {

                            if (HiveBot.database.refundPoints(request.getRequestingUserID(), request.getSelectedAnimation().getCost()) >= 1) {
                                logger.info("Refunded points to {} successfully", request.getRequestingUserID());
                            }

                        } catch (SQLException ex) {

                            logger.error("Failed to refund points to User: {} Points: {}", request.getRequestingUserID(), request.getSelectedAnimation().getCost());
                        }
                        //HiveBot.obsRemoteController.connect();
                    }
                }
            } catch (SQLException e) {
                logger.error("SQL Exception encountered - Consuming Points from User: {}",request.getRequestingUserID());
            }
        }
    }

    private void createStreamLogMessage(DispatchRequest request){
        final String notifyChannelID = Config.get("STREAM_REQUESTS_POST_CHANNELID");
        final TextChannel channel = HiveBot.mainGuild().getTextChannelById(notifyChannelID);

        Long response = null;

        if (channel != null) {

            if (channel.canTalk()) {
                HiveBot.mainGuild().retrieveMemberById(request.getRequestingUserID()).queue(foundMember -> {

                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Animation Request");
                    builder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));
                    builder.setDescription(String.format("**Animation ID:** %d\n" +
                            "**Animation Name:** %s", request.getSelectedAnimation().getId(), request.getSelectedAnimation().getSourceName()));
                    builder.setFooter(request.getID_String());


                    builder.setThumbnail(foundMember.getEffectiveAvatarUrl());
                    builder.addField("Requesting User", foundMember.getEffectiveName() + "\n" + foundMember.getId(), true);

                    channel.sendMessageEmbeds(builder.build()).queue(Success -> {

                        MessageTreeMap.put(request.getRequestID(),Success.getIdLong());

                    });
                });
            }
        }
    }

    private void notifyAcceptedAnimationRequest(DispatchRequest request) {
        final String notifyChannelID = Config.get("STREAM_REQUESTS_POST_CHANNELID");
        final TextChannel channel = HiveBot.mainGuild().getTextChannelById(notifyChannelID);

        if (channel != null) {
            if (channel.canTalk()) {

                final Long messageID = this.MessageTreeMap.get(request.getRequestID());

                //Remove the request from the Treemap
                this.MessageTreeMap.remove(request.getRequestID());

                if(messageID != null) {
                    channel.retrieveMessageById(messageID).queue(foundMessage -> {
                        EmbedBuilder builder = new EmbedBuilder(foundMessage.getEmbeds().get(0));
                        builder.addField("Cooldown Expire:", String.format("<t:%d:R>", animationCooldown.getEpochSecond()), true);
                        builder.addField("Remaining Queue: ", String.format("%d of %d", requestsQueue.size(), maxQueueSize), false);
                        builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));

                        foundMessage.editMessageEmbeds(builder.build()).queue();
                    });
                }

                /*HiveBot.mainGuild().retrieveMemberById(request.getRequestingUserID()).queue(foundMember -> {
                    //Create message embed from request
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Animation Request");
                    builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));
                    builder.setDescription(String.format("**Animation ID:** %d\n" +
                            "**Animation Name:** %s", request.getSelectedAnimation().getId(), request.getSelectedAnimation().getSourceName()));
                    builder.setThumbnail(foundMember.getEffectiveAvatarUrl());
                    builder.addField("Requesting User", foundMember.getEffectiveName() + "\n" + foundMember.getId(), true);
                    builder.addField("Cooldown Expire:", String.format("<t:%d:R>", animationCooldown.getEpochSecond()), true);
                    builder.addField("Remaining Queue: ", String.format("%d of %d", requestsQueue.size(), maxQueueSize), false);

                    channel.sendMessageEmbeds(builder.build()).queue();
                    builder.clear();
                });

                 */
            }
        }
    }

    private void notifyDeclinedAnimationRequest(DispatchRequest request) {
        final String notifyChannelID = Config.get("STREAM_REQUESTS_POST_CHANNELID");
        final TextChannel channel = HiveBot.mainGuild().getTextChannelById(notifyChannelID);

        if (channel != null) {
            if (channel.canTalk()) {

                HiveBot.mainGuild().retrieveMemberById(request.getRequestingUserID()).queue(foundMember -> {
                    //Create message embed from request
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Animation Request");
                    builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));
                    builder.setDescription(String.format("**Animation ID:** %d\n" +
                            "**Animation Name:** %s", request.getSelectedAnimation().getId(), request.getSelectedAnimation().getSourceName()));
                    builder.setThumbnail(foundMember.getEffectiveAvatarUrl());
                    builder.addField("Requesting User", foundMember.getEffectiveName() + "\n" + foundMember.getId(), true);
                    builder.addField("Cooldown Expire:", String.format("<t:%d:R>", animationCooldown.getEpochSecond()), true);
                    builder.addField("Remaining Queue: ", String.format("%d of %d", requestsQueue.size(), maxQueueSize), false);

                    channel.sendMessageEmbeds(builder.build()).queue();
                    builder.clear();
                });
            }
        }
    }

    private static final List<Long> questionsList = new ArrayList<>();

    public Instant getAnimationCooldown() {
        return animationCooldown;
    }

    public StreamHandler() {

    }

    public boolean isStreamActive() {
        return streamActive;
    }

    public void setStreamActive(boolean streamActive) {
        setStreamActive(streamActive, null);
    }

    public boolean isHandlingRequest() {
        return handlingRequest;
    }

    public void goLive() {
        setStreamActive(true, null);
    }

    public void setStreamActive(boolean streamActive, String streamTopic) {

        if (!streamActive) {
            clearQuestions(this.streamQuestionChannelID);

            BotActivity.handleTask();
            firstHereClaimed = false;

            try {
                // Add timestamp to stream archive
                HiveBot.database.putTimestamp("StreamArchive", "End", Timestamp.from(Instant.now()), "ID", currentStreamID);

                // REFUND POINTS FOR UNCALLED AnimationS
                clearRequestQueue();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            if (!this.streamActive) {

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle(String.format("%s is going LIVE soon!", Config.get("HOST_NICKNAME")));
                builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));
                builder.setDescription(String.format("Come one, come all!  Join us on the %s Stream!  Links below!\n**Remember to like and subscribe!**\n\nBe sure to type `/here` during a livestream to receive your bonus stream points!", Config.get("HOST_NICKNAME")));
                if (streamTopic != null) {
                    builder.addField("Topic", streamTopic, false);

                    this.streamTopic = streamTopic;
                }
                builder.addField("Twitch", Config.get("STREAM_TWITCH_LINK"), true);
                builder.addField("YouTube", Config.get("STREAM_YOUTUBE_LINK"), true);

                getChannel(streamChatChannelID).sendMessageEmbeds(builder.build()).queue();

                HiveBot.jda.getPresence().setActivity(Activity.streaming("Stream Mode Active", Config.get("STREAM_TWITCH_LINK")));

                try {
                    currentStreamID = HiveBot.database.registerStream();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            }
        }

        this.streamActive = streamActive;
    }

    /*
    public Integer clearRequestQueue() {

        Integer removedRequests = 0;

        // REFUND POINTS FOR UNCALLED AnimationS
        for (DispatchRequest request : this.requestsQueue) {
            try {
                if (HiveBot.database.refundPoints(request.getRequestingUserID(), request.getSelectedAnimation().getCost()) >= 1) {
                    this.requestsQueue.remove(request);
                    removedRequests++;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return removedRequests;
    }

     */

    public Integer getQueueSize() {
        return this.requestsQueue.size();
    }

    public Integer getMaxQueueSize() {
        return this.maxQueueSize;
    }

    public String getStreamTopic() {
        return streamTopic;
    }

    public boolean allowAnimations() {
        return allowAnimations;
    }

    public void setAllowAnimations(Boolean allowAnimations) {
        this.allowAnimations = allowAnimations;
    }

    public void setStreamTopic(String streamTopic) {
        this.streamTopic = streamTopic;
    }

    public Long getStreamChatChannelID() {
        return streamChatChannelID;
    }

    public Long getStreamQuestionChannelID() {
        return streamQuestionChannelID;
    }

    public Long getStreamLinksPostChannelID() {
        return streamLinksPostChannelID;
    }

    public TextChannel getChannel(final Long channelID) {
        if (HiveBot.mainGuild().getTextChannelById(channelID) != null) {
            return HiveBot.mainGuild().getTextChannelById(channelID);
        } else {
            return null;
        }
    }

    public TextChannel getLiveStreamChatChannel() {
        return HiveBot.mainGuild().getTextChannelById(this.streamChatChannelID);
    }

    public void postToStreamLog(String message){
        MessageCreateBuilder msgBuilder = new MessageCreateBuilder();
        msgBuilder.setContent(message);
        postToStreamLog(msgBuilder.build());
    }

    public void postToStreamLog(MessageEmbed embed){
        MessageCreateBuilder msgBuilder = new MessageCreateBuilder();
        msgBuilder.setEmbeds(embed);
        postToStreamLog(msgBuilder.build());
    }

    public void postToStreamLog(MessageCreateData messageData){
        this.getStreamLogChannel().sendMessage(messageData).queue();
    }

    public TextChannel getStreamLogChannel(){
        return HiveBot.mainGuild().getTextChannelById(this.streamLogChannelID);
    }

    public void parseMessage(MessageReceivedEvent event) {

        if (this.streamActive) {
            // Ask Command
            if (event.getMessage().getContentDisplay().toLowerCase().contains(HiveBot.prefix + "ask ")) {

                // Assign message to local variable
                String messageraw = event.getMessage().getContentRaw();

                // Call method to get link
                final String question = getQuestion(messageraw);

                if (question.length() <= 5) {
                    //Link was not long enough to verify
                    getLogger().info("Question is too short to post");
                    return;
                }

                // Call method to get author
                final String author = getAuthor(event, messageraw);


                try {
                    //Get history of the past 20 messages
                    event.getChannel().getHistoryBefore(event.getMessageId(), 100).limit(100).queue(messageHistory -> {

                        List<Message> messages = messageHistory.getRetrievedHistory();

                        for (Message m : messages) {
                            if (m.getContentRaw().contains(question)) {
                                //System.out.println(String.format("Question: %s\nFound Message: %s",question,m.getContentRaw()));
                                event.getMessage().addReaction(Emoji.fromUnicode("⚠")).queue();

                                String logQuestion = question;
                                if (logQuestion.length() > 20) {
                                    logQuestion = question.substring(0, 20);
                                }

                                getLogger().info("Question was already asked");
                                return;
                            }
                        }

                        String platform = getPlatform(event.getMessage());
                        if (platform == null) {
                            platform = "WHUT?";
                        }

                        //If current question was not found in messages
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle(String.format("Platform: %s", platform));
                        embedBuilder.addField("**Requester**", author, false);
                        embedBuilder.addField("**Question**", String.format("```%s```", question.trim()), false);
                        embedBuilder.setTimestamp(Instant.now());

                        Random rand = new Random();
                        float r = rand.nextFloat();
                        float g = rand.nextFloat();
                        float b = rand.nextFloat();

                        Color randomColor = new Color(r, g, b);
                        embedBuilder.setColor(randomColor);


                        TextChannel questionPushChannel = event.getGuild().getTextChannelById(streamQuestionChannelID);

                        if (questionPushChannel != null) {
                            questionPushChannel.sendMessageEmbeds(embedBuilder.build()).queue(success -> {
                                questionsList.add(success.getIdLong());
                                success.addReaction(Emoji.fromUnicode("✅")).queue();
                                success.addReaction(Emoji.fromUnicode("\u274C")).queue();
                            });
                            embedBuilder.clear();
                            event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDCE8")).queue();
                        } else {
                            getLogger().error("Question channel is NULL");
                        }

                    });
                } catch (InsufficientPermissionException e) {
                    System.out.println("Error: Missing permission: " + e.getPermission().getName());
                } catch (NullPointerException e) {
                    System.out.println("THE CHANNEL DISAPPEARED!");
                }
            }

            // LINK LISTENER
            if ((event.getMessage().getContentRaw().contains("http://")) || (event.getMessage().getContentRaw().contains("https://")) || (event.getMessage().getContentRaw().contains("www."))) {

                final String messageraw = event.getMessage().getContentRaw();
                final TextChannel pushChannel = HiveBot.mainGuild().getTextChannelById(streamLinksPostChannelID);

                // Call method to get link
                String link = getLink(messageraw);

                if (link.length() <= 5) {
                    //Link was not long enough to verify
                    return;
                }

                // Call method to get author
                String author = getAuthor(event, messageraw);
                if (pushChannel != null) {

                    pushChannel.getHistory().retrievePast(20).queue(messages -> {

                        for (Message m : messages) {
                            if (m.getContentRaw().contains(link)) {
                                event.getMessage().addReaction(Emoji.fromUnicode("⚠")).queue();
                                getLogger().info("Link was already found above.");
                                return;
                            }
                        }
                        //If current link was not found in messages
                        pushChannel.sendMessage(String.format("%s: %s", author, link)).queue();
                        event.getMessage().addReaction(Emoji.fromUnicode("\uD83D\uDCE8")).queue();
                        return;
                    });
                }
            }
        }
    }

    private String getLink(String message) {
        String link = "";

        if (message.contains("http")) {
            int linkStart = message.indexOf("http");
            try {
                // Space was found after link
                link = message.substring(linkStart, message.indexOf(" ", linkStart + 1));
            } catch (StringIndexOutOfBoundsException e) {
                // No space was found
                link = message.substring(linkStart);
            }
        } else {
            int linkStart = message.indexOf("www");
            try {
                // Space was found after link
                link = message.substring(linkStart, message.indexOf(" ", linkStart + 1));
            } catch (StringIndexOutOfBoundsException e) {
                // No space was found
                link = message.substring(linkStart);
            }
        }
        return link;
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(StreamHandler.class);
    }

    private String getAuthor(MessageReceivedEvent event, String message) {
        // Initialize author
        String author = "";
        // Does message contain brackets?
        if ((message.contains("[")) && (message.contains("]"))) {
            try {
                // Get locations of brackets
                int openBracketLocation = message.indexOf("[");
                int closeBracketLocation = message.indexOf("]");

                int colonLocation = message.indexOf(":");
                // Grab author, and strip youtube and twitch from author
                author = message.substring(colonLocation + 1, closeBracketLocation);
                //author = author + " : ";
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("Could not find author");
            }
        } else {
            author = event.getMessage().getAuthor().getName();
            author = author + " : ";
        }

        return author;
    }

    /**
     * This will return the platform that the question came from by processing the string.
     *
     * @param message
     * @return Which platform the question originated from.  aka. Discord, YouTube, Twitch
     */
    private String getPlatform(Message message) {
        String platform = null;
        //if(message.getAuthor().isBot()){
        final String messageText = message.getContentDisplay();

        if ((messageText.contains("[")) && (messageText.contains("]"))) {

            final int openBracketLocation = messageText.indexOf("[");
            final int colonLocation = messageText.indexOf(":");

            platform = messageText.substring(openBracketLocation + 1, colonLocation);
        }
        //}
        return platform;
    }

    /**
     * Parse out the question from the text provided.
     *
     * @param message
     * @return The question the user provided.
     */
    private String getQuestion(String message) {
        String question = "";
        try {
            int questionStart = message.indexOf(HiveBot.prefix + "ask");
            question = message.substring(questionStart + 4);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            System.out.println("did not find question");
        }
        return question;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        final Long messageID = event.getMessageIdLong();
        if (questionsList.contains(messageID)) {

            try {

                if (Dispatcher.checkAuthorized(event.getGuild().getIdLong(), event.getMember(), 8, null)) {

                    //if (event.getReaction().getReactionEmote().isEmoji()) {

                        final String reaction = event.getReaction().getEmoji().toString();
                        final TextChannel pushChannel = HiveBot.mainGuild().getTextChannelById(this.streamQuestionChannelID);

                        if (reaction.equals("✅")) {
                            pushChannel.retrieveMessageById(messageID).queue(success -> {
                                if (!success.getEmbeds().isEmpty()) {
                                    MessageEmbed embed = success.getEmbeds().get(0);
                                    EmbedBuilder embedBuilder = new EmbedBuilder(embed);
                                    embedBuilder.setColor(Color.GREEN);
                                    embedBuilder.setFooter("ANSWERED | " + event.getMember().getEffectiveName(), event.getUser().getEffectiveAvatarUrl());

                                    success.editMessageEmbeds(embedBuilder.build()).queue();

                                    //success.delete().queueAfter(60, TimeUnit.SECONDS);
                                    //keeperList.remove(messageID);
                                }
                            });
                        }

                        if (reaction.equals("\u274C")) {
                            pushChannel.retrieveMessageById(messageID).queue(success -> {

                                if (!success.getEmbeds().isEmpty()) {
                                    MessageEmbed embed = success.getEmbeds().get(0);
                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                    embedBuilder.setColor(Color.red);
                                    embedBuilder.setTitle(embed.getTitle());
                                    embedBuilder.setDescription("**Question Removed by Moderator/Admin**\n\nPlease review our regulations for proper question etiquette.");
                                    embedBuilder.setFooter("Removed by: " + event.getMember().getEffectiveName(), event.getUser().getEffectiveAvatarUrl());

                                    success.editMessageEmbeds(embedBuilder.build()).queue();

                                    //success.delete().queueAfter(60, TimeUnit.SECONDS);
                                    //keeperList.remove(messageID);
                                }
                            });

                        }
                    }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove the messageID from the arrayList if the message was deleted.
     *
     * @param event
     */
    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        questionsList.remove(event.getMessageIdLong());
    }

    private void clearQuestions(Long channelID) {
        try {
            List<Message> messages = new ArrayList<>();
            TextChannel channel = HiveBot.mainGuild().getTextChannelById(channelID);
            if (channel != null) {
                channel.getIterableHistory()
                        .cache(false)
                        .forEachAsync(messages::add)
                        .thenRun(() -> channel.purgeMessages(messages));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFirstHereClaimed() {
        return firstHereClaimed;
    }

    public void setFirstHereClaimed(boolean firstHereClaimed) {
        this.firstHereClaimed = firstHereClaimed;
    }

    public int getSpentCashews() {
        return spentCashews;
    }

    public void setSpentCashews(int spentCashews) {
        this.spentCashews = spentCashews;
    }

    public int getAnimationsCalled() {
        return AnimationsCalled;
    }

    public void setAnimationsCalled(int animationsCalled) {
        AnimationsCalled = animationsCalled;
    }
}
