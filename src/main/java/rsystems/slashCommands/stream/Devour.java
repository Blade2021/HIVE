package rsystems.slashCommands.stream;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
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

        reply(event,"We have moved over to StreamHook.  Please use /Here for that Bot instead");

/*
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

                builder.setDescription("You are already in the queue.  Please wait a while and try again.\n");
                builder.appendDescription(getQueuePositionString(queuePosition));
                reply(event,builder.build());

            } else {
                // User not found in the queue

                try {
                    // Grab the animation
                    final StreamAnimation animation = HiveBot.database.getAnimation(event.getOption("animation-id").getAsInt());

                    // Grab the points the user has
                    Integer points = HiveBot.database.getInteger("EconomyTable","Points","UserID",sender.getIdLong());

                    // Check if animation exists, animations is enabled, and points do not exist
                    if(animation != null && animation.isEnabled() && points != null) {

                        // Does the user have enough points
                        if (animation.getCost() <= points) {

                            DispatchRequest request = new DispatchRequest(sender.getIdLong(), animation.getId());

                            // Get the place in the queue
                            final Integer requestResult = HiveBot.streamHandler.submitRequest(request);

                            if ((requestResult != null) && (requestResult >= 0)) {
                                // REQUEST ACCEPTED

                                //Get the amount of points left AFTER the deduction for the animation request
                                points = points - animation.getCost();

                                builder.setDescription("Your request has been submitted!\n");
                                builder.appendDescription(getQueuePositionString(requestResult));
                                builder.addField("Available Cashews:", points.toString(), true);
                                builder.setFooter(request.getID_String());
                                builder.addBlankField(true);
                                reply(event, builder.build());

                            } else {
                                // FULL QUEUE
                                builder.setDescription("Sorry, looks like the queue is full right now.  Please try again later.");
                                builder.setTitle("Stream Queue is Full");
                                builder.setFooter("Error: 403");
                                builder.setColor(HiveBot.getColor(HiveBot.colorType.ERROR));
                                reply(event, builder.build());
                            }
                        } else {
                            // NOT ENOUGH POINTS
                            builder.setDescription("Sorry, it looks like you do not have enough points to call that one.");
                            builder.addField("Available Cashews:", points.toString(), true);
                            builder.addField("Required Cashews:", String.valueOf(animation.getCost()), true);
                            builder.setTitle("Not Enough Points");
                            builder.setFooter("Error: 402");
                            builder.setColor(HiveBot.getColor(HiveBot.colorType.ERROR));

                            reply(event, builder.build());
                        }
                    } else if(animation != null && animation.isEnabled() && points == null){
                        // Animation is not enabled
                        builder.setColor(HiveBot.getColor(HiveBot.colorType.ERROR));
                        builder.setTitle("No User Found");
                        builder.setFooter("Error: 404");
                        builder.setDescription("Looks like your not in our system yet.  \nHave you done `/Here` during a livestream?\n\nIf you believe this is an error. Please contact BLADE here on discord");
                        reply(event, builder.build());
                    } else {
                        // Animation is not enabled
                        builder.setDescription("Sorry, looks like that animation is currently disabled.  Please try another.");
                        builder.setTitle("Animation Disabled");
                        builder.setFooter("Error: 401");
                        builder.setColor(HiveBot.getColor(HiveBot.colorType.ERROR));
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

 */
    }

    @Override
    public String getDescription() {
        return "Devour your cashews!";
    }

    private String getQueuePositionString(int position){
        String response = null;
        switch(position){
            case 0:
                response = ("You are **first** in the queue.");
                break;
            case 1:
                response = ("You are **second** in the queue.");
                break;
            case 2:
                response = ("You are **third** in the queue.");
                break;
            case 3:
                response = ("You are **forth** in the queue.");
                break;
            case 4:
                response = ("You are **fifth** in the queue.");
                break;
            default:
                response = (String.format("You are %d in the queue.",position));
                break;
        }

        return response;
    }
}
