package rsystems.adapters;

public class Command {
    protected String command;
    protected String description;
    protected String syntax;
    protected int minimumArgCount;
    protected int rank;
    protected String commandType;

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
}
