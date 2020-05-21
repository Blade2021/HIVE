package rsystems.adapters;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import rsystems.HiveBot;

import java.util.ArrayList;

public class MessageCheck extends ListenerAdapter {
    private ArrayList<String> ignoreusers = new ArrayList<>();

    public MessageCheck(){
        ignoreusers.addAll(HiveBot.dataFile.getArrayList("IgnoreUsers"));
    }

    public boolean CheckUser(String userid){
        return ignoreusers.contains(userid);
    }

    public void reloadData(){
        ignoreusers.clear();
        ignoreusers.addAll(HiveBot.dataFile.getArrayList("IgnoreUsers"));
    }


}
