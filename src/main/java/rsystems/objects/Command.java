package rsystems.objects;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

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

    public abstract void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event);

    public abstract void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event);

    public abstract String getHelp();

    public String getName(){
        return this.getClass().getSimpleName();
    };

    /**
     * Reply to the message.
     * @param event
     * @param message
     */
    protected void reply(GuildMessageReceivedEvent event, String message){
        reply(event,message,null);
    }

    protected void reply(GuildMessageReceivedEvent event, Message message){
        reply(event,message,null);
    }

    protected void reply(GuildMessageReceivedEvent event, String message, Consumer<Message> successConsumer)
    {
        reply(event, new MessageBuilder(message).build(), successConsumer);
    }

    protected void reply(GuildMessageReceivedEvent event, MessageEmbed embed)
    {
        reply(event, embed, null);
    }

    protected void reply(GuildMessageReceivedEvent event, MessageEmbed embed, Consumer<Message> successConsumer)
    {
        reply(event, new MessageBuilder(embed).build(), successConsumer);
    }

    protected void reply(GuildMessageReceivedEvent event, Message message, Consumer<Message> successConsumer)
    {
        event.getMessage().reply(message).queue(msg ->
        {
            linkMessage(event.getMessageIdLong(), msg.getIdLong());
            if(successConsumer != null)
                successConsumer.accept(msg);
        });
    }

    protected void channelReply(GuildMessageReceivedEvent event, Message message){
        channelReply(event,message,null);
    }

    protected void channelReply(GuildMessageReceivedEvent event, String message){
        channelReply(event,message,null);
    }

    protected void channelReply(GuildMessageReceivedEvent event, MessageEmbed embed)
    {
        channelReply(event, embed, null);
    }

    protected void channelReply(GuildMessageReceivedEvent event, MessageEmbed embed, Consumer<Message> successConsumer)
    {
        channelReply(event, new MessageBuilder(embed).build(), successConsumer);
    }

    protected void channelReply(GuildMessageReceivedEvent event, String message, Consumer<Message> successConsumer)
    {
        channelReply(event, new MessageBuilder(message).build(), successConsumer);
    }

    protected void channelReply(GuildMessageReceivedEvent event, Message message, Consumer<Message> successConsumer){
        event.getChannel().sendMessage(message).queue(msg -> {

            linkMessage(event.getMessageIdLong(),msg.getIdLong());
            if(successConsumer != null)
                successConsumer.accept(msg);
        });
    }



    protected void reply(PrivateMessageReceivedEvent event, String message){
        event.getMessage().reply(message).queue();
    }

    protected void reply(PrivateMessageReceivedEvent event, Message message){
        event.getMessage().reply(message).queue();
    }

    protected void reply(PrivateMessageReceivedEvent event, String message, Consumer<Message> successConsumer)
    {
        reply(event, new MessageBuilder(message).build(), successConsumer);
    }

    protected void reply(PrivateMessageReceivedEvent event, MessageEmbed embed)
    {
        reply(event, embed, null);
    }

    protected void reply(PrivateMessageReceivedEvent event, MessageEmbed embed, Consumer<Message> successConsumer)
    {
        reply(event, new MessageBuilder(embed).build(), successConsumer);
    }

    protected void reply(PrivateMessageReceivedEvent event, Message message, Consumer<Message> successConsumer)
    {
        event.getChannel().sendMessage(message).queue(msg ->
        {
            linkMessage(event.getMessageIdLong(), msg.getIdLong());
            if(successConsumer != null)
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

    static void removeResponses(TextChannel channel, long messageId)
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
}
