package rsystems.slashCommands.stream;

import io.obswebsocket.community.client.model.SceneItem;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.SlashCommand;

import java.sql.SQLException;

/**
 *  This command will pull any animations from the target OBS installation.  Then pass the data to the database handler for processing.
 */
public class PullSourceData extends SlashCommand {
    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(),this.getDescription()).addOption(OptionType.STRING,"scene","Enter the name of the Scene to pull sources from",true);
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {

        event.deferReply(isEphemeral()).queue();

        final String sceneName = event.getOption("scene").getAsString();
        if(sceneName != null){
            HiveBot.obsRemoteController.getSceneItemList(sceneName,getSceneItemListResponse -> {
                if(getSceneItemListResponse != null && getSceneItemListResponse.isSuccessful()){
                    for(SceneItem item:getSceneItemListResponse.getSceneItems()){
                        try {
                            HiveBot.database.updateAnimation(sceneName,item.getSourceName(),item.getSceneItemId());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }
        reply(event,"Success");
    }

    @Override
    public String getDescription() {
        return "please replace me with a description";
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public Permission getDiscordPermission() {
        return Permission.ADMINISTRATOR;
    }
}
