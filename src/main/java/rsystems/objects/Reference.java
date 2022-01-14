package rsystems.objects;

import java.util.ArrayList;

public class Reference {
    protected String referenceCommand;
    protected String description;
    protected ArrayList<String> aliases;
    protected ArrayList<String> categories;
    protected String title = null;

    public Reference(String referenceCommand, String description) {
        this.referenceCommand = referenceCommand;
        this.description = description;
    }

    public Reference(String referenceCommand, String description, ArrayList<String> aliases) {
        this.referenceCommand = referenceCommand;
        this.description = description;
        this.aliases = aliases;
    }

    public Reference(String referenceCommand, String description, ArrayList<String> aliases, ArrayList<String> categories) {
        this.referenceCommand = referenceCommand;
        this.description = description;
        this.aliases = aliases;
        this.categories = categories;
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

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void addCategory(String category){
        this.categories.add(category);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
