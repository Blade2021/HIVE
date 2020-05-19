package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.Command;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Help extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Don't accept messages from BOT Accounts [BOT LAW 2]
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        // Helpful notes or not enough arguments
        if ((args[0].equalsIgnoreCase((HiveBot.prefix + HiveBot.commands.get(23).getCommand())))) {
            if (args.length >= 2) {
                Boolean send = false;
                EmbedBuilder help = new EmbedBuilder();
                help.setTitle("Command Help");

                for (Command c : HiveBot.commands) {
                    if ((c.getCommand().equalsIgnoreCase(args[1])) && (RoleCheck.getRank(event, event.getMember().getId()) >= c.getRank())) {
                        help.appendDescription(c.getDescription());
                        help.addField("`" + c.getCommand() + "`", "Syntax: " + c.getSyntax(), false);
                        send = true;
                    } else if ((c.getCommand().equalsIgnoreCase(args[1])) && (RoleCheck.getRank(event, event.getMember().getId()) < c.getRank())) {
                        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                        return;
                    }
                }

                if (send) {
                    event.getChannel().sendMessage(help.build()).queue();
                }
                help.clear();
            } else {
                event.getMessage().addReaction("✅").queue();
                try {
                    //Open a private channel with requester
                    event.getAuthor().openPrivateChannel().queue((channel) ->
                    {
                        EmbedBuilder info = new EmbedBuilder();
                        info.setTitle("HIVE BoT Commands");
                        info.setDescription("BoT Prefix: " + HiveBot.prefix + "\n**All commands ignore case for your convenience.**");
                        info.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());

                        for(Command c:HiveBot.commands){
                            if(c.getRank() <= 0) {
                                info.addField("`" + c.getCommand() + "`", c.getDescription(), false);
                            }
                        }
                        info.setColor(Color.CYAN);
                        channel.sendMessage(info.build()).queue();
                        info.clear();
                        channel.close();
                    });
                } catch(UnsupportedOperationException e) {
                    // Couldn't open private channel
                    event.getMessage().removeReaction("✅").queue();
                    event.getMessage().addReaction("🚫").queue();
                }
            }
        }

        //Commands command
        if(args[0].equalsIgnoreCase((HiveBot.prefix + HiveBot.commands.get(27).getCommand()))){
            try {

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
