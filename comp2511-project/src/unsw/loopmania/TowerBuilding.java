package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a building in the world
 */
public class TowerBuilding extends Building {

    private int EFFECT_RADIUS;
    private int SHOOTING_DAMAGE;

    public TowerBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        EFFECT_RADIUS = 3;
        SHOOTING_DAMAGE = 10;
    }

    public int getShootingDamage() {
        return SHOOTING_DAMAGE;
    }

    @Override
    public Boolean getInTowerRadius(MovingEntity entity) {
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
        return "Tower";
    }
}