package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped shield in the backend world
 */
public class Helmet extends StaticEntity implements Item {
    
    private int scalarDefence = 3;

    // reduces character's damage
    private int damage = -2;
    private int value = 20;

    public Helmet(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public void equip() {
        super.setXY(1, 0);
    }
    
    @Override
    public int getEquipSlotX() {
        return 1;
    }

    @Override 
    public int getEquipSlotY() {
        return 0;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/helmet.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setScalarDefence(scalarDefence);
        stats.setDamage(damage);
        stats.setItemName("Helmet");
        stats.setValue(value);
        return stats;
    }

    @Override
    public Object getType() {
        return "Helmet";
    }

    @Override
    public int getX() {
        return super.getX();
    }

    @Override
    public int getY() {
        return super.getY();
    }

    @Override
    public void setCoordinates(int x, int y) {
        super.setXY(x, y);        
    }

}

