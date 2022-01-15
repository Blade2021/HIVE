package rsystems.objects;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.function.Consumer;

public abstract class SlashCommand {

    private Integer permissionIndex = null;
    private CommandData commandData = new CommandData(this.getName().toLowerCase(),this.getDescription());
    private SubcommandData subcommandData = null;

    public Integer getPermissionIndex() {
        return permissionIndex;
    }
    public void setPermissionIndex(int permissionIndex) {
        this.permissionIndex = permissionIndex;
    }
    public Permission getDiscordPermission(){
        return null;
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public SubcommandData getSubcommandData() {
        return subcommandData;
    }

    public abstract void dispatch(User sender, MessageChannel channel, String content, SlashCommandEvent event);

    public abstract String getDescription();

    public String getName(){
        return this.getClass().getSimpleName();
    }

    public boolean isSubscriberCommand(){ return false;}

    public boolean isEphemeral(){return false;}

    protected void reply(SlashCommandEvent event, MessageEmbed embed){
        reply(event,new MessageBuilder(embed).build(),false,null);
    }

    protected void reply(SlashCommandEvent event, MessageEmbed embed, boolean ephemeral){
        reply(event,new MessageBuilder(embed).build(),ephemeral,null);
    }

    protected void reply(SlashCommandEvent event, MessageEmbed embed, Consumer<InteractionHook> successConsumer){
        reply(event,new MessageBuilder(embed).build(),false,successConsumer);
    }

    protected void reply(SlashCommandEvent event, Message message, boolean ephemeral){
        reply(event,message,ephemeral,null);
    }

    protected void reply(SlashCommandEvent event, String message){
        reply(event,new MessageBuilder(message).build(),this.isEphemeral(),null);
    }

    protected void reply(SlashCommandEvent event, String message, boolean ephemeral){
        reply(event,new MessageBuilder(message).build(),ephemeral,null);
    }

    protected void reply(SlashCommandEvent event, String message, boolean ephemeral, Consumer<InteractionHook> successConsumer){
        reply(event,new MessageBuilder(message).build(),ephemeral,successConsumer);
    }

    protected void reply(SlashCommandEvent event, Message message, boolean ephemeral, Consumer<InteractionHook> successConsumer){

        if(event.isAcknowledged()){
            event.getHook().editOriginal(message).queue(msg -> {
                /*
                if (successConsumer != null) {
                    successConsumer.accept(event.getHook());
                }

                 */
            });
        } else {
            event.reply(message).setEphemeral(ephemeral).queue(msg -> {
                if (successConsumer != null) {
                    successConsumer.accept(msg);
                }
            });
        }
    }

    protected void channelReply(SlashCommandEvent event, Message message){
        channelReply(event,message,null);
    }

    protected void channelReply(SlashCommandEvent event, String message){
        channelReply(event,message,null);
    }

    protected void channelReply(SlashCommandEvent event, MessageEmbed embed)
    {
        channelReply(event, embed, null);
    }

    protected void channelReply(SlashCommandEvent event, MessageEmbed embed, Consumer<Message> successConsumer)
    {
        channelReply(event, new MessageBuilder(embed).build(), successConsumer);
    }

    protected void channelReply(SlashCommandEvent event, String message, Consumer<Message> successConsumer)
    {
        channelReply(event, new MessageBuilder(message).build(), successConsumer);
    }

    protected void channelReply(SlashCommandEvent event, Message message, Consumer<Message> successConsumer){
        event.getChannel().sendMessage(message).queue(msg -> {
            if(successConsumer != null)
                successConsumer.accept(msg);
        });
    }
}
