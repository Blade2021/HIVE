package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

public class Adverts extends SlashCommand {

    @Override
    public Permission getDiscordPermission() {
        return Permission.KICK_MEMBERS;
    }

    @Override
    public Integer getPermissionIndex() {
        return 512;
    }

    @Override
    public boolean isEphemeral() {
        return false;
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(),this.getDescription()).addOption(OptionType.BOOLEAN,"advert-allowance","True = Enable / False = Disable",false);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(this.isEphemeral()).queue();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Stream Adverts Notification");
        builder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));

        if(event.getOption("advert-allowance") == null){

            builder.setDescription(String.format("Adverts are currently set to: `%s`",String.valueOf(HiveBot.streamHandler.allowAdverts()).toUpperCase()));

            reply(event,builder.build());
            builder.clear();
            return;
        }

        boolean result = event.getOption("advert-allowance").getAsBoolean();

        if(HiveBot.streamHandler.allowAdverts() == result){
            reply(event,"That setting is already set to " + result);
            return;
        }

        HiveBot.streamHandler.setAllowAdverts(result);

        if(result){

            reply(event,"Enabling adverts for users\n\nStand by for notification to stream channel");

            builder.setDescription("Adverts are now enabled!\n\nPlease be kind and use in moderation");

        } else {
            reply(event,"Disabling adverts for users\n\nStand by for notification to stream channel");

            builder.setDescription("Adverts are temporarily disabled!\n\nPlease still enjoy the show!");
        }

        HiveBot.streamHandler.getLiveStreamChatChannel().sendMessageEmbeds(builder.build()).queue();
        builder.clear();
    }

    @Override
    public String getDescription() {
        return "Enable / Disable the dispatching of adverts via users";
    }
}
