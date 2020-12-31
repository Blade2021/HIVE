package rsystems.objects;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.function.Consumer;

public abstract class Command {

    private static final FixedSizeCache<Long, TLongSet> MESSAGE_LINK_MAP = new FixedSizeCache<>(20);

    public abstract void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event);

    public abstract void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event);

    public abstract String getHelp();

    public String getName(){
        return this.getClass().getSimpleName();
    };


    protected void reply(GuildMessageReceivedEvent event, String message){
        event.getMessage().reply(message).queue();
    }

    protected void reply(GuildMessageReceivedEvent event, Message message){
        event.getMessage().reply(message).queue();
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
        event.getChannel().sendMessage(message).queue(msg ->
        {
            linkMessage(event.getMessageIdLong(), msg.getIdLong());
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

    public String[] getAliases(){
        return new String[0];
    }
}
