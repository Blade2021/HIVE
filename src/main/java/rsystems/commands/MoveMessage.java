package rsystems.commands;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import javax.annotation.Nonnull;

public class MoveMessage extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (HiveBot.commands.get(82).checkCommand(event.getMessage().getContentRaw())) {

            event.getChannel().retrieveMessageById(args[1]).queue(success -> {

                TextChannel pushChannel = event.getMessage().getMentionedChannels().get(0);

                pushChannel.sendMessage(success.getContentRaw()).queue(pushSuccess -> {
                    for(MessageReaction r:success.getReactions()){
                        try {
                            if (r.getReactionEmote().isEmote()) {
                                pushSuccess.addReaction(r.getReactionEmote().getEmote()).queue();
                            } else {
                                pushSuccess.addReaction(r.getReactionEmote().getEmoji()).queue();
                            }
                        }catch(IllegalArgumentException e){
                            System.out.println("Failed to add reaction to message due to reaction wan't available");
                        }
                    }
                });
            });
        }
    }


}

