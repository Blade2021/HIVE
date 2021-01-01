package rsystems.events;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GratitudeListener extends ListenerAdapter {

    public static ArrayList<String> coolDownChannels = new ArrayList<>();
    public static Map<Long, Long> karmaStagingMap = new HashMap<>();

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
                    final Long messageID = event.getMessageIdLong();

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

                    if((receivingMember != null) && (!receivingMember.getUser().isBot()) && (receivingMember != sendingMember)) {

                            event.getMessage().addReaction("FailFish:763254562026815489").queue();
                            event.getMessage().addReaction("❌").queue();

                            karmaStagingMap.putIfAbsent(event.getMessageIdLong(), event.getMember().getIdLong());
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

        //Does our map contain the messageID?
        if (karmaStagingMap.containsKey(messageID)) {
            System.out.println("KarmaStagingMap ID Found: " + messageID);

            //Did the author of the message add the reaction?
            if (karmaStagingMap.get(messageID).equals(event.getMember().getIdLong())) {

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

                            karmaStagingMap.remove(messageID);
                        });
                    }
                }
            }
        }
    }

}
