package rsystems.objects;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.sql.SQLException;
import java.util.function.Consumer;

public abstract class Command {

    private Integer permissionIndex = null;

    public Integer getPermissionIndex() {
        return permissionIndex;
    }

    public void setPermissionIndex(int permissionIndex) {
        this.permissionIndex = permissionIndex;
    }

    private static final FixedSizeCache<Long, TLongSet> MESSAGE_LINK_MAP = new FixedSizeCache<>(20);

    public abstract void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException;

    public abstract String getHelp();

    public String getName(){
        return this.getClass().getSimpleName();
    }

    /**
     * Reply to the message.
     * @param event
     * @param message
     */
    protected void reply(MessageReceivedEvent event, String message){
        reply(event,message,null);
    }

    protected void reply(MessageReceivedEvent event, MessageCreateData message){
        reply(event,message,null);
    }

    protected void reply(MessageReceivedEvent event, String message, Consumer<Message> successConsumer)
    {
        reply(event, MessageCreateData.fromContent(message), successConsumer);
    }

    protected void reply(MessageReceivedEvent event, MessageEmbed embed)
    {
        reply(event, embed, null);
    }

    protected void reply(MessageReceivedEvent event, MessageEmbed embed, Consumer<Message> successConsumer)
    {
        reply(event, MessageCreateData.fromEmbeds(embed), successConsumer);
    }

    protected void reply(MessageReceivedEvent event, MessageCreateData message, Consumer<Message> successConsumer)
    {

        event.getMessage().reply(message).queue(msg ->
        {
            linkMessage(event.getMessageIdLong(), msg.getIdLong());
            if(successConsumer != null)
                successConsumer.accept(msg);
        });
    }

    protected void channelReply(MessageReceivedEvent event, MessageCreateData message){
        channelReply(event,message,null);
    }

    protected void channelReply(MessageReceivedEvent event, String message){
        channelReply(event,message,null);
    }

    protected void channelReply(MessageReceivedEvent event, MessageEmbed embed)
    {
        channelReply(event, embed, null);
    }

    protected void channelReply(MessageReceivedEvent event, MessageEmbed embed, Consumer<Message> successConsumer)
    {
        channelReply(event, MessageCreateData.fromEmbeds(embed), successConsumer);
    }

    protected void channelReply(MessageReceivedEvent event, String message, Consumer<Message> successConsumer)
    {
        channelReply(event, MessageCreateData.fromContent(message), successConsumer);
    }

    protected void channelReply(MessageReceivedEvent event, MessageCreateData message, Consumer<Message> successConsumer){

        event.getChannel().sendMessage(message).queue(msg ->
        {
            linkMessage(event.getMessageIdLong(), msg.getIdLong());
            if (successConsumer != null)
                successConsumer.accept(msg);
        });
    }

    public static void linkMessage(long commandId, long responseId)
    {
        TLongSet set;
        if(!MESSAGE_LINK_MAP.contains(commandId))
        {
            set = new TLongHashSet(2);
            MESSAGE_LINK_MAP.add(commandId, set);
        }
        else
        {
            set = MESSAGE_LINK_MAP.get(commandId);
        }
        set.add(responseId);
    }

    public static void removeResponses(MessageChannel channel, long messageId)
    {
        TLongSet responses = MESSAGE_LINK_MAP.get(messageId);
        if(responses != null)
        {
            channel.purgeMessagesById(responses.toArray());
        }
    }

    public String[] getAliases(){
        return new String[0];
    }

    public boolean isOwnerOnly(){
        return false;
    }

    public Permission getDiscordPermission() {
        return null;
    }
}