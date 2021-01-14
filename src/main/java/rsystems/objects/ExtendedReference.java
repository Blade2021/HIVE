package rsystems.objects;

import java.util.ArrayList;

public class ExtendedReference extends Reference{
    private String installString;
    private ArrayList<String> links;

    public ExtendedReference(String referenceCommand, String description) {
        super(referenceCommand,description);
    }

    public ExtendedReference(String referenceCommand, String description, String installString) {
        super(referenceCommand,description);
        this.installString = installString;
    }

    public ExtendedReference(String referenceCommand, String description, String installString, ArrayList<String> aliases, ArrayList<String> category) {
        super(referenceCommand,description,aliases,category);
        this.installString = installString;
    }

    public ExtendedReference(String referenceCommand, String description, String installString, ArrayList<String> aliases, ArrayList<String> category, ArrayList<String> links) {
        super(referenceCommand,description,aliases,category);
        this.installString = installString;
        this.links = links;
    }

    public String getInstallString() {
        return installString;
    }

    public void setInstallString(String installString) {
        this.installString = installString;
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
}
