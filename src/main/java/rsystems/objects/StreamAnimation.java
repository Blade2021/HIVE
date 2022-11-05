package rsystems.objects;

public class StreamAnimation {

    private final Integer id;
    private final String sceneName;
    private final String sourceName;

    private final Integer callerID;

    private final Integer runtime;
    private int cost;
    private int cooldown;

    private boolean enabled;

    public StreamAnimation(Integer id, String sceneName, String sourceName, Integer callerID, Integer runtime, int cost, int cooldown, boolean enabled) {
        this.id = id;
        this.sceneName = sceneName;
        this.sourceName = sourceName;
        this.callerID = callerID;
        this.runtime = runtime;
        this.cost = cost;
        this.cooldown = cooldown;
        this.enabled = enabled;
    }

    public Integer getId() {
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

    public Integer getCallerID() {
        return callerID;
    }

    public Integer getRuntime() {
        return runtime;
    }
}
