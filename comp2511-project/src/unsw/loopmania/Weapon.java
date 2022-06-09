package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped sword in the backend world
 */
public class Weapon extends StaticEntity implements Item {
    int value;
    protected int damage;

    public Weapon(SimpleIntegerProperty x, SimpleIntegerProperty y){
        super(x, y);
    } 

    public void equip() {
        super.setXY(0, 0);
    }

    @Override
    public int getEquipSlotX() {
        return 0;
    }

    @Override 
    public int getEquipSlotY() {
        return 0;
    }
    
    @Override
    public Image getImage() {
        return new Image((new File("src/images/vampire.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setDamage(damage);
        stats.setValue(value);
        return stats;
    }

    @Override
    public String getType() {
        return "Weapon";
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

    public void setDamage(int damage) {
        this.damage = damage;
    }
}

