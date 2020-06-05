package rsystems.adapters;

public class KarmaUserInfo {
    private Long id;
    private String name;
    private int available_points;
    private int karma;
    private int ksent_pos;
    private int ksent_neg;

    public KarmaUserInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailable_points() {
        return available_points;
    }

    public void setAvailable_points(int available_points) {
        this.available_points = available_points;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getKsent_pos() {
        return ksent_pos;
    }

    public void setKsent_pos(int ksent_pos) {
        this.ksent_pos = ksent_pos;
    }

    public int getKsent_neg() {
        return ksent_neg;
    }

    public void setKsent_neg(int ksent_neg) {
        this.ksent_neg = ksent_neg;
    }
}
