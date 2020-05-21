package rsystems.handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rsystems.HiveBot;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static rsystems.HiveBot.LOGGER;

public class JSONFileHandler {

    public static JSONObject fileData;
    private String path = "";

    public JSONFileHandler(String path){
        this.path = path;
        loadDataFile();
    }

    public void loadDataFile() {
        JSONParser parser = new JSONParser();
        Object obj;

        try{
            obj  = parser.parse(new FileReader(path));
            fileData = (JSONObject) obj;
        } catch(FileNotFoundException e){
            System.out.println("Could not find JSON File");
            LOGGER.severe("Could not load JSON file: " + path);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        //System.out.println(fileData);
    }

    public JSONObject getDatafileData(){
        return fileData;
    }

    public Object getData(String key){return fileData.get(key);}

    public JSONArray getArrayData(String key){
        try{
            JSONObject obj = (JSONObject) fileData.get(key);
            return (JSONArray) obj.get(key);
        } catch(NullPointerException e){
            e.printStackTrace();
            System.out.println("Found null when assigning roles to list");
        }
        return null;
    }

    public ArrayList<String> getArrayList(String key){
        try{
            JSONArray objArray = (JSONArray) fileData.get(key);
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

    public boolean writeData(String key,String value){
        try {
            fileData.put(key,value);
            writeFile();
            loadDataFile();
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean appendData(String key, String value){
        try{

            Object data = fileData.get(key);
            if(data instanceof JSONArray){
                JSONArray dataArray = (JSONArray)data;
                dataArray.add(value);
                fileData.put(key,dataArray);
            } else if(data instanceof JSONObject){
                Object obj = fileData.get(key);
                obj = obj + value;
                fileData.put(key,obj);
            } else {
                fileData.put(key,value);
            }
            writeFile();
            loadDataFile();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean appendData(String key, ArrayList<String> value){
        try{
            Object data = fileData.get(key);
            JSONArray dataArray = new JSONArray();

            if(data instanceof JSONArray){
                dataArray = (JSONArray)data;
            } else if(data instanceof JSONObject){
                dataArray.add(data);
            } else {
                dataArray.add(data);
            }

            for(String s:value){
                dataArray.add(s);
            }
            fileData.put(key,dataArray);

            writeFile();
            loadDataFile();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeData(String key){
        try {
            fileData.remove(key);
            writeFile();
            loadDataFile();
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeData(String key,String value){
        try {
            Object object = fileData.get(key);
            JSONArray jsonArray = (JSONArray) object;
            for(int index = 0;index<jsonArray.size();index++){
                if(jsonArray.get(index).toString().equalsIgnoreCase(value)){
                    jsonArray.remove(index);
                }
            }
            fileData.put(key,jsonArray);
            writeFile();
            loadDataFile();
            return true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void writeFile(){
        FileWriter file;
        final String path = "data.json";
        try {
            file = new FileWriter(path);
            file.write(fileData.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
