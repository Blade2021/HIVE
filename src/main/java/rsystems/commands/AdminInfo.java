package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import rsystems.Config;
import rsystems.handlers.DataFile;
import rsystems.handlers.Jackson;
import rsystems.HiveBot;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static rsystems.events.DocStream.sendMarkers;

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
                    event.getAuthor().openPrivateChannel().queue((channel) ->
                    {
                        EmbedBuilder ainfo = new EmbedBuilder();
                        ainfo.setTitle("HIVE Admin Commands");
                        ainfo.setDescription("BoT Prefix: " + HiveBot.prefix + "\nCurrent User Count: `" + event.getGuild().getMemberCount() + " Users`");
                        ainfo.setThumbnail(event.getGuild().getIconUrl());
                        ainfo.addField("`Clear [int]`", "Clears x amount of lines of chat.", false);
                        ainfo.addField("`Status [String]`", "Sets the status activity of the BOT", false);
                        ainfo.addField("`Shutdown`", "Shuts down the BOT.  Only use if **REQUIRED!**", false);
                        ainfo.addField("`Role [String]`", "Grabs current user count for specified role", false);
                        ainfo.addField("`Poll [option 1],[option 2],[option 3]`", "Create a StrawPoll using HIVE.  Use `Poll help` for poll menu", false);
                        ainfo.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                        ainfo.setColor(Color.RED);
                        channel.sendMessage(ainfo.build()).queue();
                        ainfo.clear();
                        channel.close();
                    });
                    event.getMessage().addReaction("✅").queue();
                } else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                    event.getMessage().addReaction("🚫").queue();
                }
            }
            catch(NullPointerException e){
                System.out.println("Null permission found");
            }
            catch(InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
            catch(UnsupportedOperationException e){
                System.out.println(event.getAuthor().getName() + " tried to call admin menu but invalid operation found.");
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

        if (args[0].equalsIgnoreCase((HiveBot.prefix + "getData"))) {
            if ((event.getMember().hasPermission(Permission.ADMINISTRATOR))) {
                EmbedBuilder ainfo = new EmbedBuilder();
                ainfo.setTitle("HIVE Data File");
                try {
                    if(args.length >= 2){
                        Object obj = HiveBot.dataFile.getData(args[1]);
                        ainfo.setTitle("HIVE Data File | " + args[1]);
                        ainfo.appendDescription(obj.toString());
                    } else {
                        DataFile dataFile = HiveBot.dataFile;
                        JSONObject obj = dataFile.getDatafileData();
                        obj.keySet().forEach(keyStr ->
                        {
                            if(keyStr.toString().equalsIgnoreCase("BadWords")){
                                return;
                            }
                            if(keyStr.toString().equalsIgnoreCase("WelcomeMessage")){
                                return;
                            }

                            Object keyValue = obj.get(keyStr);
                            ainfo.appendDescription("`" + keyStr + "` : " + keyValue + "\n");
                        });
                    }
                    event.getChannel().sendMessage(ainfo.build()).queue();
                    event.getMessage().addReaction("✅").queue();
                    ainfo.clear();
                } catch (NullPointerException ignored) {
                }
            }
        }

        if (args[0].equalsIgnoreCase((HiveBot.prefix + "appendData"))) {
            if(event.getAuthor().getId().equals(Config.get("OWNER_ID"))){
                try {
                    if(args.length > 2){
                        ArrayList<String> values = new ArrayList<>();
                        for(int i = 2;i<args.length;i++){
                            values.add(args[i]);
                        }
                        System.out.println(values);
                        HiveBot.dataFile.appendData(args[1],values);
                        event.getMessage().addReaction("✅").queue();
                    } else {
                        System.out.println("debug");
                        HiveBot.dataFile.appendData(args[1], args[2]);
                        event.getMessage().addReaction("✅").queue();
                    }
                }catch(NullPointerException e){
                    e.printStackTrace();
                    event.getChannel().sendMessage("Something went wrong").queue();
                }
            }
        }

        if (args[0].equalsIgnoreCase((HiveBot.prefix + "writeData"))) {
            if(event.getAuthor().getId().equals(Config.get("OWNER_ID"))){
                try {
                    HiveBot.dataFile.writeData(args[1], args[2]);
                    event.getMessage().addReaction("✅").queue();
                }catch(NullPointerException e){
                    event.getChannel().sendMessage("Something went wrong").queue();
                }
            }
        }

        if (args[0].equalsIgnoreCase((HiveBot.prefix + "reloadData"))) {
            if(event.getAuthor().getId().equals(Config.get("OWNER_ID"))){
                try {
                    HiveBot.dataFile.loadDataFile();
                    event.getMessage().addReaction("✅").queue();
                }catch(NullPointerException e){
                    event.getChannel().sendMessage("Something went wrong").queue();
                }
            }
        }

        if (args[0].equalsIgnoreCase((HiveBot.prefix + "removeData"))) {
            if(event.getAuthor().getId().equals(Config.get("OWNER_ID"))){
                try {
                    if(args.length >= 3){
                        HiveBot.dataFile.removeData(args[1],args[2]);
                        event.getMessage().addReaction("✅").queue();
                    } else if(args.length >= 2) {
                        HiveBot.dataFile.removeData(args[1]);
                        event.getMessage().addReaction("✅").queue();
                    }
                }catch(NullPointerException e){
                    event.getChannel().sendMessage("Something went wrong").queue();
                }
            }
        }

        if (args[0].equalsIgnoreCase((HiveBot.prefix + "sendMarkers"))) {
            try {
                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    sendMarkers(event.getGuild());
                    event.getMessage().addReaction("✅").queue();
                }
            }catch(PermissionException e){
            }
        }

        if (args[0].equalsIgnoreCase((HiveBot.prefix + "Welcome"))) {
            try {
                Object object = HiveBot.dataFile.getData("WelcomeMessage");
                JSONObject jsonObject = (JSONObject) object;

                String welcomeMessage = (String) jsonObject.get(event.getGuild().getId());
                welcomeMessage = welcomeMessage.replace("{user}", event.getMember().getEffectiveName());
                event.getChannel().sendMessage(welcomeMessage).queue();
                event.getMessage().addReaction("✅").queue();
            }catch(NullPointerException e){
                System.out.println("Could not find object");
            }
        }
    }
}
