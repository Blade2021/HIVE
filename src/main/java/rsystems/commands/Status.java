package rsystems.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.util.Objects;


public class Status extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //Escape if message came from a bot account
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args[0].equalsIgnoreCase((HiveBot.prefix + "status"))) {
            try {
                if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    event.getMessage().addReaction("\uD83D\uDEAB").queue();
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have permission for that.").queue();
                    return;
                }

                if (args.length >= 2) {

                    String statusMessage = event.getMessage().getContentRaw().substring(args[0].length() + 1);
                    event.getMessage().addReaction("✅").queue();
                    event.getGuild().getJDA().getPresence().setActivity(Activity.playing(statusMessage));
                    System.out.println("CS| " + statusMessage + " | set by: " + event.getAuthor().getName());

                } else {
                    event.getGuild().getJDA().getPresence().setActivity(Activity.playing("Buzzing around"));
                }

            } catch (NullPointerException e) {
                System.out.println("Null permission detected.");
            }

        }
    }
}