package rsystems.handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rsystems.HiveBot;
import rsystems.adapters.GuildObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuildLoader {
    private static JSONObject guildFileData;
    public static Map<String,GuildObject> guilds = new HashMap<>();

    public GuildLoader() {
        JSONParser parser = new JSONParser();
        Object obj;
        String path = "newData.json";

        try{
            obj  = parser.parse(new FileReader(path));
            guildFileData = (JSONObject) obj;

            guildFileData.keySet().forEach(keySet -> {
                Object guildData = guildFileData.get(keySet);
                JSONObject gu = (JSONObject) guildData;
                System.out.println(keySet.toString());
                System.out.println(gu);

                GuildObject tempObject = new GuildObject();
                tempObject.setGuildID(keySet.toString());
                tempObject.setPrefix(gu.get("Prefix").toString());
                tempObject.setBotNick(gu.get("BotNick").toString());
                if(gu.get("WelcomeEnable").toString().equalsIgnoreCase("true")){
                    tempObject.setWelcomeEnable(true);
                } else {
                    tempObject.setWelcomeEnable(false);
                }
                if(gu.get("LanguageFilter").toString().equalsIgnoreCase("true")){
                    tempObject.setLanguageFilter(true);
                } else {
                    tempObject.setLanguageFilter(false);
                }
                tempObject.setBadWords(getStringList(gu,"BadWords"));
                tempObject.setWelcomeMessage(gu.get("WelcomeMessage").toString());
                tempObject.setAlternativeWelcomeMessage(gu.get("alternativeWelcomeMessage").toString());
                tempObject.setQuestionsPostChannelID(gu.get("QuestionPostChannel").toString());
                tempObject.setLogChannelID(gu.get("LogChannelID").toString());
                tempObject.setSuggestionPostChannelID(gu.get("SuggestionsPostChannel").toString());
                tempObject.setSuggestionReviewChannelID(gu.get("SuggestionsReviewChannel").toString());
                tempObject.setAssignableRoles(getStringList(gu,"AssignableRoles"));
                tempObject.setBotSpamChannelID(gu.get("BotSpamChannelID").toString());

                guilds.put(keySet.toString(),tempObject);
            });
        } catch(FileNotFoundException e){
            System.out.println("Could not find JSON File");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    // Grab the list from the JSON Object
    private ArrayList<String> getStringList(JSONObject object,String key){
        try{
            JSONArray objArray = (JSONArray) object.get(key);
            ArrayList<String> listData = new ArrayList<>();
            for(Object o:objArray){
                listData.add(o.toString());
            }
            return listData;
        } catch(NullPointerException e){
            e.printStackTrace();
            System.out.println("Found null when assigning roles to list");
        }
        return null;
    }
}
