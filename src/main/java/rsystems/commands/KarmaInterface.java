package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.KarmaUserInfo;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.*;

public class KarmaInterface extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        /*
        //Karma Help command
        if (HiveBot.commands.get(62).checkCommand(event.getMessage().getContentRaw())) {

            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(62))) {
                    if (args.length >= 2 && args[1].equalsIgnoreCase("-show")) {
                        if (RoleCheck.getRank(event.getGuild(), event.getMember().getId()) >= 1) {
                            karmaExplanation(event.getMessage(), true);
                        }
                    } else {
                        karmaExplanation(event.getMessage(), false);
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }
        */


        //Karma Help command short
        if (HiveBot.commands.get(70).checkCommand(event.getMessage().getContentRaw())) {

            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(70))) {

                    try{
                        event.getMessage().delete().queue();
                    } catch (NullPointerException | PermissionException e){};


                    karmaExplanationShort(event.getMessage(),true);
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //get Karma
        if (HiveBot.commands.get(49).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if ((RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(49))) && (event.getMessage().getMentionedMembers().size() == 0)) {
                    int karma = HiveBot.karmaSQLHandler.getKarma(event.getMember().getId());
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Your current karma status: 🔹" + karma).queue();
                } else if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(54))) {
                    StringBuilder output = new StringBuilder();
                    event.getMessage().getMentionedMembers().forEach(member -> {
                        int karma = HiveBot.karmaSQLHandler.getKarma(member.getId());
                        output.append(member.getEffectiveName() + " has : 🔹" + karma).append("\n");
                    });
                    event.getChannel().sendMessage(output.toString()).queue();
                    return;
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //get points
        if (HiveBot.commands.get(51).checkCommand(event.getMessage().getContentRaw())) {
            try {
                try {
                    event.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
                } catch (PermissionException | NullPointerException e) {
                }

                if ((RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(51))) && (event.getMessage().getMentionedMembers().size() == 0)) {
                    int karmaPoints = HiveBot.karmaSQLHandler.getAvailableKarmaPoints(event.getMember().getId());
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You have " + karmaPoints + " available points").queue();
                    return;
                } else if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(55))) {
                    StringBuilder output = new StringBuilder();
                    event.getMessage().getMentionedMembers().forEach(member -> {
                        int karmaPoints = HiveBot.karmaSQLHandler.getAvailableKarmaPoints(member.getId());
                        output.append(member.getEffectiveName() + " has : " + karmaPoints + " points").append("\n");
                    });
                    event.getChannel().sendMessage(output.toString()).queue();
                    return;
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //sendPositiveKarma
        //if (args[0].equalsIgnoreCase(HiveBot.karmaPrefixPositive)) {
        if(HiveBot.commands.get(53).checkCommand(event.getMessage().getContentRaw(), "")){

            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(53))) {
                if (event.getMessage().getMentionedMembers().size() > 0) {
                    event.getMessage().getMentionedMembers().forEach(member -> {
                        try {
                            runUpdateKarma(event.getGuild(), event.getMessage(), event.getMember(), member, true);
                        } catch (NullPointerException e) {
                            System.out.println("Found null when sending positive karma");
                        }
                    });
                } else {
                    try {
                        runUpdateKarma(event.getMessage(), event.getMember(), args[1], event.getGuild(), true);
                    } catch (NullPointerException e) {
                        System.out.println("Found null when trying to send karma");
                    } catch (IndexOutOfBoundsException e) {
                        event.getChannel().sendMessage("You didn't provide enough arguments").queue();
                    }
                }
            }

        }

        //sendNegativeKarma
        if (args[0].equalsIgnoreCase(HiveBot.karmaPrefixNegative)) {

            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(57))) {
                if (event.getMessage().getMentionedMembers().size() > 0) {
                    event.getMessage().getMentionedMembers().forEach(member -> {
                        try {
                            runUpdateKarma(event.getGuild(), event.getMessage(), event.getMember(), member, false);
                        } catch (NullPointerException e) {
                            System.out.println("Found null when sending negative karma");
                        }
                    });

                } else {
                    try {
                        runUpdateKarma(event.getMessage(), event.getMember(), args[1], event.getGuild(), false);
                    } catch (NullPointerException e) {
                        System.out.println("Found null when trying to send karma");
                    } catch (IndexOutOfBoundsException e) {
                        event.getChannel().sendMessage("You didn't provide enough arguments").queue();
                    }
                }
            }
        }

        //set available points
        if (HiveBot.commands.get(50).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(50))) {

                    if (event.getMessage().getMentionedMembers().size() > 0) {
                        event.getMessage().getMentionedMembers().forEach(member -> {
                            HiveBot.karmaSQLHandler.overrideKarmaPoints(member.getId(), Integer.parseInt(args[1]));
                        });
                    } else if (args[2].equalsIgnoreCase("staff")) {
                        //ArrayList<Member> staffMembers = new ArrayList<>();
                        ArrayList<Role> staffRoles = new ArrayList<>();
                        staffRoles.add(event.getGuild().getRoleById("469334570094034948"));
                        staffRoles.add(event.getGuild().getRoleById("469334775354621973"));
                        staffRoles.add(event.getGuild().getRoleById("698343546037731429"));
                        staffRoles.add(event.getGuild().getRoleById("698369672646623242"));
                        System.out.println("Adding points to: " + staffRoles.toString());
                        //staffMembers.addAll(event.getGuild().getMembersWithRoles(staffRoles));

                        for (Role r : staffRoles) {
                            for (Member m : event.getGuild().getMembersWithRoles(r)) {
                                HiveBot.LOGGER.info("Overriding points for: " + m.getId() + " New Total: " + args[1]);
                                HiveBot.karmaSQLHandler.overrideKarmaPoints(m.getId(), Integer.parseInt(args[1]));
                            }
                        }
                    } else {
                        HiveBot.karmaSQLHandler.overrideKarmaPoints(args[2], Integer.parseInt(args[1]));

                    }
                    event.getMessage().addReaction("\uD83D\uDCE8").queue();
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //set karma
        if (HiveBot.commands.get(52).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(52))) {
                    if (event.getMessage().getMentionedMembers().size() > 0) {
                        event.getMessage().getMentionedMembers().forEach(member -> {
                            try {
                                HiveBot.karmaSQLHandler.overrideKarma(member.getId(), Integer.parseInt(args[1]));
                            } catch (NullPointerException e) {
                                System.out.println("Couldn't find member");
                            }
                        });
                        event.getMessage().addReaction("\uD83D\uDCE8").queue();
                        return;
                    } else {

                        if (HiveBot.karmaSQLHandler.overrideKarma(args[1], Integer.parseInt(args[2]))) {
                            event.getMessage().addReaction("\uD83D\uDCE8").queue();
                        }
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //poll for online users
        if (HiveBot.commands.get(59).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(59))) {

                    for (Member member : event.getGuild().getMembers()) {

                        if (member.getUser().isBot()) {
                            return;
                        }

                        //Get the last date of karma increment
                        String lastSeenKarma = karmaSQLHandler.getDate(member.getId());

                        //Insert new user if not found in DB
                        if (lastSeenKarma.isEmpty()) {

                            //Only online users
                            if (member.getOnlineStatus().equals(OnlineStatus.ONLINE)) {

                                //Initiate the formatter for formatting the date into a set format
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

                                //Get the current date
                                LocalDate currentDate = LocalDate.now();

                                //Format the current date into a set format
                                String formattedCurrentDate = formatter.format(currentDate);

                                if (karmaSQLHandler.insertUser(member.getId(), member.getUser().getAsTag(), formattedCurrentDate, "KARMA")) {
                                    LOGGER.severe("Failed to add " + member.getUser().getAsTag() + " to honeyCombDB");
                                } else {
                                    LOGGER.info("Added " + member.getUser().getAsTag() + " to honeyCombDB. Table: KARMA");
                                    karmaSQLHandler.overrideKarmaPoints(member.getId(), 5);
                                }
                            }
                        }
                    }

                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //Delete User
        if (HiveBot.commands.get(60).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(60))) {
                    if (HiveBot.karmaSQLHandler.deleteUser(args[1])) {
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        event.getMessage().addReaction("🚫").queue();
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //Get Date
        if (HiveBot.commands.get(63).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(63))) {
                    if (event.getMessage().getMentionedMembers().size() == 0) {
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " | " + karmaSQLHandler.getDate(event.getMember().getId(), "KARMA")).queue();
                    } else {
                        StringBuilder dateString = new StringBuilder();
                        event.getMessage().getMentionedMembers().forEach(member -> {
                            dateString.append(member.getEffectiveName()).append(" | ").append(karmaSQLHandler.getDate(member.getId(), "KARMA")).append("\n");
                        });
                        event.getChannel().sendMessage(dateString.toString()).queue();
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //Get Top 10
        if (HiveBot.commands.get(61).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(61))) {
                    try {
                        event.getMessage().delete().queue();
                    } catch (PermissionException | NullPointerException e) {
                    }

                    StringBuilder nameString = new StringBuilder();
                    StringBuilder rankString = new StringBuilder();

                    for (Map.Entry<String, Integer> entry : karmaSQLHandler.getTopTen().entrySet()) {
                        nameString.append(entry.getKey()).append("\n");
                        rankString.append(entry.getValue()).append("\n");
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Karma: TOP 10");
                    embedBuilder.addField("Name:", nameString.toString(), true);
                    embedBuilder.addField("Karma:", rankString.toString(), true);

                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                    embedBuilder.clear();
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //Get all user info for karma
        if (HiveBot.commands.get(64).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(64))) {

                    if (event.getMessage().getMentionedMembers().size() > 0) {
                        getKarmaInfo(event.getGuild(), event.getMessage(), true, event.getMessage().getMentionedMembers().get(0).getUser().getId());
                    } else {
                        if(args.length > 1) {
                            getKarmaInfo(event.getGuild(), event.getMessage(), true, args[1]);
                        } else {
                            getKarmaInfo(event.getGuild(),event.getMessage(),true,event.getMember().getId());
                        }
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }


        //Master Override
        if (HiveBot.commands.get(58).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(58))) {
                    if (args[1].equalsIgnoreCase("points")) {
                        if (HiveBot.karmaSQLHandler.masterOverridePoints(args[2])) {
                            event.getMessage().addReaction("\uD83D\uDCE8").queue();
                        }
                    }

                    if (args[1].equalsIgnoreCase("karma")) {
                        if (HiveBot.karmaSQLHandler.masterOverrideKarma(args[2])) {
                            event.getMessage().addReaction("\uD83D\uDCE8").queue();
                        }
                    }
                }
            } catch (PermissionException e) {
            } catch (IndexOutOfBoundsException e) {
                event.getChannel().sendMessage("Missing parameter").queue();
                System.out.println("Missing parameter");
            }
        }

        // Query DB Size
        if (HiveBot.commands.get(56).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(56))) {
                    event.getChannel().sendMessage("Size: " + HiveBot.karmaSQLHandler.getDBSize() + " users").queue();
                }
            } catch (NullPointerException e) {
            }
        }

    }

    public void onMessageReceived(MessageReceivedEvent event) throws PermissionException {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        boolean source = event.getMessage().isFromGuild();

        //Get karma
        if (HiveBot.commands.get(62).checkCommand(event.getMessage().getContentRaw())) {
            LOGGER.info(HiveBot.commands.get(62).getCommand() + " called by " + event.getAuthor().getAsTag());
            if(source){
                karmaExplanationShort(event.getMessage(),true);
            } else {
                karmaExplanation(event.getMessage(), false);
            }
        }

        if (HiveBot.commands.get(69).checkCommand(event.getMessage().getContentRaw())) {
            LOGGER.info(HiveBot.commands.get(69).getCommand() + " called by " + event.getAuthor().getAsTag());
            getKarmaActiveUsers(event);
        }

    }

    private void karmaExplanation(Message message, boolean source) {
        String karmaExplanation = (String) HiveBot.dataFile.getData("KarmaExplanation");
        karmaExplanation = karmaExplanation.replace("{kPosIcon}", "<:KU:717177145717424180>");
        karmaExplanation = karmaExplanation.replace("{kNegIcon}", "<:KD:717177177849724948> ");

        String finalKarmaExplanation = karmaExplanation;

        // Send to guild channel or not
        if (source) {
            message.getChannel().sendMessage(finalKarmaExplanation).queue();
            return;
        }

        message.getAuthor().openPrivateChannel().queue((channel -> {
            channel.sendMessage(finalKarmaExplanation).queue(
                    success -> {
                        message.addReaction("✅").queue();
                    },
                    failure -> {
                        message.addReaction("⚠").queue();
                        LOGGER.warning(HiveBot.commands.get(2).getCommand() + " failed due to privacy settings.  Called by " + message.getAuthor().getAsTag());
                        message.getChannel().sendMessage(message.getAuthor().getAsMention() + " I am unable to DM you due to your privacy settings. Please update and try again.").queue();
                    });
        }));
    }

    private void karmaExplanationShort(Message message, boolean source) {
        String karmaExplanation = (String) HiveBot.dataFile.getData("KarmaExplanationShort");
        karmaExplanation = karmaExplanation.replace("{kPosIcon}", "<:KU:717177145717424180>");
        karmaExplanation = karmaExplanation.replace("{kNegIcon}", "<:KD:717177177849724948> ");

        String finalKarmaExplanation = karmaExplanation;

        // Send to guild channel or not
        if (source) {
            message.getChannel().sendMessage(finalKarmaExplanation).queue(success -> {
                success.delete().queueAfter(60,TimeUnit.SECONDS);
            });
            return;
        }

        message.getAuthor().openPrivateChannel().queue((channel -> {
            channel.sendMessage(finalKarmaExplanation).queue(
                    success -> {
                        message.addReaction("✅").queue();
                    },
                    failure -> {
                        message.addReaction("⚠").queue();
                        LOGGER.warning(HiveBot.commands.get(2).getCommand() + " failed due to privacy settings.  Called by " + message.getAuthor().getAsTag());
                        message.getChannel().sendMessage(message.getAuthor().getAsMention() + " I am unable to DM you due to your privacy settings. Please update and try again.").queue();
                    });
        }));
    }

    private void getKarmaInfo(Guild guild, Message message, boolean source, String id) {
        KarmaUserInfo karmaUserInfo;

        karmaUserInfo = HiveBot.karmaSQLHandler.userInfo(id);

        EmbedBuilder userInfo = new EmbedBuilder();
        userInfo.addField("Name: ", karmaUserInfo.getName(), true);
        userInfo.addField("UID: ", karmaUserInfo.getId().toString(), true);
        userInfo.addField("Karma: ", String.valueOf(karmaUserInfo.getKarma()), true);
        userInfo.addField("Available Points: ", String.valueOf(karmaUserInfo.getAvailable_points()), true);
        userInfo.addField("Positive Karma Sent: ", String.valueOf(karmaUserInfo.getKsent_pos()), true);
        userInfo.addField("Negative Karma Sent: ", String.valueOf(karmaUserInfo.getKsent_neg()), true);
        userInfo.setColor(Color.ORANGE);
        userInfo.setThumbnail(guild.getMemberById(id).getUser().getEffectiveAvatarUrl());

        // Send to guild channel or not
        if (source) {
            message.getChannel().sendMessage(userInfo.build()).queue();
            return;
        }

        message.getAuthor().openPrivateChannel().queue((channel -> {
            channel.sendMessage(userInfo.build()).queue(
                    success -> {
                        message.addReaction("✅").queue();
                    },
                    failure -> {
                        message.addReaction("⚠").queue();
                        LOGGER.warning(HiveBot.commands.get(2).getCommand() + " failed due to privacy settings.  Called by " + message.getAuthor().getAsTag());
                        message.getChannel().sendMessage(message.getAuthor().getAsMention() + " I am unable to DM you due to your privacy settings. Please update and try again.").queue();
                    });
        }));

        userInfo.clear();
    }

    // Run karma update using mentions
    private void runUpdateKarma(Guild guild, Message message, Member sender, Member receiver, boolean direction) {
        try {
            // User is trying to give them self karma
            if (sender.getId().equalsIgnoreCase(receiver.getId())) {
                message.addReaction("\uD83E\uDD54").queue();
                EmbedBuilder badMessage = new EmbedBuilder();
                badMessage.setDescription(message.getAuthor().getAsMention() + " You can't give karma to yourself!");
                badMessage.setImage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif");
                message.getChannel().sendMessage(badMessage.build()).queue(success -> {
                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                });
                HiveBot.karmaLogger.warning(message.getMember().getUser().getAsTag() + " attempted to send karma to them self");
                return;
            }

            // User is trying to give a BOT Karma
            if (receiver.getUser().isBot()) {
                message.addReaction("\uD83E\uDD54").queue();
                EmbedBuilder badMessage = new EmbedBuilder();
                badMessage.setDescription(message.getAuthor().getAsMention() + " You can't give karma to BoTs!");
                badMessage.setImage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif");
                message.getChannel().sendMessage(badMessage.build()).queue(success -> {
                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                });
                HiveBot.karmaLogger.warning(message.getMember().getUser().getAsTag() + " attempted to send karma to a BOT");
                return;
            }

            // Try to execute request
            int result = HiveBot.karmaSQLHandler.updateKarma(sender.getId(), receiver.getId(), direction);

            // Check result of query
            if (result == 4) {
                // Karma sent successfully
                message.addReaction("\uD83D\uDCE8").queue();
                if (direction) {
                    HiveBot.karmaLogger.info("Sending positive karma from: " + sender.getId() + " to: " + receiver.getId());
                    nickname.parseRank(guild,receiver);
                } else {
                    HiveBot.karmaLogger.info("Sending negative karma from: " + sender.getId() + " to: " + receiver.getId());
                }
            } else if (result == 1) {
                // Could not find user in DB
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " Sorry, I could not find that member in the karma DB").queue();
            } else if (result == 2) {
                // Sender did not have enough points
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " sorry, looks like you don't have any points").queue();
            } else {
                // SQL Error
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " Unknown error encountered.  Try again later").queue();
            }
        } catch (NullPointerException e) {
        }
    }

    //Run karma update using ID
    private void runUpdateKarma(Message message, Member sender, String receiver, Guild guild, boolean direction) {
        try {
            // User is trying to give them self karma
            if (sender.getId().equalsIgnoreCase(receiver)) {
                message.addReaction("\uD83E\uDD54").queue();
                EmbedBuilder badMessage = new EmbedBuilder();
                badMessage.setDescription(message.getAuthor().getAsMention() + " You can't give karma to yourself!");
                badMessage.setImage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif");
                message.getChannel().sendMessage(badMessage.build()).queue(success -> {
                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                });
                HiveBot.karmaLogger.warning(message.getMember().getUser().getAsTag() + " attempted to send karma to them self");
                return;
            }

            // User is trying to give a BOT Karma
            if (guild.getMemberById(receiver).getUser().isBot()) {
                message.addReaction("\uD83E\uDD54").queue();
                EmbedBuilder badMessage = new EmbedBuilder();
                badMessage.setDescription(message.getAuthor().getAsMention() + " You can't give karma to BoTs!");
                badMessage.setImage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif");
                message.getChannel().sendMessage(badMessage.build()).queue(success -> {
                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                });
                HiveBot.karmaLogger.warning(message.getMember().getUser().getAsTag() + " attempted to send karma to a BOT");
                return;
            }

            // Try to execute request
            int result = HiveBot.karmaSQLHandler.updateKarma(sender.getId(), receiver, direction);

            // Check result of query
            if (result == 4) {
                // Karma sent successfully
                message.addReaction("\uD83D\uDCE8").queue();
                if (direction) {
                    HiveBot.karmaLogger.info("Sending positive karma from: " + sender.getId() + " to: " + receiver);
                    nickname.parseRank(guild,receiver);
                } else {
                    HiveBot.karmaLogger.info("Sending negative karma from: " + sender.getId() + " to: " + receiver);
                }
            } else if (result == 1) {
                // Could not find user in DB
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " Sorry, I could not find that member in the karma DB").queue();
            } else if (result == 2) {
                // Sender did not have enough points
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " sorry, looks like you don't have any points").queue();
            } else {
                // SQL Error
                message.getChannel().sendMessage(message.getAuthor().getAsMention() + " Unknown error encountered.  Try again later").queue();
                System.out.println("Error encountered");
            }
            //System.out.println(result);
        } catch (NullPointerException e) {
        }
    }

    private void getKarmaActiveUsers(MessageReceivedEvent event){
        StringBuilder userList = new StringBuilder();
        StringBuilder indexList = new StringBuilder();
        StringBuilder nameList = new StringBuilder();



        EmbedBuilder messageOut = new EmbedBuilder();

        for(Map.Entry<String, Integer> entry: karmaSQLHandler.getActiveUsers().entrySet()){

            //messageOut.addField(entry.getKey(),"Name:" + karmaSQLHandler.getUserTag(entry.getKey()) + "\n" + "Karma Recv. " + entry.getValue(),false);

            userList.append(entry.getKey()).append("\n");
            nameList.append(karmaSQLHandler.getUserTag(entry.getKey())).append("\n");
            indexList.append(entry.getValue()).append("\n");


        }

        //EmbedBuilder messageOut = new EmbedBuilder();
        messageOut.addField("ID",userList.toString(),true);
        messageOut.addField("NAME",nameList.toString(),true);
        messageOut.addField("# of karma recieved",indexList.toString(),true);

        event.getChannel().sendMessage(messageOut.build()).queue();
        messageOut.clear();
    }

}