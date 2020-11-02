package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.ObjectUtils;
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
                        help.setColor(Color.YELLOW);
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
                        LOGGER.warning(event.getMember().getUser().getAsTag() + " tried to call a help command without access.  Command: " + c.getCommand());
                        event.getMessage().addReaction("🚫").queue();
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                        return;
                    }
                }

                if(!commandFound){
                    if(!HiveBot.hallMonitor.languageCheck(event.getMessage().getContentRaw())) {
                        Random random = new Random();

                        //Initialize categories for each type
                        ArrayList<String> lowLevelCommands = new ArrayList<>();

                        //Assign the commands to categories
                        for (Command c : HiveBot.commands) {
                            if (c.getRank() <= 0) {
                                try {
                                    lowLevelCommands.add(c.getCommand());
                                } catch (NullPointerException e) {
                                }
                            }
                        }
                        int rand = random.nextInt(lowLevelCommands.size());

                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + "\nI couldn't find " + args[1] + ", Did you mean " + lowLevelCommands.get(rand) + "?").queue();
                    }
                } else {
                    event.getChannel().sendMessage(help.build()).queue();
                }
                help.clear();
            } else {
                try{
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + "\nYou can use " + HiveBot.prefix + "help *command* to get instructions as well as a description of any command.").queue();
                } catch(NullPointerException e){}
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
