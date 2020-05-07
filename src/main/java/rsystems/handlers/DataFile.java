package rsystems.handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataFile {
    public static JSONObject datafileData;

    public DataFile() {
        loadDataFile();
    }

    public void loadDataFile() {
        JSONParser parser = new JSONParser();
        Object obj;
        String path = "data.json";

        try{
            obj  = parser.parse(new FileReader(path));
            datafileData = (JSONObject) obj;
        } catch(FileNotFoundException e){
            System.out.println("Could not find JSON File");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

    }

    public JSONObject getDatafileData(){
        return datafileData;
    }

    public JSONArray getArrayData(String key){
        try{
            JSONObject obj = (JSONObject) datafileData.get(key);
            return (JSONArray) obj.get(key);
        } catch(NullPointerException e){
            e.printStackTrace();
            System.out.println("Found null when assigning roles to list");
        }
        return null;
    }

    public ArrayList<String> getArrayList(String key){
        try{
            JSONArray objArray = (JSONArray) datafileData.get(key);
            ArrayList<String> listData = new ArrayList();
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
