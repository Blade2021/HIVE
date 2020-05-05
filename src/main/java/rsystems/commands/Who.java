package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Who extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(((args[0].equalsIgnoreCase((HiveBot.prefix + "who")) && args.length < 2) || (args[0].equalsIgnoreCase((HiveBot.prefix + "hive"))))) {
            event.getMessage().addReaction("\uD83D\uDC1D ").queue(); // Bee Emoji
            event.getMessage().addReaction("\uD83D\uDC4B ").queue(); // Waving hand emoji
            try {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I am HIVE! A buzzy little bot that is here to help!\nWanna see a list of commands just type: `" + HiveBot.prefix + "help`  \n\nIf you run into any issues please contact my creator: "
                    + event.getGuild().getMemberById("313832264792539142").getAsMention() + "\uD83E\uDDD9\u200D️ ").queue();
            } catch(NullPointerException e){
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I am HIVE! A buzzy little bot that is here to help!\nWanna see a list of commands just type: `" + HiveBot.prefix + "help`  \n\nIf you run into any issues please contact my creator: Blade2021#8727" + "\uD83E\uDDD9\u200D♂️ ").queue();
            }
        }

        if((args.length > 1) && (args[0].equalsIgnoreCase(HiveBot.prefix + "who"))){
            try{
                event.getMessage().delete().queue();
                if(RoleCheck.getRank(event,Long.toString(event.getMember().getUser().getIdLong())) >= 1){
                    List<Member> mentions = event.getMessage().getMentionedMembers();
                    EmbedBuilder info = new EmbedBuilder();
                    for(Member m: mentions){
                        info.setTitle("Who command");
                        info.setDescription("User: " + m.getAsMention());
                        ArrayList<String> userroles = new ArrayList<>();
                        for(Role r:m.getRoles()){
                            userroles.add(r.getName());
                        }
                        info.addField("Roles",userroles.toString(),false);
                        info.addField("UserID",m.getId(),false);
                        info.addField("Join Date",m.getTimeJoined().format(DateTimeFormatter.ISO_LOCAL_DATE),false);
                        info.setColor(Color.CYAN);
                        info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                        event.getChannel().sendMessage(info.build()).queue();
                        info.clear();
                    }
                }
            }
            catch(NullPointerException ignored){
            }
        }

    }
}
