package rsystems.handlers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rsystems.HiveBot;
import rsystems.objects.Reference;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReferenceHandler {

    private static ArrayList<String> referenceList = new ArrayList<>();

    private static final Map<String, Reference> refMap = new HashMap<>();

    public Map<String,Reference> getRefMap(){
        return refMap;
    }

    /*
    public void loadReferences(){
        refMap.clear();

        JSONFileHandler referenceFile = new JSONFileHandler("referenceData.json");
        Object referenceCommands = referenceFile.getDatafileData();
        JSONObject referenceData = (JSONObject) referenceCommands;

        // Load Simple references
        referenceData.keySet().forEach(keyStr -> {

            //Form the object into a JSONObject for processing
            Object keyValue = referenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            String tempReferenceDescriptionString = "";

            try {
                tempReferenceDescriptionString = parsedValue.get("description").toString();
                tempReferenceDescriptionString = tempReferenceDescriptionString.replaceAll("\\{prefix\\}", HiveBot.getPrefix());
            } catch (NullPointerException nullPointerException){
                System.out.printf("Skipping %s Reference.  No description found.%n",keyStr.toString());
                return;
            }

            //Create a temporary Reference Object to hold the data
            Reference tempReference = new Reference(
                    keyStr.toString(),
                    tempReferenceDescriptionString
            );

            try{
                tempReference.setAliases(getArrayList(parsedValue,"alias"));

            } catch(NullPointerException e){

            }
            tempReference.setCategories(getArrayList(parsedValue,"category"));

            if(parsedValue.get("title") != null){
                tempReference.setTitle(parsedValue.get("title").toString());
            }

            //Add Reference Object into the references array
            refMap.putIfAbsent(keyStr.toString(), tempReference);

        });
    }


     */

    public void loadReferences(){
        referenceList = new ArrayList<>();
        try {
            referenceList = HiveBot.database.getReferenceList();
        } catch(SQLException e){
            e.printStackTrace();
            ExceptionHandler.notifyException(e, this.getClass().getName());
        }
    }

    public boolean checkList(final String criteria){
        return referenceList.contains(criteria.toLowerCase());
    }

    private ArrayList<String> getArrayList(JSONObject parsedValue, String key){
        //Get links from datafile
        JSONArray jsonArray = (JSONArray) parsedValue.get(key);
        ArrayList<String> arrayList = new ArrayList<>();

        for(Object linkObject:jsonArray){
            arrayList.add(linkObject.toString());
        }
        return arrayList;
    }

    public MessageEmbed createEmbed(Reference reference){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN)
                .setDescription(reference.getDescription());

        if(reference.getTitle() != null){
            embedBuilder.setTitle(reference.getTitle());
        }

        return embedBuilder.build();
    }

}
