package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.DispatchRequest;
import rsystems.objects.SlashCommand;
import rsystems.objects.StreamAdvert;

import java.sql.SQLException;

public class Devour extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(),this.getDescription()).addOption(OptionType.INTEGER,"advert-id","Enter the advert you would like to call",true);
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        if(HiveBot.streamHandler.isStreamActive()){

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Animation Request");
            builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));
            builder.setThumbnail(event.getGuild().getSelfMember().getEffectiveAvatarUrl());


            final Integer queuePosition = HiveBot.streamHandler.checkListForUser(sender.getIdLong());
            if(queuePosition != null){
                // User already found

                builder.setDescription("You are already in the queue.  Please wait a while and try again.");
                builder.addField("Queue Position:",queuePosition.toString(),true);
                builder.addBlankField(true);
                reply(event,builder.build());

            } else {
                // User not found


                try {
                    final StreamAdvert advert = HiveBot.database.getAdvert(event.getOption("advert-id").getAsInt());
                    Integer points = HiveBot.database.getInteger("EconomyTable","Points","UserID",sender.getIdLong());

                    if(advert.isEnabled()){
                        if(advert.getCost() <= points){

                            final Integer requestResult = HiveBot.streamHandler.submitRequest(new DispatchRequest(sender.getIdLong(),advert.getId()));

                            if((requestResult != null) && (requestResult >= 0)){
                                // REQUEST ACCEPTED

                                points = points - advert.getCost();

                                builder.setDescription(String.format("Your request has been submitted!\nYou are currently `%d` in the queue.",requestResult));
                                builder.addField("Available Cashews:",points.toString(),true);
                                builder.addBlankField(true);
                                reply(event,builder.build());

                            } else {
                                // FULL QUEUE

                                builder.setDescription(String.format("Sorry, looks like the queue is full right now.  Try again later.",requestResult));
                                reply(event, builder.build());
                            }
                        } else {
                            // NOT ENOUGH POINTS
                            builder.setDescription("Sorry, it looks like you do not have enough points to call that one.");
                            builder.addField("Available Cashews:",points.toString(),true);
                            builder.addField("Required Cashews:",String.valueOf(advert.getCost()),true);

                            reply(event, builder.build());
                        }
                    } else {
                        // Advert is not enabled
                        builder.setDescription("Sorry, looks like that animation is currently disabled.  Please try another.");
                        reply(event, builder.build());
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            builder.clear();
        } else {
            // Stream is not active

            reply(event,"There is no active stream at this time.");
        }
    }

    @Override
    public String getDescription() {
        return "Devour your cashews!";
    }
}
