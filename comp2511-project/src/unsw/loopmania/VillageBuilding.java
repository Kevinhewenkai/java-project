package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a building in the world
 */
public class VillageBuilding extends Building {

    private int HEALTH_BOOST;

    public VillageBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        HEALTH_BOOST = 25;
    }

    public int getHealthBoost() {
        return HEALTH_BOOST;
    }

    @Override
    public String getType() {
        return "Village";
    }
}
