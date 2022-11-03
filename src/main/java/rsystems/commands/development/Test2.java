package rsystems.commands.development;

import io.obswebsocket.community.client.message.response.sceneitems.GetSceneItemListResponse;
import io.obswebsocket.community.client.model.SceneItem;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.sql.SQLException;

public class Test2 extends Command {
    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, MessageReceivedEvent event) throws SQLException {



        HiveBot.obsRemoteController.getSceneList(getSceneListResponse -> {
            if (getSceneListResponse.isSuccessful()) {
                // Print each Scene
                getSceneListResponse.getScenes().forEach(scene -> System.out.println(String.format("Scene: %s", scene)));
            }
        });

        GetSceneItemListResponse response = HiveBot.obsRemoteController.getSceneItemList("Test",10000);
        if(response != null && response.isSuccessful()){

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("OBS Sources");
            embedBuilder.setColor(HiveBot.getColor(HiveBot.colorType.STREAM));

            StringBuilder sourceNameString = new StringBuilder();
            StringBuilder sourceIDString = new StringBuilder();

            for(SceneItem item: response.getSceneItems()){
                sourceNameString.append(item.getSourceName()).append("\n");
                sourceIDString.append(item.getSceneItemId().toString()).append("\n");
                //System.out.println(String.format("Item Name: %s | Item ID: %d",item.getSourceName(),item.getSceneItemId()));
            }

            embedBuilder.addField("Source Name",sourceNameString.toString(),true);
            embedBuilder.addField("Source ID",sourceIDString.toString(),true);

            reply(event,embedBuilder.build());
        }

        HiveBot.obsRemoteController.getSceneItemId("Test","Dog",0,getSceneItemIdResponse -> {

        });

        /*
        HiveBot.obsRemoteController.setSourceFilterEnabled("Test","Dog",true, callback -> {
            if(callback.isSuccessful()){
                System.out.println("true");
            } else {
                System.out.println("false");
            }
        });

         */
    }

    @Override
    public String getHelp() {
        return "something";
    }
}
