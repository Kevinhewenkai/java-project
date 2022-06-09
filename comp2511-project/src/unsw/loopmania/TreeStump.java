package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class TreeStump extends Shield {

    int defence = 75;
    int value = 1000;

    public TreeStump(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/tree_stump.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setDefence(defence);
        stats.setValue(value);
        stats.setItemName("TreeStump");
        return stats;
    }
    
}
