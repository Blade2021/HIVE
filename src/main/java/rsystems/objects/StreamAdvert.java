package rsystems.objects;

public class StreamAdvert {

    private int id;
    private String sceneName;
    private String sourceName;
    private int cost;
    private int cooldown;

    private boolean enabled;

    public StreamAdvert(int id, String sceneName, String sourceName) {
        this.id = id;
        this.sceneName = sceneName;
        this.sourceName = sourceName;
    }

    public StreamAdvert(int id, String sceneName, String sourceName, int cost, int cooldown) {
        this.id = id;
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.cost = cost;
        this.cooldown = cooldown;
        this.enabled = true;
    }

    public StreamAdvert(int id, String sceneName, String sourceName, int cost, int cooldown, boolean enabled) {
        this.id = id;
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.cost = cost;
        this.cooldown = cooldown;
        this.enabled = enabled;
    }


    public int getId() {
        return id;
    }

    public String getSceneName() {
        return sceneName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getCost() {
        return cost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
