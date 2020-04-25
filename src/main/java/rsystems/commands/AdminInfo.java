package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.Config;
import rsystems.Handlers.Jackson;
import rsystems.HiveBot;

import java.awt.*;

public class AdminInfo extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase((HiveBot.prefix + "admin"))){
            try{
                if(event.getMessage().getMember().hasPermission(Permission.ADMINISTRATOR)){
                    EmbedBuilder ainfo = new EmbedBuilder();
                    ainfo.setTitle("HIVE Admin Commands");
                    ainfo.setDescription("BoT Prefix: " + HiveBot.prefix+ "\nCurrent User Count: `" + event.getGuild().getMemberCount() + " Users`");
                    ainfo.setThumbnail(event.getGuild().getIconUrl());
                    ainfo.addField("`Clear [int]`","Clears x amount of lines of chat.",false);
                    ainfo.addField("`Status [String]`","Sets the status activity of the BOT",false);
                    ainfo.addField("`Shutdown`","Shuts down the BOT.  Only use if **REQUIRED!**",false);
                    ainfo.addField("`Role [String]`","Grabs current user count for specified role", false);
                    ainfo.addField("`Poll [option 1],[option 2],[option 3]`","Create a StrawPoll using HIVE.  Use `Poll help` for poll menu",false);
                    ainfo.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                    ainfo.setColor(Color.RED);
                    event.getChannel().sendMessage(ainfo.build()).queue();
                    ainfo.clear();
                }
            }
            catch(NullPointerException e){
                System.out.println("Null permission found");
            }
            catch(InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }

        if(args[0].equalsIgnoreCase((HiveBot.prefix + "stats"))) {
            try {
                if((event.getMember().hasPermission(Permission.MANAGE_CHANNEL))) {
                    int textChannelAmt = event.getGuild().getTextChannels().size();
                    int voiceChannelAmt = event.getGuild().getVoiceChannels().size();
                    int memberCount = event.getGuild().getMemberCount();

                    EmbedBuilder ainfo = new EmbedBuilder();
                    ainfo.setTitle(event.getGuild().getName() + " discord server");
                    ainfo.setDescription("Current User Count: " + memberCount + "\n" +
                            "Text Channel Count: " + textChannelAmt + "\n" +
                            "Voice Channel Count: " + voiceChannelAmt);
                    ainfo.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                    ainfo.setColor(Color.RED);
                    event.getChannel().sendMessage(ainfo.build()).queue();
                    ainfo.clear();
                } else {
                    event.getChannel().sendMessage("You do not have access to that command").queue();
                }
            } catch(NullPointerException e){
                System.out.println("Could not find permissions");
            } catch(InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }

        if(args[0].equalsIgnoreCase((HiveBot.prefix + "reload"))) {
            try {
                if((event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
                    Config.reload();
                    event.getMessage().addReaction("✅").queue();
                } else {
                    event.getChannel().sendMessage("You do not have access to that command").queue();
                }
            } catch(NullPointerException e){
                System.out.println("Could not find permissions");
            }
        }

        if(args[0].equalsIgnoreCase((HiveBot.prefix + "load"))) {
            try {
                if ((event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
                    event.getChannel().sendMessage("```json\n" + Jackson.readJFile().toString() + "```").queue();
                } else {
                    event.getMessage().addReaction("🚫").queue();
                }
            }catch(NullPointerException e){
                e.printStackTrace();
            };
        }

        if(args[0].equalsIgnoreCase((HiveBot.prefix + "jread"))) {
            if (args.length < 2) {
                return;
            } else {
                try {
                    if ((event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
                        String output = Jackson.readDataBit(args[1]);
                        if (!output.isBlank()) {
                            event.getChannel().sendMessage("```json\n" + Jackson.readDataBit(args[1]) + "```").queue();
                        }
                    } else {
                        event.getMessage().addReaction("🚫").queue();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        if (args[0].equalsIgnoreCase((HiveBot.prefix + "jset"))) {
            if (args.length < 2) {
                return;
            } else {
                if ((event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
                    try {
                        if(Jackson.writeData(args[1], event.getMessage().getContentRaw().substring(args[0].length() + args[1].length() + 2))){
                            event.getMessage().addReaction("✅").queue();
                        }else{
                            event.getMessage().addReaction("🚫").queue();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }else{
                    event.getMessage().addReaction("🚫").queue();
                }
            }
        }
    }
}
