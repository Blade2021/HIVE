package rsystems.handlers;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;

public class Jackson {
    private static FileWriter file;
    private static JSONParser parser = new JSONParser();
    private static Object obj;
    private static JSONObject jsonObj = null;
    private static final String path = "data.json";

    public static JSONObject readJFile() {
        try{
            obj  = parser.parse(new FileReader(path));
            jsonObj = (JSONObject) obj;

        } catch(FileNotFoundException e){
            System.out.println("Could not find JSON File");
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonObj;
    }

    public static String readDataBit(String id){
        try{
            obj  = parser.parse(new FileReader(path));
            jsonObj = (JSONObject) obj;

            return jsonObj.get(id).toString();

        } catch(FileNotFoundException e){
            System.out.println("Could not find JSON File");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            return "";
        }

        return "";
    }

    public static boolean writeData(String id,String value){
        try {
            parser = new JSONParser();
            obj  = parser.parse(new FileReader(path));
            jsonObj = (JSONObject) obj;

            jsonObj.put(id,value);

            

            file = new FileWriter(path);
            file.write(jsonObj.toJSONString());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                file.flush();
                file.close();
                return true;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
