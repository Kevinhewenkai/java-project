package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped armour in the backend world
 */
public class Armour extends StaticEntity implements Item {

    private int defence = 20;
    private int value = 30;

    // TODO: handle halving enemy attacks

    public Armour(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public void equip() {
        super.setXY(1, 1);
    }

    @Override
    public int getEquipSlotX() {
        return 1;
    }

    @Override
    public int getEquipSlotY() {
        return 1;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/armour.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setDefence(defence);
        stats.setValue(value);
        stats.setItemName("Armour");
        return stats;
    }

    @Override
    public Object getType() {
        return "Armour";
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
