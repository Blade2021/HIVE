package rsystems.commands.modCommands;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.LOGGER;
import static rsystems.HiveBot.commands;

public class Mute extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");


        /*
        MUTE USER COMMAND
         */
        if (HiveBot.commands.get(79).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), commands.get(79))) {

                int minutes = 0;

                // Convert args[1] to a integer for minutes
                try {
                    minutes = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You forgot to include an amount of time.").queue();
                    // Potato Emoji (FIRE THE POTATO!)
                    event.getMessage().addReaction("\uD83E\uDD54").queue();
                    return;
                }

                // Message contains mentioned members
                if (event.getMessage().getMentionedMembers().size() > 0) {
                    // Call mute member function for each MENTIONED member
                    int finalMinutes = minutes;
                    event.getMessage().getMentionedMembers().forEach(member -> {
                        muteMember(member, event.getGuild(), finalMinutes, event.getMember());
                    });
                    // Try using IDs
                } else {
                    for (int i = 2; i < args.length; i++) {
                        try {
                            muteMember(event.getGuild().getMemberById(args[i]), event.getGuild(), minutes, event.getMember());
                        } catch (NullPointerException e) {
                            System.out.println("I found an error when trying to mute a user");
                        }
                    }
                }
            }
        }
        /*
        MUTE CHANNEL COMMAND
         */
            if (HiveBot.commands.get(80).checkCommand(event.getMessage().getContentRaw())) {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), commands.get(80))) {
                    try {
                        int timeout = 0;
                        try {
                            // Parse argument 1 into an integer
                            timeout = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " INVALID SYNTAX.").queue();
                            event.getMessage().addReaction("\uD83E\uDD54").queue();
                            return;
                        }
                        //Initialize the string builder for errors to be put into
                        StringBuilder errors = new StringBuilder();

                        if (event.getMessage().getMentionedChannels().size() == 0) {
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " you did not mention any channels.").queue();
                            return;
                        } else {

                            //Delete the trigger message
                            event.getMessage().delete().queue();

                            // Get a list of all Text Channels that were mentioned for processing
                            List<TextChannel> mentionedChannels = event.getMessage().getMentionedChannels();
                            for (TextChannel channel : mentionedChannels) {
                                try {

                                    //Write the timeout value to a final variable
                                    int finalTimeout = timeout;

                                    //Attach the deny override
                                    channel.putPermissionOverride(event.getGuild().getPublicRole()).setDeny(Permission.MESSAGE_WRITE).queue(success -> {

                                        //Notify the channel
                                        channel.sendMessage("This channel is being put on cooldown by " + event.getMessage().getAuthor().getAsMention() + ".  See you in " + finalTimeout + " minutes").queue(cooldownSuccess -> {
                                            cooldownSuccess.delete().queueAfter(finalTimeout, TimeUnit.MINUTES);
                                        });


                                        //Clear the deny override after the given amount of time from argument 1
                                        channel.putPermissionOverride(event.getGuild().getPublicRole()).clear(Permission.MESSAGE_WRITE).queueAfter(finalTimeout, TimeUnit.MINUTES);
                                        channel.sendMessage("Channel is now unlocked.  Please be sure to abide all rules of the server!").queueAfter(finalTimeout, TimeUnit.MINUTES);
                                    });


                                } catch (InsufficientPermissionException e) {
                                    // Create a message to notify that the BOT does not have permission to set an override
                                    errors.append("❗").append("Missing permission[" + e.getPermission() + "] for channel: " + channel.getName()).append("\n");
                                }
                            }
                        }

                        // Were there errors found, If so send a message
                        if (!errors.toString().isBlank()) {
                            event.getChannel().sendMessage(errors.toString()).queue();
                        }

                    } catch (NullPointerException e) {
                        System.out.println("Found null looking for channel");
                    }
                }
            }
        }



            private void muteMember (Member member, Guild guild,int time, Member mod){
                try {
                    // Add mute role to member
                    guild.modifyMemberRoles(member, guild.getRolesByName("Mute", true), null).queue(success -> {
                        System.out.println("Successfully muted " + member.getEffectiveName());
                        try {
                            member.getUser().openPrivateChannel().queue(user -> {
                                user.sendMessage("You have been muted for " + time + " minute(s) by " + mod.getUser().getAsTag() + ".  Please take a minute to review the rules of the server.").queue(dmSuccess -> {
                                    System.out.println("Sent user a direct message alerting them of the mute");
                                }, dmFailure -> {
                                    System.out.println("Failed to send direct message due to privacy settings");
                                });
                            });
                        } catch (NullPointerException e) {
                            System.out.println("Could not find user");
                        } catch (ErrorResponseException userError) {
                            if (userError.getErrorCode() == 50007) {
                                System.out.println("Attempted to send direct message but privacy settings blocked message");
                            }
                        }

                        //Unmute user after x amount of minutes
                        guild.modifyMemberRoles(member, null, guild.getRolesByName("Mute", true)).queueAfter(time, TimeUnit.MINUTES);
                    });

                } catch (NullPointerException e) {
                    System.out.println("Could not find role");
                }
            }

        }
