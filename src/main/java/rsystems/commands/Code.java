package rsystems.commands;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import rsystems.HiveBot;

import java.awt.*;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static rsystems.HiveBot.LOGGER;

public class Code extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        //Escape if message came from a bot account
        if(event.getMessage().getAuthor().isBot()){
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");


        // Check if the message contains a command
        if(HiveBot.commands.get(5).checkCommand(event.getMessage().getContentRaw())){
            LOGGER.info(HiveBot.commands.get(5).getCommand() + " called by " + event.getAuthor().getAsTag());
            try {
                try{
                    event.getMessage().delete().reason("Bot command called").queue();
                } catch(InsufficientPermissionException | NullPointerException ignored){
                }

                EmbedBuilder info = new EmbedBuilder();
                info.setTitle("Formatting code in Discord:");
                info.setDescription("Paste your code in the following format Replacing `{language}` with your desired language:\n \\`\\`\\``{language}` \nCode Here\n\\`\\`\\`");
                info.appendDescription("\n`Before:`\n{\"greeting\":\"Hello World\"}\n\n`After:` ```json\n{\"greeting\":\"Hello World\"}```[More information](https://github.com/Blade2021/HIVE/wiki/Code-Formatting-&-Validation)");
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

        // Check to see if initial message contains a json code block
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
            } catch (IndexOutOfBoundsException | PermissionException ignored){
                // Ignore any errors
            }
        }

        // Check to see if initial message contains a yaml code block
        if (event.getMessage().getContentRaw().contains("```yaml")){
            String message = event.getMessage().getContentRaw();

            try{
                event.getMessage().addReaction("\uD83D\uDC1D ").queue();
            }catch(InsufficientPermissionException | NullPointerException ignored){
            }

            // Try to locate the start and end of the code block
            try{
                int startCode = message.indexOf("```yaml");
                int endCode = message.indexOf("```",startCode+3);

                String code = message.substring(startCode+7, endCode);
                if(validateYaml(code)){
                    event.getMessage().addReaction("✅").queue();
                } else {
                    event.getMessage().addReaction("🚫").queue();
                }
            } catch (IndexOutOfBoundsException | PermissionException | NullPointerException ignored){
                // Ignore any errors
            }
        }
    }

    /*
    This method is called upon a message is updated.  (Edited)  This way we can recheck the message for proper code.
     */
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event){
        if (event.getMessage().getContentRaw().contains("```yaml")){
            String message = event.getMessage().getContentRaw();

            try{
                // Add a Bee Emoji to the message to signal we received a code message.
                event.getMessage().addReaction("\uD83D\uDC1D ").queue();
            }catch(InsufficientPermissionException | NullPointerException ignored){
            }

            try{
                //Find the start and end of the code snippet.
                int startCode = message.indexOf("```yaml");
                int endCode = message.indexOf("```",startCode+3); //Add 3 to the start to exclude the first set of ticks.

                String code = message.substring(startCode+7, endCode); // Add 7 to the starter to exclude ```yaml
                // The validation returned true (Good YAML Code)
                if(validateYaml(code)){
                    event.getMessage().addReaction("✅").queue();
                    try{
                        //Remove the NG emoji if there was one added.  If not the catch will trigger
                        event.getMessage().removeReaction("🚫").queue();
                    } catch(PermissionException | NullPointerException ignored) {
                    }
                } else {
                    // The validation of the code block returned a failure
                    event.getMessage().addReaction("🚫").queue();
                    try{
                        //Remove the OK emoji if there was one added.  If not the catch will trigger
                        event.getMessage().removeReaction("✅").queue();
                    } catch (IndexOutOfBoundsException | PermissionException | NullPointerException ignored){
                        // Ignore any errors
                    }
                }
            } catch (IndexOutOfBoundsException ignored){
                // Could not locate beginning and end of code block
            }
        }

        // Does the message contain a json code block
        if (event.getMessage().getContentRaw().contains("```json")){
            String message = event.getMessage().getContentRaw();
            try{
                // Add a Bee Emoji to the message to signal we received a code message.
                event.getMessage().addReaction("\uD83D\uDC1D ").queue();
            }catch(InsufficientPermissionException | NullPointerException ignored){
            }

            try{
                //Find the start and end of the code snippet.
                int startCode = message.indexOf("```json");
                int endCode = message.indexOf("```",startCode+3); //Add 3 to the start to exclude the first set of ticks.

                String code = message.substring(startCode+7, endCode); // Add 7 to the starter to exclude ```json

                // The validation of the code block returned OK
                if(validateJson(code)){
                    event.getMessage().addReaction("✅").queue();
                    try{
                        //Remove the NG emoji if there was one added.  If not the catch will trigger
                        event.getMessage().removeReaction("🚫").queue();
                    } catch(PermissionException | NullPointerException ignored) {
                    }

                } else {
                    // The validation of the code block returned a failure
                    event.getMessage().addReaction("🚫").queue();
                    try{
                        //Remove the OK emoji if there was one added.  If not the catch will trigger
                        event.getMessage().removeReaction("✅").queue();
                    } catch(PermissionException | NullPointerException ignored) {
                    }
                }
            } catch (IndexOutOfBoundsException ignored){
                // Could not locate beginning and end of code block
            }
        }

    }

    /*
        This method will validate the json code and return true if the code validates, returns false if invalid
     */
    private boolean validateJson(String code){
        try {
            JsonParser parser = new JsonParser();
            parser.parse(code);
        } catch(JsonSyntaxException e){
            return false;
        }
        return true;
    }


    /*
        This method will validate the yaml code and return true if the code validates, returns false if invalid
     */
    private boolean validateYaml(String code){
        Yaml yamlValidator = new Yaml();
        try{
            yamlValidator.load(code);
        } catch(YAMLException e){
            return false;
        }
        return true;
    }



}
