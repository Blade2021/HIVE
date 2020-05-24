package rsystems.handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rsystems.HiveBot;
import rsystems.adapters.Reference;

import java.util.ArrayList;

public class ReferenceLoader {
    private static JSONFileHandler referenceFile = new JSONFileHandler("referenceData.json");
    private static Object referenceCommands = referenceFile.getDatafileData();
    private static JSONObject referenceData = (JSONObject) referenceCommands;

    public ReferenceLoader(){
        referenceData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = referenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            //Create a temporary Reference Object to hold the data
            Reference tempRef = new Reference(
                    keyStr.toString(),
                    parsedValue.get("installation").toString(),
                    parsedValue.get("description").toString()
            );

            try{
                tempRef.setAlias(getArrayList(parsedValue,"alias"));
            } catch(NullPointerException e){
            }

            tempRef.setLinks(getArrayList(parsedValue,"links"));
            tempRef.setCategory(getArrayList(parsedValue,"category"));

            //Add Reference Object into the references array
            HiveBot.references.add(tempRef);

        });
    }

    public void updateData(){
        referenceFile.loadDataFile();
        referenceCommands = referenceFile.getDatafileData();
        referenceData = (JSONObject) referenceCommands;


        HiveBot.references.clear();
        referenceData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = referenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

                //Create a temporary Reference Object to hold the data
                Reference tempRef = new Reference(
                        keyStr.toString(),
                        parsedValue.get("installation").toString(),
                        parsedValue.get("description").toString()
                );

                tempRef.setLinks(getArrayList(parsedValue,"links"));
                tempRef.setCategory(getArrayList(parsedValue,"category"));

                try{
                    tempRef.setAlias(getArrayList(parsedValue,"alias"));
                } catch(NullPointerException e){
                }

                //Add Reference Object into the references array
                HiveBot.references.add(tempRef);

        });
    }

    private ArrayList<String> getArrayList(JSONObject parsedValue,String key){
        //Get links from datafile
        JSONArray jsonArray = (JSONArray) parsedValue.get(key);
        ArrayList<String> arrayList = new ArrayList<>();

        for(Object linkObject:jsonArray){
            arrayList.add(linkObject.toString());
        }
        return arrayList;
    }
}
