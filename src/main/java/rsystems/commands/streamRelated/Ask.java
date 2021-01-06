package rsystems.commands.streamRelated;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ask extends Command {

    private static final Long questionPullChannel = Long.valueOf(Config.get("QuestionPullChannel"));
    private static final Long questionPushChannel = Long.valueOf(Config.get("QuestionPushChannel"));
    private static final Long reStreamID = Long.valueOf(Config.get("ReStreamID"));


    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        if (channel.getIdLong() == questionPullChannel) {

            if (event.getAuthor().isBot()) {
                if (event.getAuthor().getIdLong() != reStreamID) {
                    return;
                }
            }

            if (content.length() <= 5) {
                //Link was not long enough to verify
                System.out.println("Question was to short");
                return;
            }

            String author = getAuthor(event, content);
            if (author != null) {

                //List<Message> messages = new ArrayList<>();

                event.getChannel().getHistory().retrievePast(100).queue(messages -> {
                            int msgCount = 0;

                            for (Message m : messages) {
                                String checkContent = m.getContentDisplay().replaceFirst("!ask ","");
                                if (checkContent.equalsIgnoreCase(content)) {
                                    msgCount++;
                                    if (msgCount >= 2) {
                                        System.out.println(checkContent);
                                        System.out.println(content);
                                        System.out.println(msgCount);
                                        event.getMessage().addReaction("⚠").queue();
                                        return;
                                    }
                                }
                            }

                            EmbedBuilder questionBuilder = new EmbedBuilder()
                                    .setTitle("Requester: " + author)
                                    .addField("**Question:**",content,false);

                            Random rand = new Random();
                            float r = rand.nextFloat();
                            float g = rand.nextFloat();
                            float b = rand.nextFloat();

                            Color randomColor = new Color(r,g,b);
                            questionBuilder.setColor(randomColor);

                            final TextChannel pushChannel = HiveBot.drZzzGuild().getTextChannelById(questionPushChannel);
                            if(pushChannel != null) {
                                pushChannel.sendMessage(questionBuilder.build()).queue();
                                event.getMessage().addReaction("\uD83D\uDCE8").queue();
                            }
                            questionBuilder.clear();

                        });
            }


        }
    }

    @Override
    public String getHelp() {
        return null;
    }

    private String getAuthor(GuildMessageReceivedEvent event, String message) {

        // Initialize author
        String author = null;
        // Does message contain brackets?
        if ((message.contains("[")) && (message.contains("]"))) {
            try {
                // Get locations of brackets
                int openBracketLocation = message.indexOf("[");
                int closeBracketLocation = message.indexOf("]");
                // Grab author, and strip youtube and twitch from author
                author = message.substring(openBracketLocation + 1, closeBracketLocation).replaceFirst("YouTube:", "").replaceFirst("Twitch:", "");
                //author = author + " : ";
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("Could not find author");
            }
        } else {
            author = event.getMessage().getAuthor().getName();
            //author = author + " : ";
        }

        return author;
    }
}
