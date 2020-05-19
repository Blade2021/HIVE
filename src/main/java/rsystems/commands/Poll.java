package rsystems.commands;

import com.samuelmaddock.strawpollwrapper.DupCheckType;
import com.samuelmaddock.strawpollwrapper.StrawPoll;
import kotlin.random.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class Poll extends ListenerAdapter {

    static String URL = null;

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if (args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(10).getCommand())) {
            try {
                if(RoleCheck.getRank(event,event.getMember().getId()) >= HiveBot.commands.get(10).getRank()){

                    // User has administrator rights
                    // GET Help with poll command
                    if((args.length < 2) || (args[1].equalsIgnoreCase("help")) || (args[1].equalsIgnoreCase("?"))){
                        try {
                            EmbedBuilder info = new EmbedBuilder();
                            info.setTitle("HIVE Poll Help");
                            info.setDescription("Poll information");
                            info.setThumbnail(event.getGuild().getIconUrl());
                            info.addField("`Poll [option 1],[option 2],[option 3]`", "Start a strawpoll using HVIE (COMMA SEPARATED!)", false);
                            info.addField("`Poll GetURL`", "Grab the URL of current Poll", false);
                            info.addField("`Poll SetURL [URL]`", "Set URL of current Poll", false);
                            info.addField("`Poll GetVotes`", "Display current vote count from poll", false);
                            info.addField("`Poll Pick`", "Grab the votes and pick the winner", false);
                            info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                            info.setColor(Color.CYAN);
                            event.getChannel().sendTyping().queue();
                            event.getChannel().sendMessage(info.build()).queue();
                            info.clear();
                        } catch (PermissionException e){
                            event.getChannel().sendMessage("Missing Permissions: " + e.getPermission().getName()).queue();
                        }
                        return;

                    } else {

                        // User has administrator permissions

                        // Set URL for Poll
                        if (args[1].equalsIgnoreCase("seturl")) {
                            setURL(event.getMessage().getContentRaw().substring((args[0].length() + args[1].length())+2));
                            event.getMessage().addReaction("✅").queue();
                            return;
                        }

                        //Get votes command
                        if (args[1].equalsIgnoreCase("getvotes")) {
                            //Check if poll is in session
                            if(URL == null){
                                event.getChannel().sendMessage("There is currently no active poll.").queue();
                                return;
                            } else {
                                event.getChannel().sendMessage(getVotes().build()).queue();
                            }
                            return;
                        }

                        // Poll - Get URL for current Poll in session
                        if (args[1].equalsIgnoreCase("geturl")) {
                            if(URL == null){
                                event.getChannel().sendMessage("There is currently no active poll.").queue();
                            } else {
                                event.getChannel().sendMessage(URL).queue();
                            }
                            return;
                        }

                        if (args[1].equalsIgnoreCase("pick")){

                            if (URL != null) {
                                String message = pickWinner();
                                if (message != null) {
                                    event.getChannel().sendMessage(message).queue();
                                }

                                URL = null;
                                return;
                            } else {
                                // POLL URL IS NULL (No active polls)
                                event.getMessage().addReaction("🚫").queue();
                                event.getChannel().sendMessage("There are no active polls right now").queue();
                            }
                        }
                        // SET NEW POLL OPTIONS

                        String optionsraw = event.getMessage().getContentRaw().substring(args[0].length());
                        List<String> options = Arrays.asList(optionsraw.trim().split(",", 10));

                        if(options.size() < 2){
                            event.getChannel().sendMessage("Not enough options were supplied.  Try again.").queue();
                            return;
                        }

                        StrawPoll strawPoll = new StrawPoll("HIVE BoT Poll",options).setDupCheck(DupCheckType.NORMAL);
                        strawPoll.create();

                        event.getChannel().sendMessage("GO CAST YOUR VOTES! " + strawPoll.getPollURL()).queue();
                        URL=strawPoll.getPollURL(); // Store poll url into data

                    }
                } else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("Null found on permissions request");
            }
        }

    }

    private void setURL(String URL){
        Poll.URL = URL;
    }

    private EmbedBuilder getVotes(){
        EmbedBuilder voteEmbed = new EmbedBuilder();
        voteEmbed.setTitle("Current Poll Votes",URL);
        try {
            StrawPoll getVote = new StrawPoll(URL);
            ListIterator<String> listItr = getVote.getOptions().listIterator();
            int x = 0;
            while (listItr.hasNext() && (x<12)) {
                try {
                    // Build embed with builder
                    voteEmbed.addField(getVote.getOptions().get(x),getVote.getVotes().get(x).toString(),false);
                    x++;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        return voteEmbed;
    }

    private String pickWinner(){
        StrawPoll winner = new StrawPoll(URL);

        try {
            //Initiate list of options
            ListIterator<String> listItr = winner.getOptions().listIterator();

            int x = 0;
            int[] voteArray = new int[winner.getOptions().size()];

            while (listItr.hasNext()) {
                try {
                    voteArray[x] = Integer.parseInt(winner.getVotes().get(x).toString());
                    x++;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }

            // Find the max value in the array
            int max = 0;
            for (int vote:voteArray) {
                if((vote > max) && (vote > 0)){
                    max = vote;
                }
            }

            // Find out if there are duplicates of the max value
            if(max > 0) {
                int duplicates = 0;
                List<String> suddenOptions = new ArrayList();

                for(int voteIndex=0;voteIndex < voteArray.length; voteIndex++){
                    if(voteArray[voteIndex] == max){
                        duplicates++;
                        suddenOptions.add(winner.getOptions().get(voteIndex));
                    }
                }

                // Single option won as it should!
                if(duplicates == 1)
                {
                    return("We have a winner! " + suddenOptions.get(0) + " wins this round!");
                }

                // Two way TIE
                if (duplicates == 2) {
                    int rand = Random.Default.nextInt(4);
                    String message = "";
                    switch(rand){
                        case 0:
                            message = "OPTIONA AND OPTIONB ARE GOING HEAD TO HEAD!";
                            break;
                        case 1:
                            message = "OPTIONA is taunting OPTIONB for a rematch!";
                            break;
                        case 2:
                            message = "OPTIONA said OPTIONB smells funny.";
                            break;
                        case 3:
                            message = "OPTIONA is getting taunted by OPTIONB";
                            break;
                        case 4:
                            message = "OPTIONA gave OPTIONB a disgust look!";
                            break;
                    }
                    message = message.replaceFirst("OPTIONA",suddenOptions.get(0));
                    message = message.replaceFirst("OPTIONB",suddenOptions.get(1));

                   return("Well its a TIE and " + message);

                }
                // Greater than or equal to a 3 way tie
                if (duplicates >= 3){

                    return("WHAT! Nope, i'm out on this one! YOU figure it out!");
                }
            } else {
                // Nobody voted
                return null;
            }
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return null;
    }

}
