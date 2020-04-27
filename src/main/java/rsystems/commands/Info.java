package rsystems.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Random;

public class Info extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) throws PermissionException {
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");

        //Info command
        if((args[0].equalsIgnoreCase((HiveBot.prefix + "info")) || (args[0].equalsIgnoreCase(((HiveBot.prefix + "help")))) || (args[0].equalsIgnoreCase((HiveBot.prefix + "commands"))))) {
            event.getMessage().addReaction("✅").queue();
            try {
                //Open a private channel with requester
                event.getAuthor().openPrivateChannel().queue((channel) ->
                {
                    EmbedBuilder info = new EmbedBuilder();
                    info.setTitle("HIVE BoT Information");
                    info.setDescription("BoT Prefix: " + HiveBot.prefix + "\n**All commands ignore case for your convenience.**");
                    info.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl());
                    info.addField("`**Official Commands:**`", "", false);
                    info.addField("`Notify`", "Enable/Disable notification channel for stream events", false);
                    info.addField("`Ping`", "Grab the latest latency between the bot and Discord servers", false);
                    info.addField("`Helpdoc`", "Post a link to the Helpful Documents Page", false);
                    info.addField("`Who`", "Display information about HIVE", false);
                    info.addField("`TwitchSub`", "Awesome Twitch Subscriber information", false);
                    info.addField("`**Fun Commands**`", "", false);
                    info.addField("`Execute Order 66`", "Send a message to the troops", false);
                    info.addField("`ThreeLawsSafe`", "You can figure it out ;) ", false);
                    info.addField("`Rule 34`", "Uh... Same as above....", false);
                    info.setFooter("Called by " + event.getMessage().getAuthor().getName(), event.getMember().getUser().getAvatarUrl());
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

        //Request features or report bugs
        if((args[0].equalsIgnoreCase(HiveBot.prefix + "request")) || (args[0].equalsIgnoreCase(HiveBot.prefix + "requests")) || (args[0].equalsIgnoreCase(HiveBot.prefix + "bug"))){
            event.getChannel().sendMessage("Request new features and notify of a bug on GitHub: https://github.com/Blade2021/HIVEWasp/issues").queue();
        }

        //Three Laws Safe command
        if((args[0].equalsIgnoreCase((HiveBot.prefix + "botlaws"))) || (args[0].equalsIgnoreCase((HiveBot.prefix + "threelawssafe")))){
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
        if(event.getMessage().getContentRaw().startsWith(HiveBot.prefix + "execute order 66")){

            String[] rand = {" Yes my lord.", " Yes My lord, The troops have been notified.",
                    " Yes my lord, Alright troops, move out!", " Right away my lord"};

            String[] gifLinks = {"https://tenor.com/view/heading-in-stormtrooper-starwars-gif-4902440","https://tenor.com/view/dancing-darth-vader-storm-troopers-gif-5595478",
                    "https://tenor.com/view/cooking-storm-trooper-pancakes-star-wars-gif-15568625", "https://tenor.com/view/roast-squad-star-wars-order-gif-10141605","https://tenor.com/view/star-wars-rey-gif-14874010", "https://tenor.com/view/star-wars-pizza-darth-vader-han-solo-gif-4826490"};

            int index = new Random().nextInt(rand.length);
            int gifIndex = new Random().nextInt(gifLinks.length);

            event.getChannel().sendMessage(event.getAuthor().getAsMention() + rand[index] + "\n\n" + gifLinks[gifIndex]).queue();

        }


        //Rule 34 Command
        if((event.getMessage().getContentRaw().startsWith(HiveBot.prefix + "rule 34")) || (event.getMessage().getContentRaw().startsWith(HiveBot.prefix + "rule34"))){

            //Random string selection
            String[] rand = {" You need help.", " nope, im out.",
                    " rUlE tHiRty FouR", " I don't know what to say to you anymore"};
            int index = new Random().nextInt(rand.length);

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
