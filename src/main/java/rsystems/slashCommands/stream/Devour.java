package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import rsystems.HiveBot;
import rsystems.objects.DispatchRequest;
import rsystems.objects.SlashCommand;
import rsystems.objects.StreamAnimation;

import java.sql.SQLException;

public class Devour extends SlashCommand {

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(this.getName().toLowerCase(),this.getDescription()).addOption(OptionType.INTEGER,"animation-id","Enter the animation you would like to call",true);
    }

    @Override
    public boolean isEphemeral() {
        return true;
    }

    @Override
    public void dispatch(User sender, MessageChannel channel, String content, SlashCommandInteractionEvent event) {
        event.deferReply(isEphemeral()).queue();

        // Check if a stream is active
        if(HiveBot.streamHandler.isStreamActive()){

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Animation Request");
            builder.setColor(HiveBot.getColor(HiveBot.colorType.USER));
            builder.setThumbnail(event.getGuild().getSelfMember().getEffectiveAvatarUrl());

            // Check to see if user is already in the queue
            final Integer queuePosition = HiveBot.streamHandler.checkListForUser(sender.getIdLong());
            if(queuePosition != null){
                // User already found

                builder.setDescription("You are already in the queue.  Please wait a while and try again.");
                builder.addField("Queue Position:",queuePosition.toString(),true);
                builder.addBlankField(true);
                reply(event,builder.build());

            } else {
                // User not found in the queue

                try {
                    // Grab the animation
                    final StreamAnimation animation = HiveBot.database.getAnimation(event.getOption("animation-id").getAsInt());

                    // Grab the points the user has
                    Integer points = HiveBot.database.getInteger("EconomyTable","Points","UserID",sender.getIdLong());

                    // Check if animation exists, animations is enabled, and points do not exist
                    if(animation != null && animation.isEnabled() && points != null){

                        // Does the user have enough points
                        if(animation.getCost() <= points){

                            // Get the place in the queue
                            final Integer requestResult = HiveBot.streamHandler.submitRequest(new DispatchRequest(sender.getIdLong(),animation.getId()));

                            if((requestResult != null) && (requestResult >= 0)){
                                // REQUEST ACCEPTED

                                //Get the amount of points left AFTER the deduction for the animation request
                                points = points - animation.getCost();

                                //Set the reply message
                                switch(requestResult){
                                    case 0:
                                        builder.setDescription("Your request has been submitted!\nYou are **first** in the queue.");
                                        break;
                                    case 1:
                                        builder.setDescription("Your request has been submitted!\nYou are **second** in the queue.");
                                        break;
                                    case 2:
                                        builder.setDescription("Your request has been submitted!\nYou are **third** in the queue.");
                                        break;
                                    case 3:
                                        builder.setDescription("Your request has been submitted!\nYou are **forth** in the queue.");
                                        break;
                                    case 4:
                                        builder.setDescription("Your request has been submitted!\nYou are **fifth** in the queue.");
                                        break;
                                    default:
                                        builder.setDescription(String.format("Your request has been submitted!\nYou are %d in the queue.",requestResult));
                                        break;
                                }
                                builder.addField("Available Cashews:",points.toString(),true);
                                builder.addBlankField(true);
                                reply(event,builder.build());

                            } else {
                                // FULL QUEUE
                                builder.setDescription("Sorry, looks like the queue is full right now.  Try again later.");
                                reply(event, builder.build());
                            }
                        } else {
                            // NOT ENOUGH POINTS
                            builder.setDescription("Sorry, it looks like you do not have enough points to call that one.");
                            builder.addField("Available Cashews:",points.toString(),true);
                            builder.addField("Required Cashews:",String.valueOf(animation.getCost()),true);

                            reply(event, builder.build());
                        }
                    } else {
                        // Animation is not enabled
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
