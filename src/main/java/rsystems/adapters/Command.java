package rsystems.adapters;

import rsystems.HiveBot;

import java.util.ArrayList;

public class Command {
    protected String command;
    protected String description;
    protected String syntax;
    protected int minimumArgCount;
    protected int rank;
    protected String commandType;
    protected ArrayList<String> alias = new ArrayList<>();


    public Command(String command){
        this.command = command;
    }

    public Command(String command, String description, String syntax, int minimumArgCount, int rank) {
        this.command = command;
        this.description = description;
        this.syntax = syntax;
        this.minimumArgCount = minimumArgCount;
        this.rank = rank;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public int getMinimumArgCount() { return minimumArgCount; }

    public void setMinimumArgCount(int minimumArgCount) { this.minimumArgCount = minimumArgCount; }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public ArrayList<String> getAlias() {
        return alias;
    }

    public void setAlias(ArrayList<String> alias) {
        this.alias.addAll(alias);
    }

    public void clearAlias(){
        this.alias.clear();
    }

    public boolean checkCommand(String message) {
        String[] args = message.split("\\s+");
        if (args[0].equalsIgnoreCase(HiveBot.prefix + this.command)) {
            return true;
        } else {
            final Boolean[] returnValue = {false};
            this.alias.forEach(alias -> {
                if (args[0].equalsIgnoreCase(HiveBot.prefix + alias)) {
                    returnValue[0] = true;
                }
            });
            return returnValue[0];
        }
    }

    public boolean checkCommand(String message, String prefix) {
        String[] args = message.split("\\s+");
        if (args[0].equalsIgnoreCase(prefix + this.command)) {
            return true;
        } else {
            final Boolean[] returnValue = {false};
            this.alias.forEach(alias -> {
                if (args[0].equalsIgnoreCase(prefix + alias)) {
                    returnValue[0] = true;
                }
            });
            return returnValue[0];
        }
    }

    public boolean checkCommand(String message, Boolean bool){
        String formattedMessage = message.toLowerCase();
        if(formattedMessage.startsWith(HiveBot.prefix + this.command.toLowerCase())){
            return true;
        } else {
            final Boolean[] returnValue = {false};
            this.alias.forEach(alias -> {
                if(formattedMessage.startsWith(HiveBot.prefix + alias.toLowerCase())){
                    returnValue[0] = true;
                }
            });
            return returnValue[0];
        }
    }

    public boolean helpCheck(String command){
        String[] args = command.split("\\s+");
        if(args[0].equalsIgnoreCase(this.command)){
            return true;
        } else {
            final Boolean[] returnValue = {false};
            this.alias.forEach(alias -> {
                if(args[0].equalsIgnoreCase(alias)){
                    returnValue[0] = true;
                }
            });
            return returnValue[0];
        }
    }
}
