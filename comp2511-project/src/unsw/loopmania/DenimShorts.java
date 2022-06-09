package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class DenimShorts extends RareItem {
    private int speed;
    private int totalCycles;
    private int value = 500;

    public DenimShorts(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/denim_shorts.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setSpeed(speed);
        stats.setItemName("DenimShorts");
        stats.setValue(value);
        return stats;
    }

    public int getTotalCycles() {
        return totalCycles;
    }

    public void setTotalCycles(int totalCycles) {
        this.totalCycles = totalCycles;
    }
}
