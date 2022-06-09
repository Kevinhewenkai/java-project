package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a building in the world
 */
public class VampireCastleBuilding extends Building {
    private int loopCount;
    private static final int VAMPIRE_SPAWN_RATE = 5;
    public VampireCastleBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        loopCount = 0;
    }

    public void incrementLoopCount() {
        loopCount = (loopCount + 1) % VAMPIRE_SPAWN_RATE;
    }

    public int getLoopCount() {
        return loopCount;
    }

    @Override
    public String getType() {
        return "VampireCastle";
    }
}
