package rsystems.commands;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
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
                try{
                    event.getMessage().delete().reason("Bot command called").queue();
                } catch(InsufficientPermissionException | NullPointerException ignored){
                }

                EmbedBuilder info = new EmbedBuilder();
                info.setTitle("Formatting code in Discord:");
                info.setDescription("Paste your code in the following format Replacing `{language}` with your desired language:\n \\`\\`\\``{language}` \nCode Here\n\\`\\`\\`");
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


        if (event.getMessage().getContentRaw().contains("```json")){
            String message = event.getMessage().getContentRaw();
            try{
                event.getMessage().addReaction("\uD83D\uDC1D ").queue();
            }catch(InsufficientPermissionException | NullPointerException ignored){
            }

            try{
                int startCode = message.indexOf("```json");
                int endCode = message.indexOf("```",startCode+3);

                String code = message.substring(startCode+7, endCode);
                if(validateJson(code)){
                    event.getMessage().addReaction("✅").queue();
                } else {
                    event.getMessage().addReaction("🚫").queue();
                }
            } catch (IndexOutOfBoundsException ignored){
            }
        }

        if (event.getMessage().getContentRaw().contains("```yaml")){
            String message = event.getMessage().getContentRaw();

            try{
                event.getMessage().addReaction("\uD83D\uDC1D ").queue();
            }catch(InsufficientPermissionException | NullPointerException ignored){
            }

            try{
                int startCode = message.indexOf("```yaml");
                int endCode = message.indexOf("```",startCode+3);

                String code = message.substring(startCode+7, endCode);
                if(validateYaml(code)){
                    event.getMessage().addReaction("✅").queue();
                } else {
                    event.getMessage().addReaction("🚫").queue();
                }
            } catch (IndexOutOfBoundsException ignored){
            }
        }
    }


    private boolean validateJson(String code){
        try {
            JsonParser parser = new JsonParser();
            parser.parse(code);
        } catch(JsonSyntaxException e){
            return false;
        }
        return true;
    }

    private boolean validateYaml(String code){
        Yaml yamlValidator = new Yaml();
        try{
            yamlValidator.load(code);
        } catch(YAMLException e){
            System.out.println(e.getCause());
            return false;
        }
        return true;
    }

}
