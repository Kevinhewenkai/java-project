package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class LuckyClover extends RareItem {

    private int totalCycles;
    private int value = 10000;

    public LuckyClover(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/lucky_clover.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        // TODO Auto-generated method stub
        Stats result = super.getStats();
        result.setItemName("LuckyClover");
        result.setValue(value);
        return result;
    }

    public void incrementTotalCycles() {
        totalCycles++;
    }

    public int getTotalCycles() {
        return totalCycles;
    }
}
