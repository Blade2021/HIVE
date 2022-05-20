package rsystems.slashCommands.stream;

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
            if(HiveBot.streamHandler.checkListForUser(sender.getIdLong())){
                // User already found

                reply(event,"You are already in the queue.  Please wait a while and try again");

            } else {
                // User not found


                try {
                    final StreamAdvert advert = HiveBot.database.getAdvert(event.getOption("advert-id").getAsInt());
                    final Integer points = HiveBot.database.getInteger("EconomyTable","Points","UserID",sender.getIdLong());

                    if(advert.isEnabled()){
                        if(advert.getCost() <= points){
                            if(HiveBot.streamHandler.submitRequest(new DispatchRequest(sender.getIdLong(),advert.getId()))){
                                reply(event,"Your request has been submitted");
                            } else {
                                reply(event, "Looks like the queue is full right now.  Try again later.");
                            }
                        } else {
                            reply(event, "Sorry, it looks like you do not have enough points to call that one");
                        }
                    } else {
                        // Advert is not enabled
                        reply(event,"Sorry that advert is disabled right now.");
                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
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
