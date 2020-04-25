package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;

public class Code extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if((args[0].equalsIgnoreCase((HiveBot.prefix + "code")))){
            try {
                EmbedBuilder info = new EmbedBuilder();
                info.setTitle("Formatting code in Discord:");
                info.setDescription("When pasting code to discord, wrap your code in \\`\\`\\` characters on both sides of the code block.\n");
                info.appendDescription("\n`Before:`\n{\"greeting\":\"Hello World\"}\n\n`After:` ```json\n{\"greeting\":\"Hello World\"}```");
                info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                info.setColor(Color.CYAN);
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage(info.build()).queue();
                info.clear();
            } catch (NullPointerException e){
                System.out.println("User left after trigger");
            }
            catch (InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }
    }

}
