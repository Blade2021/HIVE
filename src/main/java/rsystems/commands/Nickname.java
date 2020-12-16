package rsystems.commands;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static rsystems.HiveBot.*;

public class Nickname extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        //Change nickname for message author
        if (HiveBot.commands.get(81).checkCommand(event.getMessage().getContentRaw())) {
            LOGGER.info(HiveBot.commands.get(81).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {

                String currentName = event.getMember().getEffectiveName();

                Collection<Emoji> collection = new ArrayList<Emoji>();

                collection.add(EmojiManager.getByUnicode("🎄"));

                if(currentName.contains("🎄")){
                    currentName = EmojiParser.removeEmojis(currentName,collection);

                    System.out.println(currentName);
                    event.getMember().modifyNickname(currentName).queue();
                } else {
                    event.getMember().modifyNickname(currentName + "🎄").queue();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        //Change nickname for message author
        if (HiveBot.commands.get(66).checkCommand(event.getMessage().getContentRaw())) {
            LOGGER.info(HiveBot.commands.get(66).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {
                boolean languagePass = true;
                for (String s : args) {
                    if (HiveBot.hallMonitor.languageCheck(s)) {
                        languagePass = false;
                    }
                }

                if (languagePass) {

                    if ((event.getMessage().getEmotes().size() > 0) || (EmojiParser.extractEmojis(event.getMessage().getContentRaw()).size() > 0)) {
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " No emotes or emoji's allowed").queue();
                        return;
                    }

                    //Reset their nickname
                    if ((args.length > 1) && ((args[1].equalsIgnoreCase("-clear")) || (args[1].equalsIgnoreCase("-reset")))) {
                        if (handleNickname(event.getGuild(), event.getMember().getId(),"")) {
                            event.getMessage().addReaction("✅").queue();
                        }
                    } else {

                        if (handleNickname(event.getGuild(), event.getMember().getId(), event.getMessage().getContentRaw().substring(args[0].length() + 1))) {
                            event.getMessage().addReaction("✅").queue();
                        }
                    }

                }

            } catch (IndexOutOfBoundsException e) {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You did not provide enough arguments").queue();
            } catch (HierarchyException e) {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Sorry, I cannot edit you").queue();
            }

        }

        //Opt in/Out of Karma emoji
        if (HiveBot.commands.get(67).checkCommand(event.getMessage().getContentRaw())) {
            LOGGER.info(HiveBot.commands.get(67).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {
                // Set a boolean to check if the write status passed
                boolean writeStatus = false;

                //Does the message contain an argument?
                if (args.length < 2) {
                    // The message does not contain an argument (Trigger a flip of current value)

                    // If current OPTOUT status is set to true (1)
                    if (HiveBot.karmaSQLHandler.getInt("OPTOUT", event.getMember().getId()) >= 1) {
                        //Attempt to set the OPTOUT value to false (0)
                        if (HiveBot.karmaSQLHandler.setInt(event.getMember().getId(), "OPTOUT", 0)) {
                            //Send a message back to the channel confirming, good write to DB
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I have removed the OPTOUT status from you").queue();
                            //Trigger the parse rank function to get current rank and add icon to nickname
                            parseRank(event.getGuild(), event.getMember());
                            //Set the write status variable to trigger a complete finish of method
                            writeStatus = true;
                        }
                        // If current OPTOUT status is set to false (0)
                    } else {
                        //Attempt to set the OPTOUT value to true (1)
                        if (HiveBot.karmaSQLHandler.setInt(event.getMember().getId(), "OPTOUT", 1)) {
                            //Send a message back to the channel confirming, good write to DB
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I have added the OPTOUT status to you").queue();
                            //Trigger the cleanseNickname method to remove the emoji from the user's nickname
                            cleanseNickname(event.getGuild(), event.getMember());
                            //Set the write status variable to trigger a complete finish of method
                            writeStatus = true;
                        }
                    }
                    // User included an argument in call
                } else {
                    // Does argument equal true
                    if (args[1].equalsIgnoreCase("true")) {
                        //Attempt to set the OPTOUT value to true (1)
                        if (HiveBot.karmaSQLHandler.setInt(event.getMember().getId(), "OPTOUT", 1)) {
                            //Send a message back to the channel confirming, good write to DB
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I have added the OPTOUT status to you").queue();
                            //Trigger the cleanseNickname method to remove the emoji from the user's nickname
                            cleanseNickname(event.getGuild(), event.getMember());
                            //Set the write status variable to trigger a complete finish of method
                            writeStatus = true;
                        }
                    } else {
                        //Attempt to set the OPTOUT value to false (0)
                        if (HiveBot.karmaSQLHandler.setInt(event.getMember().getId(), "OPTOUT", 0)) {
                            //Send a message back to the channel confirming, good write to DB
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I have removed the OPTOUT status from you").queue();
                            parseRank(event.getGuild(), event.getMember());
                            //Set the write status variable to trigger a complete finish of method
                            writeStatus = true;
                        }
                    }
                }

                //Did method complete all functions?
                if (writeStatus) {
                    event.getMessage().addReaction("✅").queue();
                }

            } catch (NullPointerException e) {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You did not provide enough arguments").queue();
            }
        }

        if (HiveBot.commands.get(68).checkCommand(event.getMessage().getContentRaw())) {
            if (RoleCheck.checkRank(event.getMessage(), event.getMember(), HiveBot.commands.get(68))) {
                checkActive(event.getGuild());
            }
        }

    }

    /*
    USING ID
    - Adds a row to the karmaTracking table for the date and ID provided
    - Sets KTYPE in KARMA table to 4 for removing emoji from nickname at a later date
     */
    public void parseRank(Guild guild, String id) {
        int rank = HiveBot.karmaSQLHandler.checkKarmaRanking(id); // Get count of karma received
        int suffixRank = 0;
        if (rank > 5) {
            if (HiveBot.karmaSQLHandler.setType(id, 4)) {
                suffixRank = 4;
            } else {
                System.out.println("NICKNAME ESCAPED. User already has type 4");
                //return;
            }
        } else {
            rank = getType(id);
            suffixRank = rank;
        }
        try {
            int optOut = HiveBot.karmaSQLHandler.getInt("OPTOUT", id);
            if (optOut >= 1) {
                return;
            } else {
                handleNickname(guild, id, suffixRank);
            }
        } catch (NullPointerException | PermissionException e) {
            LOGGER.severe("Could not set nickname for user: " + id);
        }

    }

    /*
    USING MEMBER
    - Adds a row to the karmaTracking table for the date and ID provided
    - Sets KTYPE in KARMA table to 4 for removing emoji from nickname at a later date
     */
    public void parseRank(Guild guild, Member member) {
        int rank = HiveBot.karmaSQLHandler.checkKarmaRanking(member.getId());
        int suffixRank = 0;

        if (rank > 5) {
            System.out.println("Setting type to 4");
            if (HiveBot.karmaSQLHandler.setType(member.getId(), 4)) {
                suffixRank = 4;
                try {
                    //todo BUG FIX
                    //guild.getTextChannelById(botSpamChannel).sendMessage(member.getAsMention() + " Congrats, you have been set as an active contributor!").queue();
                } catch (NullPointerException e) {
                }
            } else {
                System.out.println("NICKNAME ESCAPED. User already has type 4");
                return;
            }
        } else {
            rank = getType(member.getId());
            suffixRank = rank;
        }

        try {
            int optOut = HiveBot.karmaSQLHandler.getInt("OPTOUT", member.getId());
            if (optOut >= 1) {
                return;
            } else {
                handleNickname(guild, member.getId(), suffixRank);
            }
        } catch (NullPointerException | PermissionException e) {
            LOGGER.severe("Could not set nickname for user: " + member.getId());
        }
    }


    /*
    - Checks the table of actively set members
    - If a user has fallen below the limit, set appropriate type
     */
    private void checkActive(Guild guild) {
        ArrayList<String> activeMembers = new ArrayList<>();
        activeMembers.addAll(HiveBot.karmaSQLHandler.getActive());
        for (String id : activeMembers) {
            if (HiveBot.karmaSQLHandler.checkKarmaRanking(id) < 5) {
                int rank = getType(id);
                try {
                    int optOut = HiveBot.karmaSQLHandler.getInt("OPTOUT", id);
                    if (optOut >= 1) {
                        return;
                    } else {
                        handleNickname(guild, id, rank);
                    }
                } catch (NullPointerException | PermissionException e) {
                    LOGGER.severe("Could not set nickname for user: " + id);
                }
            }
        }
    }

    private int getType(String id) {
        int karmaRank = HiveBot.karmaSQLHandler.getKarma(id);
        System.out.println(karmaRank);
        if (karmaRank >= 60) {
            HiveBot.karmaSQLHandler.setType(id, 3);
            System.out.println("Setting Type to 3");
            return 3;
        } else if (karmaRank >= 40) {
            HiveBot.karmaSQLHandler.setType(id, 2);
            System.out.println("Setting Type to 2");
            return 2;
        } else if (karmaRank >= 20) {
            HiveBot.karmaSQLHandler.setType(id, 1);
            System.out.println("Setting Type to 1");
            return 1;
        } else {
            HiveBot.karmaSQLHandler.setType(id, 0);
            System.out.println("Setting Type to 0");
            return 0;
        }
    }

    private boolean handleNickname(Guild guild, String id, int karmaType) {
        try {
            //int karmaType = HiveBot.karmaSQLHandler.getInt("KTYPE",id);

            Member member = guild.getMemberById(id);
            String currentNickname = member.getEffectiveName();

            Collection<Emoji> collection = new ArrayList<Emoji>();
            collection.add(EmojiManager.getForAlias("🎄"));
            String nicknameWithoutEmoji = EmojiParser.removeAllEmojisExcept(currentNickname,collection);

            String suffix = "";
            if (karmaType >= 4) {
                suffix = "\uD83C\uDF1F"; //Active contributor
            } else if (karmaType >= 3) {
                suffix = "\uD83D\uDD36"; // Karma >= 50
            } else if (karmaType >= 2) {
                suffix = "\uD83D\uDCA0"; // Karma >= 30
            } else if (karmaType >= 1) {
                suffix = "\uD83D\uDD39"; // Karma >= 20
            }

            member.modifyNickname(nicknameWithoutEmoji + suffix).queue();
        } catch (HierarchyException e) {
            return false;
        }

        return true;
    }

    private boolean handleNickname(Guild guild, String id, String name) {
        try {
            int karmaType = getType(id);
            System.out.println(karmaType);

            Member member = guild.getMemberById(id);
            String currentNickname = name;
            Collection<Emoji> collection = new ArrayList<Emoji>();
            collection.add(EmojiManager.getForAlias("🎄"));
            String nicknameWithoutEmoji = EmojiParser.removeAllEmojisExcept(currentNickname,collection);

            String suffix = "";
            if (karmaType >= 4) {
                suffix = "\uD83C\uDF1F"; //Active contributor
            } else if (karmaType >= 3) {
                suffix = "\uD83D\uDD36"; // Karma >= 50
            } else if (karmaType >= 2) {
                suffix = "\uD83D\uDCA0"; // Karma >= 30
            } else if (karmaType >= 1) {
                suffix = "\uD83D\uDD39"; // Karma >= 20
            }

            member.modifyNickname(nicknameWithoutEmoji + suffix).queue();
        } catch (HierarchyException e) {
            return false;
        }

        return true;
    }

    private void cleanseNickname(Guild guild, Member member) {
        String currentNickname = member.getEffectiveName();
        String nicknameWithoutEmoji = EmojiParser.removeAllEmojis(currentNickname);

        try {
            guild.modifyNickname(member, nicknameWithoutEmoji).queue();
        } catch (HierarchyException e) {
            System.out.println("Can't set nickname for user: " + member.getId());
        }
    }
}
