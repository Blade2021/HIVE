package rsystems.handlers;
import org.json.simple.JSONObject;
import rsystems.HiveBot;
import rsystems.adapters.Command;

import java.util.ArrayList;

public class CommandLoader {
    private static JSONFileHandler commandFile = new JSONFileHandler("commandData.json");
    private Object referenceCommands = commandFile.getDatafileData();
    private JSONObject commandData = (JSONObject) referenceCommands;

    public CommandLoader(){
        commandData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = commandData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            //Create a temporary Reference Object to hold the data
            Command tempCommand = new Command(
                    keyStr.toString(),
                    parsedValue.get("description").toString(),
                    parsedValue.get("syntax").toString(),
                    Integer.parseInt(parsedValue.get("minArgCount").toString()),
                    Integer.parseInt(parsedValue.get("rank").toString())
            );

            //Add Reference Object into the references array
            HiveBot.commands.add(tempCommand);

        });
    }

    public void updateData(){
        commandData.keySet().forEach(keyStr -> {
            //Form the object into a JSONObject for processing
            Object keyValue = commandData.get(keyStr);
            JSONObject parsedValue = (JSONObject) keyValue;

            for(Command c:HiveBot.commands){
                if(c.getCommand().equalsIgnoreCase(keyStr.toString())){
                    c.setDescription(parsedValue.get("description").toString());
                    c.setSyntax(parsedValue.get("syntax").toString());
                    c.setMinimumArgCount(Integer.parseInt(parsedValue.get("minimumArgCount").toString()));
                    c.setRank(Integer.parseInt(parsedValue.get("rank").toString()));
                }
            }
        });
    }
}
