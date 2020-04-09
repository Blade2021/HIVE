package rsystems.commands;

import com.samuelmaddock.strawpollwrapper.DupCheckType;
import com.samuelmaddock.strawpollwrapper.StrawPoll;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import kotlin.random.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

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
        if (args[0].equalsIgnoreCase(HiveBot.prefix + "Poll")) {

            //String URL = null;
            try {
                if (event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)) {

                    // User has administrator rights
                    // GET Help with poll command

                    if((args.length < 2) || (args[1].equalsIgnoreCase("help")) || (args[1].equalsIgnoreCase("?"))){
                        EmbedBuilder info = new EmbedBuilder();
                        info.setTitle("HIVE Poll Help");
                        info.setDescription("Poll information");
                        info.setThumbnail(event.getGuild().getIconUrl());
                        info.addField("Poll x,y,z","Option 1, Option 2, Option 3 (COMMA SEPARATED!)",false);
                        info.addField("GetURL","Grab the URL of current Poll",false);
                        info.addField("SetURL (URL)","Set URL of current Poll",false);
                        info.addField("GetVotes","Display current vote count from poll",false);
                        info.addField("Pick","Grab the votes and pick the winner",false);
                        info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                        info.setColor(Color.CYAN);
                        event.getChannel().sendTyping().queue();
                        event.getChannel().sendMessage(info.build()).queue();
                        info.clear();
                        return;
                    }
                    /*
                    if (args.length < 2) {
                        // Not enough arguments (nothing to check)
                        event.getMessage().addReaction("\uD83D\uDEAB").queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " Not enough arguments supplied").queue();
                    }*/ else {

                        // User has administrator permissions

                        // Set URL for Poll
                        if (args[1].equalsIgnoreCase("seturl")) {
                            URL=event.getMessage().getContentRaw().substring((args[0].length() + args[1].length())+2);
                            return;
                        }

                        //Get votes command
                        if (args[1].equalsIgnoreCase("getvotes")) {
                            //Check if poll is in session
                            if(URL == null){
                                System.out.println("URL is NULL");
                                event.getChannel().sendMessage("There is currently no active poll.").queue();
                                return;
                            } else {
                                System.out.println("URL is not NULL");
                                System.out.println(URL);
                                try {
                                    StrawPoll getVote = new StrawPoll(URL);
                                    ListIterator<String> listItr = getVote.getOptions().listIterator();
                                    int x = 0;
                                    while (listItr.hasNext()) {
                                        try {
                                            //System.out.println(strawPoll.getVotes().toString());
                                            event.getChannel().sendMessage(getVote.getOptions().get(x) + " | " + getVote.getVotes().get(x).toString()).queue();
                                            x++;
                                        } catch (IndexOutOfBoundsException e) {
                                            break;
                                        }
                                    }
                                }
                                catch(NullPointerException e){
                                    e.printStackTrace();
                                }
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

                        if ((args[1].equalsIgnoreCase("pick")) && (URL != null)) {

                            StrawPoll winner = new StrawPoll(URL);
                            try {
                                ListIterator<String> listItr = winner.getOptions().listIterator();
                                int x = 0;
                                int[] voteArray = new int[50];

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
                                for (int i:voteArray) {
                                    if((voteArray[i] > max) && (voteArray[i] > 0)){
                                        max = voteArray[i];
                                    }
                                }
                                // Find out if there are duplicates of the max value
                                if(max > 0) {
                                    int dupCheck = 0;
                                    List<String> suddenOptions = new ArrayList();

                                    for(int f=0;f<voteArray.length;f++){
                                        if(voteArray[f] == max){
                                            dupCheck++;
                                            suddenOptions.add(winner.getOptions().get(f));
                                        }
                                    }

                                    System.out.println(dupCheck);

                                    // Single option won as it should!
                                    if(dupCheck == 1)
                                    {
                                        event.getChannel().sendMessage("We have a winner! " + suddenOptions.get(0) + " wins this round!").queue();
                                    }

                                    // Two way TIE
                                    if (dupCheck == 2) {
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

                                        event.getChannel().sendMessage("Well its a TIE and " + message).queue();

                                    }
                                    // Greater than or equal to a 3 way tie
                                    if (dupCheck >= 3){

                                        event.getChannel().sendMessage("WHAT! Nope, i'm out on this one! YOU figure it out!").queue();
                                    }
                                } else {
                                    event.getMessage().addReaction("\uD83E\uDDD0").queue();
                                    event.getChannel().sendMessage("Uh... well this is awkward, nobody voted! \uD83E\uDD10 ").queue();
                                }


                                //event.getChannel().sendMessage("Winning Option is: " + winningOption + " with " + vote + " votes").queue();
                                URL = null;
                            }
                            catch(NullPointerException e){
                                e.printStackTrace();
                            }
                            return;
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
/*
                        new Thread( new Runnable() {
                            public void run()  {
                                try  { Thread.sleep( 30000 ); }
                                catch (InterruptedException ie)  {}
                                try {

                                    System.out.println(strawPoll.getVotes());
                                    /*
                                    //System.out.println(strawPoll.getPollURL());
                                    ListIterator<String> listItr = strawPoll.getOptions().listIterator();
                                    int x = 0;
                                    while(listItr.hasNext()) {
                                        try {
                                            //System.out.println(strawPoll.getVotes().toString());
                                            event.getChannel().sendMessage(strawPoll.getOptions().get(x) + " | " + strawPoll.getVotes().get(x).toString()).queue();
                                            x++;
                                        } catch(IndexOutOfBoundsException e){
                                            break;
                                        }
                                    }


                                }
                                catch(NullPointerException e){
                                    event.getChannel().sendMessage("Something terrible went wrong!").queue();
                                }
                                return;
                            }
                        } ).start();


                     */

                    }
                } else {
                    // User does not have administrator rights
                    event.getMessage().addReaction("\uD83D\uDEAB").queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to this command.").queue();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                System.out.println("Null found on permissions request");
            }
        }

    }
}
