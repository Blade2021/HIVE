package rsystems.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class GratitudeListener extends ListenerAdapter {

    private static ArrayList<String> coolDownChannels = new ArrayList<>();

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        String[] triggers = {"thanks", "thank you", "thnx", "thx "};

        for (String trigger : triggers) {
            if (event.getMessage().getContentDisplay().toLowerCase().contains(trigger)) {
                if ((!event.getMessage().getMentionedMembers().isEmpty()) || (event.getMessage().getReferencedMessage() != null)) {

                    Member receivingMember = null;
                    if (!event.getMessage().getMentionedMembers().isEmpty()) {
                        // Assign the receiver to the first mentioned member
                        receivingMember = event.getMessage().getMentionedMembers().get(0);
                    } else {
                        if (event.getMessage().getReferencedMessage().getMember() != null) {
                            // Assign the receiver to the replied message's author
                            receivingMember = event.getMessage().getReferencedMessage().getMember();
                        }
                    }

                    Member sendingMember = event.getMember();

                    if ((receivingMember != null) && (!receivingMember.getUser().isBot()) && (receivingMember != sendingMember)) {

                        event.getMessage().addReaction("FailFish:763254562026815489").queue();
                        event.getMessage().addReaction("❌").queue();

                        //karmaStagingMap.putIfAbsent(event.getMessageIdLong(), event.getMember().getIdLong());

                        if(!HiveBot.karmaSQLHandler.insertStaging(event.getChannel().getIdLong(), event.getMessageIdLong(),sendingMember.getIdLong())){
                            event.getMessage().addReaction("⚠").queue();
                        }
                    } else {
                        event.getMessage().addReaction("\uD83E\uDD54").queue();
                    }

                } else {
                    if (!coolDownChannels.contains(event.getChannel().getId())) {

                        for (String s : triggers) {
                            if (event.getMessage().getContentRaw().toLowerCase().contains(s)) {
                                event.getChannel().sendMessage("Don't forget to send karma! <:KU:717177145717424180> ~karma for more info!").queue(success -> {
                                    success.delete().queueAfter(10, TimeUnit.MINUTES);
                                });

                                new Thread(new Runnable() {
                                    public void run() {
                                        try {
                                            System.out.println("Adding " + event.getChannel().getName() + " to cooldown");
                                            coolDownChannels.add(event.getChannel().getId());
                                            //Sleep the thread for 10 minutes
                                            Thread.sleep(600000);
                                        } catch (InterruptedException ie) {
                                        }
                                        //Remove the entry from the HashMap
                                        Iterator it = coolDownChannels.iterator();
                                        while (it.hasNext()) {
                                            String checkId = (String) it.next();
                                            if (checkId.equalsIgnoreCase(event.getChannel().getId())) {
                                                it.remove();
                                            }
                                        }
                                    }
                                }).start();
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot())
            return;

        final Long messageID = event.getMessageIdLong();

        if (HiveBot.karmaSQLHandler.checkStaging(messageID, event.getMember().getIdLong())) {
            System.out.println("KarmaStagingMap ID Found: " + messageID);
            //Is the reaction an emote?
            if ((event.getReaction().getReactionEmote().isEmote()) ||
                    ((event.getReaction().getReactionEmote().isEmoji()) && (event.getReaction().getReactionEmote().getEmoji().equalsIgnoreCase("❌")))) {

                String removalEmote = null;

                if (event.getReaction().getReactionEmote().isEmote()) {
                    Emote emote = event.getReactionEmote().getEmote();
                    if (emote.getId().equalsIgnoreCase("763254562026815489")) {
                        removalEmote = "❌";
                    }
                } else if (event.getReaction().getReactionEmote().isEmoji()) {
                    if (event.getReaction().getReactionEmote().getEmoji().equalsIgnoreCase("❌")) {
                        removalEmote = "763254562026815489";
                    }

                }

                if (removalEmote != null) {
                    //Get a list of the past 100 messages
                    String finalRemovalEmote = removalEmote;

                    event.getChannel().retrieveMessageById(event.getMessageId()).queue(success -> {
                        if (finalRemovalEmote.equalsIgnoreCase("❌")) {
                            success.removeReaction("❌", HiveBot.jda.getSelfUser()).queue();
                        } else {
                            success.removeReaction(event.getGuild().getEmoteById("763254562026815489"), HiveBot.jda.getSelfUser()).queue();
                        }

                        HiveBot.karmaSQLHandler.deleteFromStaging(event.getMessageIdLong());
                    });
                }
            }
        }

    }

}
