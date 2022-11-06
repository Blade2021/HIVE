package rsystems.events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.objects.StreamAnimation;

import java.awt.*;
import java.sql.SQLException;

public class ModalEventListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        /*if (event.getModalId().equals("obsanimationreg")) {

            String sceneName = event.getValue("scene").getAsString();
            String sourceName = event.getValue("source").getAsString();

            try {
                Integer result = HiveBot.database.registerOBSAnimation(sceneName,sourceName);

                if(result != null){
                    event.reply(String.format("Your submission has been registered with the ID: %d",result)).setEphemeral(true).queue();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else


         */
            if(event.getModalId().equalsIgnoreCase("animation-mod")){
            try {
                Integer animationID = Integer.parseInt(event.getValue("animationID").getAsString());
                String sceneName = event.getValue("scene").getAsString();
                String sourceName = event.getValue("source").getAsString();
                Integer cost = Integer.parseInt(event.getValue("cost").getAsString());
                Integer cooldown = Integer.parseInt(event.getValue("cooldown").getAsString());

                StreamAnimation animation = new StreamAnimation(animationID,sceneName,sourceName,null,null,cost,cooldown,true);

                Integer result = HiveBot.database.modifyAnimation(animationID,animation);
                if(result >= 1){
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Animation Modification Request");
                    builder.setColor(HiveBot.getColor(HiveBot.colorType.FRUIT));

                    builder.setDescription(String.format("Animation ID: `%d` has been successfully modified.\n\nScene: `%s`\n" +
                            "Source: `%s`\n" +
                            "Cost: `%d` cashews\n" +
                            "Cooldown: `%d` minutes",animationID,sceneName,sourceName,cost,cooldown));

                    event.replyEmbeds(builder.build()).queue();
                    builder.clear();
                } else {
                    event.reply("Something went wrong.").queue();
                }

            } catch(NumberFormatException | SQLException e){
                // fail
            }
        } else
            if(event.getModalId().startsWith("mid-")){

                event.deferReply().queue();

                String channelID = event.getModalId().substring(event.getModalId().indexOf("-")+1,event.getModalId().lastIndexOf("-"));
                String messageID = event.getModalId().substring(event.getModalId().lastIndexOf("-")+1);

                //.reply(String.format("ChannelID: %s\nMessageID: %s",channelID,messageID)).queue();

                EmbedBuilder builder = new EmbedBuilder();

                if((event.getValue("color").getAsString() != null) && !(event.getValue("color").getAsString().isEmpty())) {
                    builder.setColor(Color.decode(event.getValue("color").getAsString()));
                }

                if((event.getValue("title").getAsString() != null) && !(event.getValue("title").getAsString().isEmpty())){
                    builder.setTitle(event.getValue("title").getAsString());
                }
                builder.setDescription(event.getValue("message").getAsString());

                TextChannel channel = HiveBot.mainGuild().getTextChannelById(channelID);
                if(messageID.equalsIgnoreCase("new")){
                    channel.sendMessageEmbeds(builder.build()).queue(success -> {
                        event.getHook().editOriginal(String.format("Your message has been created. ID: %s\nLink: %s",success.getId(),success.getJumpUrl())).queue();
                    });
                } else {
                    channel.retrieveMessageById(messageID).queue(retrievedMessage -> {
                        retrievedMessage.editMessageEmbeds(builder.build()).queue(success -> {
                            event.getHook().editOriginal("The embed has been modified.\n"+success.getJumpUrl()).queue();
                        });
                    });
                }
            }
    }
}