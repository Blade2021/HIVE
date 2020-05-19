package rsystems.commands;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;
import rsystems.adapters.RoleCheck;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Random;

public class Say extends ListenerAdapter{

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args[0].equalsIgnoreCase(HiveBot.prefix + HiveBot.commands.get(28).getCommand())) {
            if(RoleCheck.getRank(event,event.getMember().getId()) >= HiveBot.commands.get(28).getRank()) {


                File file = null;  //Initalize file as null
                try {
                    // Get path of JAR file
                    file = new File(this.getClass().getProtectionDomain().
                            getCodeSource().getLocation().toURI().getPath());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


                char[] messageCharArray = event.getMessage().getContentDisplay().substring(args[0].length() + 1).toCharArray();
                event.getMessage().delete().queue();

                Random rd = new Random();
                int randInt;
                for (int index = 0; index < messageCharArray.length; index++) {
                    randInt = rd.nextInt(20);
                    if (Character.isAlphabetic(messageCharArray[index])) {
                        if (randInt > 10) {
                            messageCharArray[index] = Character.toUpperCase(messageCharArray[index]);
                        } else {
                            messageCharArray[index] = Character.toLowerCase(messageCharArray[index]);
                        }
                    }
                }
                String newMessage = new String(messageCharArray);


                // Get sponge image path
                try {
                    String path = file.getParent() + "/images/sponge.png";
                    File image = new File(path);
                    //Send message WITH image
                    event.getChannel().sendMessage(newMessage)
                            .addFile(image)
                            .queue();
                } catch (NullPointerException e) {
                    //Send regular message without image
                    System.out.println("Couldn't find file:");
                    event.getChannel().sendMessage(newMessage)
                            .queue();
                }
            }
        }
    }

}
