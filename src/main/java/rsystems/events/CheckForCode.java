package rsystems.events;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import rsystems.HiveBot;
import rsystems.adapters.HasteBin;

import java.io.IOException;

public class CheckForCode extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentDisplay();

        if ((message.contains("-")) || ((message.contains("{")) && (message.contains("}"))) || (message.contains(":"))) {
            try {
                //String message = event.getMessage().getContentDisplay();

                /*
                DOES THE MESSAGE CONTAIN FORMATTED CODE ALREADY?
                 */
                if (message.contains("```")) {
                    int formattedCodeStart = message.indexOf("```");
                    int formattedCodeEnd = (message.indexOf("```",formattedCodeStart+1)+4);
                    String firstcut = message.substring(0,formattedCodeStart);
                    String secondCut = message.substring(formattedCodeEnd);
                    message = firstcut + secondCut;
                }

                if (event.getMessage().getContentRaw().contains("{")) {
                    System.out.println("Test passed");
                }

                String code = "";

                /*
                START SEARCHING FOR JSON CODE
                 */
                if ((message.contains("{")) && (message.contains("}"))) {
                    System.out.println("Code found");
                    int startCodeSearch = message.indexOf("{");
                    int endCodeSearch = message.indexOf("}");
                    code = jsonCodeSearch(message, startCodeSearch, endCodeSearch);
                    try {
                        if (code.length() > 300) {
                            try {
                                HasteBin bin = new HasteBin();
                                String url = bin.post(code, false);
                                event.getMessage().delete().queue();
                                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I found code in your message but it was too large to process here.  We moved your code to here: " + url).queue();
                            } catch (IOException e) {
                                System.out.println("Something went wrong");
                            }
                        } else {
                            System.out.println(code.length());
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "JSON code found\n```json\n" + code + "```\nTry using code formatting next time.\n**"+ HiveBot.prefix+"code** for more info").queue();

                            if ((code.length() - 1) == message.length()) {
                                event.getMessage().delete().queue();
                            } else {
                                System.out.println("Message Size: " + message.length());
                                System.out.println("Code Size: " + code.length());
                            }
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Null found");
                    }

                }


                /*
                START SEARCHING FOR YAML CODE
                 */
                if ((message.contains("-")) || (message.contains(":"))) {
                    int startCodeSearch = message.indexOf("-");
                    int startCodeSearchColon = message.indexOf(":");

                    if ((startCodeSearch < startCodeSearchColon) && (startCodeSearch > 0)) {
                        code = yamlCodeSearch(message, startCodeSearch);
                    } else {
                        int startOfLine = message.lastIndexOf("\n", startCodeSearchColon);
                        code = yamlCodeSearch(message, startOfLine + 1);
                    }


                    try {
                        if (code.length() > 900) {
                            try {
                                HasteBin bin = new HasteBin();
                                String url = bin.post(code, false);
                                event.getMessage().delete().queue();
                                event.getChannel().sendMessage(event.getAuthor().getAsMention() + " I found code in your message but it was too large to process here.  We moved your code to here: " + url).queue();
                            } catch (IOException e) {
                                System.out.println("Something went wrong");
                            }
                        } else {
                            System.out.println(code.length());
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + "Yaml code found\n```yaml\n" + code + "```\nTry using code formatting next time.\n**"+ HiveBot.prefix+"code** for more info").queue();

                            if ((code.length() - 1) == message.length()) {
                                event.getMessage().delete().queue();
                            } else {
                                System.out.println("Message Size: " + message.length());
                                System.out.println("Code Size: " + code.length());
                            }
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Found null");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String yamlCodeSearch(String message, int codeStartIndex) {
        boolean followerLine = false;
        int codeStart = 0;
        int lineIndex = 0;
        String[] lines = message.substring(codeStartIndex).split("\\n");

        StringBuilder code = new StringBuilder();
        for (String s : lines) {
            lineIndex++;
            if ((s.startsWith("  ")) || (s.startsWith("-")) || (s.replace("\\n", "").endsWith(":"))) {
                codeStart = lineIndex;
                code.append(s).append("\n");
                if (s.startsWith("  ")) {
                    followerLine = true;
                }
            } else {
                if (codeStart > 0) {
                    if ((s.startsWith("\\n")) || (s.equals(""))) {
                        continue;
                    } else {
                        break;
                    }
                }
            }
        }

        String[] codeSplit = code.toString().split("\\n");
        int codeLinesSize = codeSplit.length;

        if ((followerLine) && codeLinesSize > 1) {
            if (codeCheckYAML(code.toString())) {
                return code.toString();
            }
        }
        return null;
    }

    private String jsonCodeSearch(String message, int codeStartIndex, int codeEndIndex) {
        String code = message.substring(codeStartIndex, codeEndIndex + 1);
        String[] lines = code.split("\\n");

        if (lines.length > 2) {
            try {
                JsonParser parser = new JsonParser();
                parser.parse(code);
            } catch (JsonSyntaxException e) {
                return null;
            }
            return code;
        }
        return null;
    }

    private boolean codeCheckYAML(String code) {
        Yaml yamlValidator = new Yaml();
        try {
            yamlValidator.load(code);
        } catch (YAMLException e) {
            return false;
        }
        return true;
    }

}