package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static rsystems.HiveBot.LOGGER;
import static rsystems.HiveBot.karmaSQLHandler;

public class KarmaInterface extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

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
        if (args[0].equalsIgnoreCase(HiveBot.karmaPrefixPositive)) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(53))) {

                    event.getMessage().getMentionedMembers().forEach(member -> {
                        try {
                            if (member.getId().equalsIgnoreCase(event.getMember().getId())) {
                                event.getMessage().addReaction("\uD83E\uDD54").queue();
                                event.getChannel().sendMessage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif \n\n" + event.getAuthor().getAsMention() + " You can't give karma to yourself!").queue(success -> {
                                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                                });
                                return;
                            } else if (member.getUser().isBot()) {
                                event.getMessage().addReaction("\uD83E\uDD54").queue();
                                event.getChannel().sendMessage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif \n\n" + event.getAuthor().getAsMention() + " You can't give karma to BoTs!").queue(success -> {
                                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                                });
                                return;
                            } else {

                                if (HiveBot.karmaSQLHandler.updateKarma(event.getMember().getId(), member.getId(), true)) {
                                    event.getMessage().addReaction("\uD83D\uDCE8").queue();
                                } else {
                                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " sorry, looks like you don't have any points").queue();
                                }
                            }

                        } catch (NullPointerException e) {
                            System.out.println("Found null");
                        }
                    });

                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }

        //sendNegativeKarma
        if (args[0].equalsIgnoreCase(HiveBot.karmaPrefixNegative)) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(57))) {

                    event.getMessage().getMentionedMembers().forEach(member -> {
                        try {
                            if (member.getId().equalsIgnoreCase(event.getMember().getId())) {
                                event.getMessage().addReaction("\uD83E\uDD54").queue();
                                event.getChannel().sendMessage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif \n\n" + event.getAuthor().getAsMention() + " You can't take karma from yourself!").queue(success -> {
                                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                                });
                                return;
                            } else if (member.getUser().isBot()) {
                                event.getMessage().addReaction("\uD83E\uDD54").queue();
                                event.getChannel().sendMessage("https://cdn.discordapp.com/attachments/602040131079372825/717792573174841434/image0.gif \n\n" + event.getAuthor().getAsMention() + " You can't take karma from BoTs!").queue(success -> {
                                    success.delete().queueAfter(30, TimeUnit.SECONDS);
                                });
                                return;
                            } else {

                                if (HiveBot.karmaSQLHandler.updateKarma(event.getMember().getId(), member.getId(), false)) {
                                    event.getMessage().addReaction("\uD83D\uDCE8").queue();
                                } else {
                                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " sorry, looks like you don't have any points").queue();
                                }
                            }
                        } catch (NullPointerException e) {
                            System.out.println("Found null");
                        }
                    });

                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
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

        //Get Top 5
        if (HiveBot.commands.get(61).checkCommand(event.getMessage().getContentRaw())) {
            try {
                if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(60))) {
                    try {
                        event.getMessage().delete().queue();
                    } catch (PermissionException | NullPointerException e) {
                    }

                    StringBuilder nameString = new StringBuilder();
                    StringBuilder rankString = new StringBuilder();

                    for (Map.Entry<String, Integer> entry : karmaSQLHandler.getTopFive().entrySet()) {
                        nameString.append(entry.getKey()).append("\n");
                        rankString.append(entry.getValue()).append("\n");
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle("Karma: TOP 5");
                    embedBuilder.addField("Name:", nameString.toString(), true);
                    embedBuilder.addField("Rank:", rankString.toString(), true);

                    event.getChannel().sendMessage(embedBuilder.build()).queue();
                    embedBuilder.clear();
                }
            } catch (NullPointerException e) {
                System.out.println("Found null");
            }
        }


        //Master Override
        if(HiveBot.commands.get(58).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(58))){
                    if(args[1].equalsIgnoreCase("points")) {
                        if (HiveBot.karmaSQLHandler.masterOverridePoints(args[2])) {
                            event.getMessage().addReaction("\uD83D\uDCE8").queue();
                        }
                    }

                    if(args[1].equalsIgnoreCase("karma")) {
                        if (HiveBot.karmaSQLHandler.masterOverrideKarma(args[2])) {
                            event.getMessage().addReaction("\uD83D\uDCE8").queue();
                        }
                    }
                }
            }catch(PermissionException e){
            }catch(IndexOutOfBoundsException e){
                event.getChannel().sendMessage("Missing parameter").queue();
                System.out.println("Missing parameter");
            }
        }

        // Query DB Size
        if(HiveBot.commands.get(56).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(56))){
                    event.getChannel().sendMessage("Size: " + HiveBot.karmaSQLHandler.getDBSize()).queue();
                }
            } catch (NullPointerException e) {
            }
        }

    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) throws PermissionException {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        if (HiveBot.commands.get(62).checkCommand(event.getMessage().getContentRaw())) {
            LOGGER.info(HiveBot.commands.get(62).getCommand() + " called by " + event.getAuthor().getAsTag());
            karmaExplanation(event.getMessage(), false);
        }
    }

    private void karmaExplanation(Message message, boolean source) {
        String karmaExplanation = (String) HiveBot.dataFile.getData("KarmaExplanation");
        karmaExplanation = karmaExplanation.replace("{kPosIcon}", "<:KU:717177145717424180>");
        karmaExplanation = karmaExplanation.replace("{kNegIcon}", "<:KD:717177177849724948> ");

        String finalKarmaExplanation = karmaExplanation;

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

}