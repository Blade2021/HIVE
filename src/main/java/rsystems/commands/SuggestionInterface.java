package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.suggestionHandler;


public class SuggestionInterface extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        /*
        CREATE A SUGGESTION APPROVAL REQUEST
         */
        if (HiveBot.commands.get(71).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(71))) {
                if (args.length > 1) {
                    //Create the suggestion request
                    int suggestionID = suggestionHandler.createSuggestion(event.getAuthor().getId(), event.getMessage().getContentDisplay().substring(args[0].length() + 1));
                    event.getChannel().sendMessage("Your suggestion has been submitted for review.  ID: " + suggestionID).queue();

                    suggestionHandler.postSuggestionReview(suggestionID,event.getGuild());

                } else {
                    event.getChannel().sendMessage("Did you forget to write your suggestion? \uD83E\uDD14").queue();
                }
            }
        }

        /*
        ACCEPT A SUGGESTION REQUEST
         */
        if (HiveBot.commands.get(72).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(72))) {
                int suggestionID = Integer.parseInt(args[1]);

                if (suggestionHandler.getFieldInt(suggestionID, "status") == 0) {
                    suggestionHandler.postSuggestion(suggestionID,event.getGuild());
                    removeApprovalRequest(event.getChannel(), suggestionHandler.getFieldString(suggestionID,"pollMessageID"));
                } else {
                    event.getChannel().sendMessage("That suggestion has already been processed").queue();
                }
            }
        }


        /*
        REJECT A SUGGESTION REQUEST
         */
        if (HiveBot.commands.get(73).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(73))) {
                int suggestionID = Integer.parseInt(args[1]);

                if (suggestionHandler.getFieldInt(suggestionID, "status") == 0) {
                    removeApprovalRequest(event.getChannel(), suggestionHandler.getFieldString(suggestionID,"pollMessageID"));
                    suggestionHandler.updateRowInt(suggestionID, "status", 5);

                    String userid = suggestionHandler.getFieldString(suggestionID, "requesterID");
                    try {
                        event.getGuild().getMemberById(userid).getUser().openPrivateChannel().queue(success -> {
                            success.sendMessage("We regret to inform you that your suggestion request (ID: " + suggestionID + " ) was denied.  Please contact the staff for more information.").queue();
                        });
                    } catch(NullPointerException e){
                        System.out.println("Could not find user: " + userid);
                    }

                } else {
                    event.getChannel().sendMessage("That suggestion has already been processed").queue();
                }
            }
        }


        /*
        REJECT A SUGGESTION
         */

        /*
        if (HiveBot.commands.get(73).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(73))) {
                int suggestionID = Integer.parseInt(args[1]);

                if (suggestionHandler.getFieldInt(suggestionID, "status") == 0) {
                    removeApprovalRequest(event.getChannel(), suggestionHandler.getFieldString(suggestionID,"pollMessageID"));
                    suggestionHandler.updateRowInt(suggestionID, "status", 5);
                } else {
                    event.getChannel().sendMessage("That suggestion has already been processed").queue();
                }
            }
        }
         */

        /*
        SET STATUS MESSAGE OF SUGGESTION
         */
        if (HiveBot.commands.get(74).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(74))) {
                if(args.length >= 2){
                    try {
                        int suggestionID = Integer.parseInt(args[1]);

                        if(args.length == 2){
                            String status = suggestionHandler.getFieldString(suggestionID,"statusMessage");
                            try {
                                if (!status.isEmpty()) {
                                    event.getChannel().sendMessage("Current Status:\n" + status).queue();
                                }
                            } catch (NullPointerException e){
                            }
                        } else {
                            int messageStart = (args[0].length() + args[1].length() + 2);
                            suggestionHandler.setStatusMessage(suggestionID, event.getGuild(), event.getMessage().getContentRaw().substring(messageStart));
                        }
                    } catch(NumberFormatException e){
                        event.getChannel().sendMessage("Could not find ID of suggestion").queue();
                    }
                } else {
                    event.getChannel().sendMessage("Not enough parameters were given").queue();
                }
            }
        }

        /*
        APPROVE SUGGESTION
         */
        if (HiveBot.commands.get(75).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(75))) {
                int suggestionID = Integer.parseInt(args[1]);
                if(args.length >= 3){
                    suggestionHandler.handleSuggestion(suggestionID,event.getGuild(),true,event.getMessage().getContentRaw().substring((args[0].length() + args[1].length() + 2)));
                } else {
                    suggestionHandler.handleSuggestion(suggestionID, event.getGuild(), true, "Accepted");
                }
            }
        }

        /*
        DENY SUGGESTION
         */
        if (HiveBot.commands.get(76).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(76))) {
                int suggestionID = Integer.parseInt(args[1]);
                if(args.length >= 3){
                    suggestionHandler.handleSuggestion(suggestionID,event.getGuild(),false,event.getMessage().getContentRaw().substring((args[0].length() + args[1].length() + 2)));
                } else {
                    suggestionHandler.handleSuggestion(suggestionID, event.getGuild(), false, "Denied");
                }
            }
        }

        /*
        OVERRIDE MESSAGE
         */
        if (HiveBot.commands.get(77).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(77))) {

                try {

                    int suggestionID = Integer.parseInt(args[1]);
                    int currentStatus = suggestionHandler.getFieldInt(suggestionID, "status");

                    if (currentStatus == 0) {
                        String newMessage = event.getMessage().getContentDisplay().substring(args[0].length() + args[1].length() + 2);
                        suggestionHandler.updateRowString(suggestionID, "suggestion", newMessage);

                        //Send request notification to appropriate channels
                        TextChannel suggestionOutputChannel = event.getGuild().getTextChannelById("710656936735014952"); //todo Change hardcoded channel id to guild data

                        try {
                            //Grab the message
                            Message message = suggestionHandler.grabSuggestionMessage(suggestionOutputChannel, suggestionHandler.getFieldString(suggestionID, "pollMessageID"));

                            //Create a new temporary embed to hold the data
                            EmbedBuilder tempEmbed = new EmbedBuilder();

                            //Move previous entries into the new temporary embed
                            List<MessageEmbed> embeds = message.getEmbeds();
                            for (MessageEmbed me : embeds) {
                                tempEmbed.setTitle(me.getTitle());
                                tempEmbed.setDescription(newMessage);
                                tempEmbed.appendDescription("\n\nTo approve:");
                                tempEmbed.appendDescription("```\n" + HiveBot.prefix + "suggestionaccept " + suggestionID + "\n```");
                                tempEmbed.appendDescription("\nTo reject:");
                                tempEmbed.appendDescription("```\n" + HiveBot.prefix + "suggestionreject " + suggestionID + "\n```");
                                tempEmbed.appendDescription("\nOr click one of of the reactions below.");
                            }

                            //Replace current embed with temporary one
                            message.editMessage(tempEmbed.build()).override(true).queue();
                            tempEmbed.clear();

                        } catch (NullPointerException e) {
                            System.out.println("Could not find message");
                        }

                    } else if(currentStatus == 1){
                        event.getChannel().sendMessage("Sorry " + event.getAuthor().getAsMention() + ", That suggestion is already submitted as a poll.").queue();
                    }

                }catch(NumberFormatException e){
                    event.getChannel().sendMessage("I could not find a suggestion with that ID").queue();
                }
            }
        }


        /*
        GET OPEN SUGGESTIONS (VOTING)
         */
        if (HiveBot.commands.get(78).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(78))) {

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Open suggestions:");
                StringBuilder unacceptedStringBuilder = new StringBuilder();
                StringBuilder votingStringBuilder = new StringBuilder();

                //Process through OPEN suggestions
                for (Map.Entry<Integer, String> entry : suggestionHandler.openSuggestionMap(0).entrySet()) {
                    unacceptedStringBuilder.append(entry.getKey()).append("\n");
                }

                //Process through OPEN suggestions
                for (Map.Entry<Integer, String> entry : suggestionHandler.openSuggestionMap(1).entrySet()) {
                    votingStringBuilder.append(entry.getKey()).append("\n");
                }

                embedBuilder.addField("Unaccepted Suggestions",unacceptedStringBuilder.toString(),true);
                embedBuilder.addField("Taking Votes",votingStringBuilder.toString(),true);

                event.getChannel().sendMessage(embedBuilder.build()).queue();
                embedBuilder.clear();
            }
        }

    }

    private void removeApprovalRequest(TextChannel channel, String messageID) {
        try {
            List<Message> messages = channel.getHistory().retrievePast(100).complete();

            //Remove the approval request
            Iterator it = messages.iterator();
            while (it.hasNext()) {
                Message tempMessage = (Message) it.next();
                if (tempMessage.getId().equalsIgnoreCase(messageID)) {
                    tempMessage.addReaction("\uD83D\uDCE8").queue();
                    tempMessage.delete().queueAfter(30, TimeUnit.SECONDS);
                }
            }
        } catch (NullPointerException e) {
            System.out.println("couldn't find message");
        }
    }
}
