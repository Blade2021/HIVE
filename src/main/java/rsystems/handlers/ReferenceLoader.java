package rsystems.handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rsystems.HiveBot;
import rsystems.adapters.ExtendedReference;
import rsystems.adapters.Reference;

import java.util.ArrayList;

public class ReferenceLoader {
    private static JSONFileHandler extendedReferenceFile = new JSONFileHandler("extendedReferenceData.json");
    private static Object extendedReferenceCommands = extendedReferenceFile.getDatafileData();
    private static JSONObject extendedReferenceData = (JSONObject) extendedReferenceCommands;

    private static JSONFileHandler referenceFile = new JSONFileHandler("referenceData.json");
    private static Object referenceCommands = referenceFile.getDatafileData();
    private static JSONObject referenceData = (JSONObject) referenceCommands;

    public ReferenceLoader(){
        // Load extended references
        extendedReferenceData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = extendedReferenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            //Create a temporary Reference Object to hold the data
            ExtendedReference tempExtendedReference = new ExtendedReference(
                    keyStr.toString(),
                    parsedValue.get("description").toString().replace("{prefix}",HiveBot.prefix),
                    parsedValue.get("installation").toString()
            );

            try{
                tempExtendedReference.setAliases(getArrayList(parsedValue,"alias"));
            } catch(NullPointerException e){
            }

            tempExtendedReference.setLinks(getArrayList(parsedValue,"links"));
            tempExtendedReference.setCategory(getArrayList(parsedValue,"category"));

            //Add Reference Object into the references array
            //HiveBot.extendedReferences.add(tempExtendedReference);
            HiveBot.extendedReferenceMap.putIfAbsent(keyStr.toString(),tempExtendedReference);

        });

        // Load Simple references
        referenceData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = referenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            //Create a temporary Reference Object to hold the data
            Reference tempReference = new Reference(
                    keyStr.toString(),
                    parsedValue.get("description").toString()
            );

            try{
                tempReference.setAliases(getArrayList(parsedValue,"alias"));
            } catch(NullPointerException e){
            }
            tempReference.setCategory(getArrayList(parsedValue,"category"));

            //Add Reference Object into the references array
            //HiveBot.references.add(tempReference);
            HiveBot.referenceMap.putIfAbsent(keyStr.toString(),tempReference);

        });
    }

    public void updateData(){
        extendedReferenceFile.loadDataFile();
        extendedReferenceCommands = extendedReferenceFile.getDatafileData();
        extendedReferenceData = (JSONObject) extendedReferenceCommands;

        referenceFile.loadDataFile();
        referenceCommands = referenceFile.getDatafileData();
        referenceData = (JSONObject) referenceCommands;

        // Clear all references from array to recreate them below.
        //HiveBot.extendedReferences.clear();
        HiveBot.extendedReferenceMap.clear();
        //HiveBot.references.clear();
        HiveBot.referenceMap.clear();

        extendedReferenceData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = extendedReferenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

                //Create a temporary Reference Object to hold the data
                ExtendedReference tempRef = new ExtendedReference(
                        keyStr.toString(),
                        parsedValue.get("installation").toString(),
                        parsedValue.get("description").toString().replace("{prefix}",HiveBot.prefix)
                );

                tempRef.setLinks(getArrayList(parsedValue,"links"));
                tempRef.setCategory(getArrayList(parsedValue,"category"));

                try{
                    tempRef.setAliases(getArrayList(parsedValue,"alias"));
                } catch(NullPointerException e){
                }

                //Add Reference Object into the references array
                HiveBot.extendedReferenceMap.putIfAbsent(keyStr.toString(),tempRef);
                //HiveBot.extendedReferences.add(tempRef);

        });

        // Load Simple references
        referenceData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = referenceData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            //Create a temporary Reference Object to hold the data
            Reference tempReference = new Reference(
                    keyStr.toString(),
                    parsedValue.get("description").toString()
            );

            try{
                tempReference.setAliases(getArrayList(parsedValue,"alias"));
            } catch(NullPointerException e){
            }
            tempReference.setCategory(getArrayList(parsedValue,"category"));

            //Add Reference Object into the references array
            //HiveBot.references.add(tempReference);
            HiveBot.referenceMap.putIfAbsent(keyStr.toString(), tempReference);

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
