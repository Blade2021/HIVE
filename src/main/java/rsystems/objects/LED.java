package rsystems.objects;

public class LED {

    private String ledName;
    private int ledVoltage;
    private boolean whiteIncluded;
    private float wattagePerPixel_Theoretical;
    private float wattagePerPixel_Tested;


    public LED(String ledName) {
        this.ledName = ledName;
    }

    public LED(String ledName, int ledVoltage, boolean whiteIncluded, int wattagePerPixel_Theoretical, int wattagePerPixel_Tested) {
        this.ledName = ledName;
        this.ledVoltage = ledVoltage;
        this.whiteIncluded = whiteIncluded;
        this.wattagePerPixel_Theoretical = wattagePerPixel_Theoretical;
        this.wattagePerPixel_Tested = wattagePerPixel_Tested;
    }

    public String getLedName() {
        return ledName;
    }

    public int getLedVoltage() {
        return ledVoltage;
    }

    public void setLedVoltage(int ledVoltage) {
        this.ledVoltage = ledVoltage;
    }

    public boolean isWhiteIncluded() {
        return whiteIncluded;
    }

    public void setWhiteIncluded(boolean whiteIncluded) {
        this.whiteIncluded = whiteIncluded;
    }

    public float getWattagePerPixel_Theoretical() {
        return wattagePerPixel_Theoretical;
    }

    public void setWattagePerPixel_Theoretical(float wattagePerPixel_Theoretical) {
        this.wattagePerPixel_Theoretical = wattagePerPixel_Theoretical;
    }

    public float getWattagePerPixel_Tested() {
        return wattagePerPixel_Tested;
    }

    public void setWattagePerPixel_Tested(float wattagePerPixel_Tested) {
        this.wattagePerPixel_Tested = wattagePerPixel_Tested;
    }
}
