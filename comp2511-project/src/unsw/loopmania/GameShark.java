package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class GameShark extends RareItem {
    private int totalCycles;
    private int value = 700;

    public GameShark(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/gameshark.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        // TODO Auto-generated method stub
        Stats stats = super.getStats();
        stats.setItemName("GameShark");
        stats.setValue(value);
        return stats;
    }

    public void incrementTotalCycles() {
        totalCycles++;
    }

    public int getTotalCycles() {
        return totalCycles;
    }
}