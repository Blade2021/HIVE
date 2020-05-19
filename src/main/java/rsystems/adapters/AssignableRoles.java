package rsystems.adapters;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AssignableRoles {
    private ArrayList<String> roles = new ArrayList<>();

    public AssignableRoles(){
        loadRoleFile();
    }

    public void loadRoleFile() {
        FileWriter file;
        JSONParser parser = new JSONParser();
        Object obj;
        JSONObject jsonObj = null;
        String path = "data.json";

        try{
            obj  = parser.parse(new FileReader(path));
            jsonObj = (JSONObject) obj;
        } catch(FileNotFoundException e){
            System.out.println("Could not find JSON File");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        try{
            JSONArray jsonArray = (JSONArray) jsonObj.get("AssignableRoles");
            roles.clear();
            for(Object i:jsonArray){
                roles.add(i.toString());
            }
        } catch(NullPointerException e){
            e.printStackTrace();
            System.out.println("Found null when assigning roles to list");
        }

    }

    public ArrayList<String> getRoles(){
        return roles;
    }
}
