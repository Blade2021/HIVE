package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.Command;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static rsystems.HiveBot.LOGGER;

public class Help extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        // Help Command
        if(HiveBot.commands.get(23).checkCommand(event.getMessage().getContentRaw())){
            LOGGER.info(HiveBot.commands.get(23).getCommand() + " called by " + event.getAuthor().getAsTag());
            if (args.length >= 2) {
                Boolean commandFound = false;
                EmbedBuilder help = new EmbedBuilder();
                help.setTitle("Command Help");

                for (Command c : HiveBot.commands) {
                    if((c.helpCheck(args[1])) && (RoleCheck.getRank(event, event.getMember().getId()) >= c.getRank())){
                        help.appendDescription(c.getDescription());
                        help.addField("`" + c.getCommand() + "`", "Syntax: " + c.getSyntax(), false);
                        try {
                            if(c.getAlias().size() > 0) {

                                StringBuilder aliasString = new StringBuilder();
                                for (String s : c.getAlias()) {
                                    aliasString.append(s).append(",");
                                }

                                help.addField("Alias", aliasString.toString(), false);
                            }
                        } catch (NullPointerException e){
                        }
                        commandFound = true;
                    } else if ((c.getCommand().equalsIgnoreCase(args[1])) && (RoleCheck.getRank(event, event.getMember().getId()) < c.getRank())) {
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                        return;
                    }
                }

                if(!commandFound){
                    if(!HiveBot.hallMonitor.languageCheck(event.getMessage().getContentRaw())) {
                        Random random = new Random();
                        int rand = random.nextInt(HiveBot.commands.size());
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + "I couldn't find " + args[1] + ", Did you mean " + HiveBot.commands.get(rand).getCommand() + "?").queue();
                    }
                } else {
                    event.getChannel().sendMessage(help.build()).queue();
                }
                help.clear();
            } else {
                try {
                    //Open a private channel with requester
                    event.getAuthor().openPrivateChannel().queue((channel) ->
                    {
                        EmbedBuilder info = new EmbedBuilder();
                        info.setTitle("HIVE BoT Information V. " + HiveBot.version);
                        info.setDescription("BoT Prefix: " + HiveBot.prefix + "\n**All commands ignore case for your convenience.**\nNeed help with a command?  Just type " + HiveBot.prefix + "help [command]\n" + HiveBot.prefix + "help Who");
                        info.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());

                        //Initialize categories for each type
                        ArrayList<String> utilityCommands = new ArrayList<>();
                        ArrayList<String> infoCommands = new ArrayList<>();
                        ArrayList<String> funCommands = new ArrayList<>();

                        //Assign the commands to categories
                        for(Command c:HiveBot.commands){
                            if(c.getRank() <= 0) {
                                try {
                                    if (c.getCommandType().equalsIgnoreCase("utility")) {
                                        utilityCommands.add(c.getCommand());
                                    }
                                    if (c.getCommandType().equalsIgnoreCase("information")) {
                                        infoCommands.add(c.getCommand());
                                    }
                                    if (c.getCommandType().equalsIgnoreCase("fun")) {
                                        funCommands.add(c.getCommand());
                                    }
                                }catch(NullPointerException e){
                                    System.out.println("Found null for command: " + c.getCommand());
                                }
                            }
                        }

                        StringBuilder utilityString = new StringBuilder();
                        for(String s:utilityCommands){
                            utilityString.append(s).append("\n");
                        }

                        StringBuilder infoString = new StringBuilder();
                        for(String s:infoCommands){
                            infoString.append(s).append("\n");
                        }

                        StringBuilder funString = new StringBuilder();
                        for(String s:funCommands){
                            funString.append(s).append("\n");
                        }

                        info.addField("Utility", utilityString.toString(),true);
                        info.addField("Information",infoString.toString(),true);
                        info.addField("Fun",funString.toString(),true);

                        info.setColor(Color.CYAN);
                        channel.sendMessage(info.build()).queue(
                                success -> {
                                    event.getMessage().addReaction("✅").queue();
                                },
                                failure -> {
                                    event.getMessage().addReaction("⚠").queue();
                                    LOGGER.warning(HiveBot.commands.get(2).getCommand() + " failed due to privacy settings.  Called by " + event.getAuthor().getAsTag());
                                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I am unable to DM you due to your privacy settings. Please update and try again.").queue();
                                });
                        info.clear();
                        channel.close();
                    });
                } catch(UnsupportedOperationException e) {
                    // Couldn't open private channel
                    event.getMessage().addReaction("🚫").queue();
                } catch(ErrorResponseException e){
                    LOGGER.warning(HiveBot.commands.get(2).getCommand() + " failed.  Called by " + event.getAuthor().getAsTag());
                    event.getMessage().addReaction("⚠").queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I am unable to DM you due to privacy settings. Please update and try again.").queue();
                }
            }
        }

        //Commands command
        if(HiveBot.commands.get(27).checkCommand(event.getMessage().getContentRaw())){
            LOGGER.info(HiveBot.commands.get(27).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {
                try {
                    //event.getMessage().delete().reason("Deleting trigger").queue();
                }catch(PermissionException e){
                    LOGGER.severe("Failed to delete trigger message in channel: " + event.getChannel().getName());
                }

                ArrayList<String> commands = new ArrayList<>();
                int rank = RoleCheck.getRank(event,event.getMember().getId());

                // Only allow admins to check roles of other users
                if((args.length > 1) && (RoleCheck.getRank(event,event.getMember().getId()) >= 3)){
                    try {
                        List<Member> mentions = event.getMessage().getMentionedMembers();
                        for (Member m : mentions) {
                            rank = RoleCheck.getRank(event, m.getId());
                            for (Command c : HiveBot.commands) {
                                if (rank >= c.getRank()) {
                                    commands.add("`" + c.getCommand() + "`");
                                }
                            }
                            event.getChannel().sendMessage(m.getAsMention() + " You have access to the following commands:\n\n" + commands.toString()).queue();
                        }
                        return;
                    } catch (NullPointerException e){
                    }
                } else if((args.length > 1) && (RoleCheck.getRank(event,event.getMember().getId()) < 3)){
                    event.getMessage().addReaction("🚫").queue();
                }

                // No mentions found on channel
                for (Command c : HiveBot.commands) {
                    if(rank >= c.getRank()) {
                        commands.add("`" + c.getCommand() + "`");
                    }
                }
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You have access to the following commands:\n\n" + commands.toString()).queue();

            } catch(NullPointerException e){}
        }

    }
}
