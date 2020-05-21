package rsystems.adapters;

import java.util.ArrayList;

public class Reference {
    private String refCode;
    private String installString;
    private String description;
    private ArrayList<String> category;
    private ArrayList<String> links;
    private ArrayList<String> alias;


    public Reference(String refCode) {
        this.refCode = refCode;
    }

    public Reference(String refCode, String installString) {
        this.refCode = refCode;
        this.installString = installString;
    }

    public Reference(String refCode, String installString, String description) {
        this.refCode = refCode;
        this.installString = installString;
        this.description = description;
    }

    public Reference(String refCode, String installString, String description, ArrayList<String>  category) {
        this.refCode = refCode;
        this.installString = installString;
        this.description = description;
        this.category = category;
    }

    public Reference(String refCode, String installString, String description, ArrayList<String>  category, ArrayList<String> links) {
        this.refCode = refCode;
        this.installString = installString;
        this.description = description;
        this.category = category;
        this.links = links;
    }

    public String getRefCode() {
        return refCode;
    }

    public String getInstallString() {
        return installString;
    }

    public void setInstallString(String installString) {
        this.installString = installString;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ArrayList<String> getLinks() {
        return links;
    }

    public void setLinks(ArrayList<String> links) {
        this.links = links;
    }

    public void addLinks(String link){
        this.links.add(link);
    }

    public ArrayList<String> getAlias() {
        return alias;
    }

    public void setAlias(ArrayList<String> alias) {
        this.alias = alias;
    }

    public void addAlias(String alias){
        this.alias.add(alias);
    }
}
