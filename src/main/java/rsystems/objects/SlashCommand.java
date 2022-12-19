package rsystems.objects;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.function.Consumer;

public abstract class SlashCommand {

    private Integer permissionIndex = null;
    private final SlashCommandData commandData = Commands.slash(this.getName().toLowerCase(), this.getDescription());
    private final SubcommandData subcommandData = null;

    public Integer getPermissionIndex() {
        return permissionIndex;
    }
    public void setPermissionIndex(int permissionIndex) {
        this.permissionIndex = permissionIndex;
    }
    public Permission getDiscordPermission(){
        return null;
    }

    public boolean isOwnerOnly(){
        return false;
    }

    public SlashCommandData getCommandData() {
        return commandData;
    }

    public SubcommandData getSubcommandData() {
        return subcommandData;
    }

    public abstract void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event);

    public abstract String getDescription();

    public String getName(){
        return this.getClass().getSimpleName();
    }

    public boolean isSubscriberCommand(){ return false;}

    public boolean isEphemeral(){return false;}

    protected void reply(SlashCommandInteractionEvent event, MessageEmbed embed){
        reply(event, MessageCreateData.fromEmbeds(embed),false,null);
    }

    protected void reply(SlashCommandInteractionEvent event, MessageEmbed embed, boolean ephemeral){
        reply(event,MessageCreateData.fromEmbeds(embed),ephemeral,null);
    }

    protected void reply(SlashCommandInteractionEvent event, MessageEmbed embed, Consumer<InteractionHook> successConsumer){
        reply(event,MessageCreateData.fromEmbeds(embed),false,successConsumer);
    }

    protected void reply(SlashCommandInteractionEvent event, MessageCreateData message, boolean ephemeral){
        reply(event,message,ephemeral,null);
    }

    protected void reply(SlashCommandInteractionEvent event, String message){
        reply(event,MessageCreateData.fromContent(message),this.isEphemeral(),null);
    }

    protected void reply(SlashCommandInteractionEvent event, String message, boolean ephemeral){
        reply(event,MessageCreateData.fromContent(message),ephemeral,null);
    }

    protected void reply(SlashCommandInteractionEvent event, String message, boolean ephemeral, Consumer<InteractionHook> successConsumer){
        reply(event,MessageCreateData.fromContent(message),ephemeral,successConsumer);
    }

    protected void reply(SlashCommandInteractionEvent event, MessageCreateData message, boolean ephemeral, Consumer<InteractionHook> successConsumer){

        MessageEditData data = MessageEditData.fromCreateData(message);

        if(event.isAcknowledged()){
            event.getHook().editOriginal(data).queue(msg -> {
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

    protected void channelReply(SlashCommandInteractionEvent event, MessageCreateData message){
        channelReply(event,message,null);
    }

    protected void channelReply(SlashCommandInteractionEvent event, String message){
        channelReply(event,message,null);
    }

    protected void channelReply(SlashCommandInteractionEvent event, MessageEmbed embed)
    {
        channelReply(event, embed, null);
    }

    protected void channelReply(SlashCommandInteractionEvent event, MessageEmbed embed, Consumer<Message> successConsumer)
    {
        channelReply(event, MessageCreateData.fromEmbeds(embed), successConsumer);
    }

    protected void channelReply(SlashCommandInteractionEvent event, String message, Consumer<Message> successConsumer)
    {
        channelReply(event, MessageCreateData.fromContent(message), successConsumer);
    }

    protected void channelReply(SlashCommandInteractionEvent event, MessageCreateData message, Consumer<Message> successConsumer){
        event.getChannel().sendMessage(message).queue(msg -> {
            if(successConsumer != null)
                successConsumer.accept(msg);
        });
    }
}
