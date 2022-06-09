package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a building in the world
 */
public class ZombiePitBuilding extends Building {
    public ZombiePitBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return "ZombiePit";
    }
}
