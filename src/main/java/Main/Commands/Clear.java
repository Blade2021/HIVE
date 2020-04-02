package Main.Commands;

import Main.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;

public class Clear extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Don't accept messages from BOT Accounts
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase((Main.prefix + "clear"))){
            if(args.length < 2){
                // No Argument Detected
                EmbedBuilder error = new EmbedBuilder();
                error.setColor(Color.CYAN);
                error.setTitle("Specify amount to delete");
                error.setDescription("Usage: `" + Main.prefix + "clear [#of messages]`");
                event.getChannel().sendMessage(error.build()).queue();
            } else {
                try {
                    List<Message> messages = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1])+1).complete();
                    event.getChannel().deleteMessages(messages).queue();

                    // Too many messages
                    EmbedBuilder success = new EmbedBuilder();
                    success.setColor(Color.blue);
                    success.setTitle("Successfully deleted " + args[1] + " messages.");
                    success.setFooter("Command called by: " + event.getMessage().getAuthor().getName(), event.getAuthor().getAvatarUrl());
                    event.getChannel().sendMessage(success.build()).queue();
                }
                catch (IllegalArgumentException e){
                    if (e.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                        // Too many messages
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(Color.RED);
                        error.setTitle("\uD83D\uDEAB Too many messages selected");
                        error.setDescription("Between 1-100 messages can be deleted at one time.");
                        event.getChannel().sendMessage(error.build()).queue();
                    } else {
                        // Messages too old
                        EmbedBuilder error = new EmbedBuilder();
                        error.setColor(Color.RED);
                        error.setTitle("\uD83D\uDEAB Selected messages are older than 2 weeks");
                        error.setDescription("Messages older than 2 weeks cannot be deleted.");
                        event.getChannel().sendMessage(error.build()).queue();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
