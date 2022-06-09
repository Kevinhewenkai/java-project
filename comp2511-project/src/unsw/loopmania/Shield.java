package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped shield in the backend world
 */
public class Shield extends StaticEntity implements Item {

    private int defence = 15;
    private int value = 35;

    public Shield(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    public void equip() {
        super.setXY(2, 0);
    }

    @Override
    public int getEquipSlotX() {
        return 2;
    }

    @Override
    public int getEquipSlotY() {
        return 0;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/shield.png")).toURI().toString());
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setDefence(defence);
        stats.setValue(value);
        stats.setItemName("Shield");
        return stats;
    }

    @Override
    public Object getType() {
        return "Shield";
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
