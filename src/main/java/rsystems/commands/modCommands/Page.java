package rsystems.commands.modCommands;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.json.simple.JSONObject;
import rsystems.Config;
import rsystems.HiveBot;
import rsystems.objects.Command;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class Page extends Command {

    @Override
    public Integer getPermissionIndex() {
        return 2;
    }

    boolean cooldown = false;

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, PrivateMessageReceivedEvent event) {

    }

    @Override
    public void dispatch(User sender, MessageChannel channel, Message message, String content, GuildMessageReceivedEvent event) {
        if(HiveBot.mainGuild().getMemberById(sender.getIdLong()) != null){
            String[] args = event.getMessage().getContentRaw().split("\\s+");
            String data = "";
            if(args.length > 1){
                data = event.getMessage().getContentRaw().substring(args[0].length()+1);
            }

            JSONObject json = new JSONObject();
            try {
                json.put("UserID", event.getAuthor().getId());
                json.put("User", event.getAuthor().getName());
                json.put("Data", data);
            } catch (NullPointerException e) {
                event.getChannel().sendMessage("Something went wrong...").queue();
            }

            // Pull the webhook URL from .env file for security purposes.
            final String POSTS_API_URL = Config.get("notifyLight");

            if (!cooldown) {
                try {

                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(POSTS_API_URL))
                            .header("Content-Type","application/json; charset=UTF-8")
                            .POST(HttpRequest.BodyPublishers.ofString(json.toJSONString()))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(event.getAuthor().getAsTag() + " : " + response);
                    int scode = response.statusCode();
                    if (scode == 200) {
                        event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + " Hook sent...").queue();
                    } else {
                        event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + " Something went wrong. Code[ " + scode + " ]").queue();
                    }

                    cooldown = true;


                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException ie) {
                            }
                            cooldown = false;
                        }
                    }).start();


                } catch (IllegalArgumentException e) {
                    System.out.println("Something went wrong");
                } catch (InsufficientPermissionException e) {
                    event.getChannel().sendMessage("I am lacking permissions to perform this action").queue();
                } catch (IndexOutOfBoundsException e) {
                    event.getChannel().sendMessage("Nice try " + event.getAuthor().getAsMention() + "! I see what you did there!").queue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage("Something went wrong...").queue();
                } catch (IOException e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage("Something went wrong...").queue();
                }
            } else {
                String[] rand = {" Listen!  I'm working on it!", " Look! Do you want to be the bot?", " This hook is smoking hot! Lets let it cool down some!",
                        " There's only so much squirreling I can help to prevent", "Nope.  The doc is off in his own little world here",
                        " I cannot break the laws of bots"};
                int index = new Random().nextInt(rand.length);  //Generate a random int for string selection
                event.getChannel().sendMessage(event.getMessage().getAuthor().getAsMention() + rand[index]).queue();
            }

        }
    }

    @Override
    public String getHelp() {
        return null;
    }
}
