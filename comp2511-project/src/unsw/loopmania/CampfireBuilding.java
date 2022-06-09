package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a building in the world
 */
public class CampfireBuilding extends Building {

    private int EFFECT_RADIUS;

    public CampfireBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        EFFECT_RADIUS = 5;
    }

    @Override
    public Boolean getInCampfireRadius(MovingEntity entity) {
        int thisX = getX();
        int thisY = getY();
        int entityX = entity.getX();
        int entityY = entity.getY();

        if (Math.sqrt(Math.pow((thisX-entityX), 2) + Math.pow((thisY-entityY), 2)) <= EFFECT_RADIUS) {
            return true;
        }
        return false;
    }

    @Override
    public String getType() {
        return "Campfire";
    }
}