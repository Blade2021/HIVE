package rsystems.commands;

import com.sun.tools.javac.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import rsystems.Config;
import rsystems.adapters.Command;
import rsystems.adapters.RoleCheck;
import rsystems.handlers.DataFile;
import rsystems.HiveBot;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import static rsystems.events.DocStream.sendMarkers;

public class AdminInfo extends ListenerAdapter {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        if(HiveBot.messageCheck.CheckUser(event.getAuthor().getId())){
            return;
        }



        String[] args = event.getMessage().getContentRaw().split("\\s+");

        // Admin Menu command
        if(HiveBot.commands.get(13).checkCommand(event.getMessage().getContentRaw())){
            try{
                int rank = RoleCheck.getRank(event,event.getMember().getId());
                /*
                if(RoleCheck.checkRank(event,event.getMember(),HiveBot.commands.get(13))){

                }

                 */
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(13))){
                    event.getAuthor().openPrivateChannel().queue((channel) ->
                    {
                        EmbedBuilder info = new EmbedBuilder();
                        info.setTitle("HIVE Admin Commands");
                        info.setDescription("BoT Prefix: " + HiveBot.prefix + "\n" + event.getGuild().getName() + "Current User Count: `" + event.getGuild().getMemberCount() + " Users`");
                        info.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());

                        //Initialize categories for each type
                        ArrayList<String> utilityCommands = new ArrayList<>();
                        ArrayList<String> botCommands = new ArrayList<>();
                        ArrayList<String> userCommands = new ArrayList<>();
                        ArrayList<String> karmaCommands = new ArrayList<>();

                        //Assign the commands to categories
                        for(Command c:HiveBot.commands){
                            if((rank >= c.getRank()) && (c.getRank() > 0)){
                                try {
                                    //info.addField("`" + c.getCommand() + "`", c.getDescription(), false);
                                    if (c.getCommandType().equalsIgnoreCase("utility-admin")) {
                                        //info.addField("",c.getCommand(),true);
                                        utilityCommands.add(c.getCommand());
                                    }
                                    if (c.getCommandType().equalsIgnoreCase("botControl")) {
                                        //info.addField("",c.getCommand(),true);
                                        botCommands.add(c.getCommand());
                                    }
                                    if (c.getCommandType().equalsIgnoreCase("user-control")) {
                                        //info.addField("",c.getCommand(),true);
                                        userCommands.add(c.getCommand());
                                    }
                                    if (c.getCommandType().equalsIgnoreCase("karma-admin")) {
                                        //info.addField("",c.getCommand(),true);
                                        karmaCommands.add(c.getCommand());
                                    }
                                } catch (NullPointerException e){
                                    System.out.println("Found null for command" + c.getCommand());
                                }
                            }
                        }

                        StringBuilder utilityString = new StringBuilder();
                        for(String s:utilityCommands){
                            utilityString.append(s).append("\n");
                        }

                        StringBuilder botString = new StringBuilder();
                        for(String s:botCommands){
                            botString.append(s).append("\n");
                        }

                        StringBuilder userString = new StringBuilder();
                        for(String s:userCommands){
                            userString.append(s).append("\n");
                        }

                        StringBuilder karmaString = new StringBuilder();
                        for(String s:karmaCommands){
                            karmaString.append(s).append("\n");
                        }

                        info.addField("Utility", utilityString.toString(),true);
                        info.addField("Bot Control",botString.toString(),true);
                        info.addField("User Control",userString.toString(),true);
                        info.addField("Karma Admin",karmaString.toString(),true);

                        info.setFooter("Please use these commands responsibly", event.getMember().getUser().getAvatarUrl());
                        info.setColor(Color.RED);
                        channel.sendMessage(info.build()).queue(
                                success -> {
                                    event.getMessage().addReaction("✅").queue();
                                },
                                failure -> {
                                    event.getMessage().addReaction("⚠").queue();
                                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I am unable to DM you due to your privacy settings. Please update and try again.").queue();
                                });
                        info.clear();
                        channel.close();
                    });
                    event.getMessage().addReaction("✅").queue();
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



        // Stats Command
        if(HiveBot.commands.get(20).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(20))){
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
                }
            } catch(NullPointerException e){
                System.out.println("Could not find permissions");
            } catch(InsufficientPermissionException e){
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + "Missing Permission: " + e.getPermission().getName()).queue();
            }
        }

        // Get Data
        if(HiveBot.commands.get(21).checkCommand(event.getMessage().getContentRaw())){
            if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(21))){
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

                            if(keyStr.toString().equalsIgnoreCase("alternativeWelcomeMessage")){
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

        //Append Data command
        if(HiveBot.commands.get(33).checkCommand(event.getMessage().getContentRaw())){
            if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(33))){
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

        //Write Data Command
        if(HiveBot.commands.get(34).checkCommand(event.getMessage().getContentRaw())){
            if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(34))){
                try {
                    HiveBot.dataFile.writeData(args[1], args[2]);
                    event.getMessage().addReaction("✅").queue();
                }catch(NullPointerException e){
                    event.getChannel().sendMessage("Something went wrong").queue();
                }
            }
        }

        //todo:Check on usage of this command
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

        //Remove Data command
        if(HiveBot.commands.get(35).checkCommand(event.getMessage().getContentRaw())){
            if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(35))){
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

        // Send Markers
        if(HiveBot.commands.get(24).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(24))){
                    sendMarkers(event.getGuild());
                    event.getMessage().addReaction("✅").queue();
                }else {
                    event.getChannel().sendMessage(event.getAuthor().getAsMention() + " You do not have access to that command").queue();
                }
            }catch(PermissionException e){
            }
        }

        //Trigger welcome message
        if(HiveBot.commands.get(29).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(29))){
                    Object object = HiveBot.dataFile.getData("WelcomeMessage");
                    JSONObject jsonObject = (JSONObject) object;

                    String welcomeMessage = (String) jsonObject.get(event.getGuild().getId());
                    welcomeMessage = welcomeMessage.replace("{user}", event.getMember().getEffectiveName());
                    event.getChannel().sendMessage(welcomeMessage).queue();
                    event.getMessage().addReaction("✅").queue();
                }
            }catch(NullPointerException e){
                System.out.println("Could not find object");
            }
        }

        //Get Stream Mode
        if(HiveBot.commands.get(25).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(25))){
                    event.getChannel().sendMessage("Stream Mode: " + HiveBot.getStreamMode()).queue();
                }
            }catch(PermissionException e){
            }
        }

        //Set Stream Mode
        if(HiveBot.commands.get(26).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(26))){
                    if(args[1].equalsIgnoreCase("true")){
                        HiveBot.setStreamMode(true);
                    } else {
                        HiveBot.setStreamMode(false);
                    }
                    event.getMessage().addReaction("✅ ").queue();
                }
            }catch(PermissionException e){
            }catch(IndexOutOfBoundsException e){
                event.getChannel().sendMessage("Missing parameter").queue();
                System.out.println("Missing parameter");
            }
        }


        //Test command
        if(HiveBot.commands.get(47).checkCommand(event.getMessage().getContentRaw())){
            try {
                EmbedBuilder info = new EmbedBuilder();
                info.setDescription("[GitHub](http://github.com)");

                event.getChannel().sendMessage(info.build()).queue();
                info.clear();
            }catch(PermissionException e){
            }catch(IndexOutOfBoundsException e){
                event.getChannel().sendMessage("Missing parameter").queue();
                System.out.println("Missing parameter");
            }
        }

        // Reload All command
        if(HiveBot.commands.get(32).checkCommand(event.getMessage().getContentRaw())){
            try {
                if (RoleCheck.checkRank(event.getMessage(),event.getMember(),HiveBot.commands.get(32))){
                    HiveBot.reloadAll();
                    event.getMessage().addReaction("✅").queue();
                }
            } catch (NullPointerException e) {
            }
        }

    }
    private File getJarFile() throws FileNotFoundException {
        String path = Main.class.getResource(Main.class.getSimpleName() + ".class").getFile();
        if(path.startsWith("/")) {
            throw new FileNotFoundException("This is not a jar file: \n" + path);
        }
        path = ClassLoader.getSystemClassLoader().getResource(path).getFile();

        return new File(path.substring(0, path.lastIndexOf('!')));
    }
}
