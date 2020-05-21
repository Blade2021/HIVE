package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.Command;

import java.awt.*;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import static rsystems.HiveBot.LOGGER;

public class Info extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) throws PermissionException {
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        if(args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(15).getCommand())){
            LOGGER.info(HiveBot.commands.get(15).getCommand() + " called by " + event.getAuthor().getAsTag());
            event.getChannel().sendMessage("Current Version: " + HiveBot.version).queue();
            //System.out.println(HiveBot.commands.get(2).getCommand());
        }

        //Info command
        if(args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(2).getCommand())){
            LOGGER.info(HiveBot.commands.get(2).getCommand() + " called by " + event.getAuthor().getAsTag());

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
                                //info.addField("`" + c.getCommand() + "`", c.getDescription(), false);
                                if (c.getCommandType().equalsIgnoreCase("utility")) {
                                    //info.addField("",c.getCommand(),true);
                                    utilityCommands.add(c.getCommand());
                                }
                                if (c.getCommandType().equalsIgnoreCase("information")) {
                                    //info.addField("",c.getCommand(),true);
                                    infoCommands.add(c.getCommand());
                                }
                                if (c.getCommandType().equalsIgnoreCase("fun")) {
                                    //info.addField("",c.getCommand(),true);
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

        if(args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(30).getCommand())) {
            LOGGER.info(HiveBot.commands.get(30).getCommand() + " called by " + event.getAuthor().getAsTag());
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            long uptime = runtimeMXBean.getUptime();
            long uptimeinSeconds = uptime / 1000;
            long uptimeHours = uptimeinSeconds / (60 * 60);
            long uptimeMinutes = (uptimeinSeconds / 60) - (uptimeHours * 60);
            long uptimeSeconds = uptimeinSeconds % 60;
            event.getChannel().sendMessageFormat("Uptime: %s hours, %s minutes, %s seconds",uptimeHours,uptimeMinutes,uptimeSeconds).queue();
        }

        //Request features or report bugs
        if((args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(18).getCommand())) || (args[0].equalsIgnoreCase(HiveBot.prefix + "requests")) || (args[0].equalsIgnoreCase(HiveBot.prefix + "bug"))){
            LOGGER.info(HiveBot.commands.get(18).getCommand() + " called by " + event.getAuthor().getAsTag());
            event.getChannel().sendMessage("Request new features and notify of a bug on GitHub: https://github.com/Blade2021/HIVEWasp/issues").queue();
        }

        //Three Laws Safe command
        if((args[0].equalsIgnoreCase((HiveBot.prefix + "botlaws"))) || (args[0].equalsIgnoreCase((HiveBot.prefix + HiveBot.commands.get(14).getCommand())))){
            LOGGER.info(HiveBot.commands.get(14).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {
                EmbedBuilder info = new EmbedBuilder();
                info.setTitle("3 Laws of BoTs");
                info.setThumbnail("http://marc-jennings.co.uk/wp-content/uploads/2020/04/robot_1f916.png");
                info.addField("`Law 1`", "A BoT will **NOT** trigger another bot", false);
                info.addField("`Law 2`", "A BoT will **NOT** trigger itself", false);
                info.addField("`Law 3`", "A BoT will **NOT** kill hoomans", false);
                info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
                info.setColor(Color.CYAN);
                event.getChannel().sendTyping().queue();
                event.getChannel().sendMessage(info.build()).queue();
                info.clear();
            } catch (PermissionException e){
                event.getChannel().sendMessage("Missing Permission: " + e.getPermission().getName()).queue();
            }
        }

        //Execute order 66 command
        if(event.getMessage().getContentRaw().startsWith(HiveBot.prefix + HiveBot.commands.get(16).getCommand())){
            LOGGER.info(HiveBot.commands.get(16).getCommand() + " called by " + event.getAuthor().getAsTag());

            String[] rand = {" Yes my lord.", " Yes My lord, The troops have been notified.",
                    " Yes my lord, Alright troops, move out!", " Right away my lord"};

            String[] gifLinks = {"https://tenor.com/view/heading-in-stormtrooper-starwars-gif-4902440","https://tenor.com/view/dancing-darth-vader-storm-troopers-gif-5595478",
                    "https://tenor.com/view/cooking-storm-trooper-pancakes-star-wars-gif-15568625", "https://tenor.com/view/roast-squad-star-wars-order-gif-10141605","https://tenor.com/view/star-wars-rey-gif-14874010", "https://tenor.com/view/star-wars-pizza-darth-vader-han-solo-gif-4826490"};

            int index = new Random().nextInt(rand.length);
            int gifIndex = new Random().nextInt(gifLinks.length);

            event.getChannel().sendMessage(event.getAuthor().getAsMention() + rand[index] + "\n\n" + gifLinks[gifIndex]).queue();

        }


        //Rule 34 Command
        if((event.getMessage().getContentRaw().startsWith(HiveBot.prefix + HiveBot.commands.get(17).getCommand())) || (event.getMessage().getContentRaw().startsWith(HiveBot.prefix + "rule34"))){
            LOGGER.info(HiveBot.commands.get(17).getCommand() + " called by " + event.getAuthor().getAsTag());
            //Random string selection
            String[] rand = {" You need help.", " nope, im out.",
                    " rUlE tHiRty FouR", " I don't know what to say to you anymore"};
            int index = new Random().nextInt(rand.length);

            if((args.length >= 2) && (args[1].equalsIgnoreCase("sponge"))){
                index = 2;
            }

            //If random triggers sponge response
            if(index == 2) {

                File file = null;  //Initalize file as null
                try {
                    // Get path of JAR file
                    file = new File(this.getClass().getProtectionDomain().
                            getCodeSource().getLocation().toURI().getPath());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                // Get sponge image path
                try {
                    String path = file.getParent() + "/images/sponge.png";
                    File image = new File(path);
                    //Send message WITH image
                    event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + rand[index])
                            .addFile(image)
                            .queue();
                } catch (NullPointerException e) {
                    //Send regular message without image
                    System.out.println("Couldn't find file:");
                    event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + rand[index])
                            .queue();
                }
            } else {
                //Send one of the other responses
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + rand[index]).queue();
            }

        }
    }

}
