package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a building in the world
 */
public class HerosCastleBuilding extends Building {
    public HerosCastleBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public String getType() {
        return "HerosCastle";
    }

    public void enableLuckyClover() {
        
    }
}
