package rsystems.adapters;

import java.util.ArrayList;

public class Reference {
    protected String referenceCommand;
    protected String description;
    protected ArrayList<String> aliases;
    protected ArrayList<String> category;

    public Reference(String referenceCommand, String description) {
        this.referenceCommand = referenceCommand;
        this.description = description;
    }

    public Reference(String referenceCommand, String description, ArrayList<String> aliases) {
        this.referenceCommand = referenceCommand;
        this.description = description;
        this.aliases = aliases;
    }

    public Reference(String referenceCommand, String description, ArrayList<String> aliases, ArrayList<String> category) {
        this.referenceCommand = referenceCommand;
        this.description = description;
        this.aliases = aliases;
        this.category = category;
    }

    public String getReferenceCommand() {
        return referenceCommand;
    }

    public void setReferenceCommand(String referenceCommand) {
        this.referenceCommand = referenceCommand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getAliases() {
        return aliases;
    }

    public void setAliases(ArrayList<String> aliases) {
        this.aliases = aliases;
    }

    public void addAlias(String alias){
        this.aliases.add(alias);
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public void addCategory(String category){
        this.category.add(category);
    }
}
