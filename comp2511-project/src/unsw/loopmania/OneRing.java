package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class OneRing extends RareItem {
    private int value = 1500;

    public OneRing(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/the_one_ring.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        // TODO Auto-generated method stub
        Stats stats = super.getStats();
        stats.setValue(value);
        stats.setItemName("OneRing");
        return stats;
    }
}
