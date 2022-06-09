package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a Building in the world
 * which doesn't move
 */
public abstract class Building extends StaticEntity {
    public Building(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    public Boolean getInCampfireRadius(MovingEntity entity) {
        return false;
    }

    public Boolean getInTowerRadius(MovingEntity entity) {
        return false;
    }

    public Boolean entityOnBuilding(MovingEntity entity) {
        if (getX() == entity.getX() && getY() == entity.getY()) {
            return true;
        }
        return false;
    }

    public String getType() {
        return "";
    }

}