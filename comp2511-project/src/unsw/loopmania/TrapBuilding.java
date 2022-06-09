package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a building in the world
 */
public class TrapBuilding extends Building {

    private int DAMAGE_VALUE;

    public TrapBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        DAMAGE_VALUE = 20;
    }

    public int getDamageValue() {
        return DAMAGE_VALUE;
    }

    @Override
    public String getType() {
        return "Trap";
    }
}
