package rsystems.handlers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rsystems.HiveBot;
import rsystems.adapters.Command;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CommandData {
    public static JSONObject fileData;

    public CommandData() {
        loadDataFile();
    }

    public void loadDataFile() {
        JSONParser parser = new JSONParser();
        Object obj;
        String path = "commands.json";

        try{
            obj  = parser.parse(new FileReader(path));
            fileData = (JSONObject) obj;

            fileData.keySet().forEach(keyStr ->
            {
                Object keyValue = fileData.get(keyStr);
                JSONObject jsonObject = (JSONObject) keyValue;

                for(Command c: HiveBot.commands){
                    if(c.getCommand().equalsIgnoreCase(keyStr.toString())){

                        int rank = Integer.parseInt(jsonObject.get("rank").toString());
                        c.setRank(rank);

                        String description = (String) jsonObject.get("description");
                        c.setDescription(description);

                        String syntax = jsonObject.get("syntax").toString();
                        c.setSyntax(syntax);
                        try {
                            String commandType = jsonObject.get("commandType").toString();
                            c.setCommandType(commandType);
                        } catch (NullPointerException e){}
                    }
                }
            });

        } catch(FileNotFoundException e){
            System.out.println("Could not find JSON File");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

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
