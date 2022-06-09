package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class HealthPotion extends StaticEntity implements Item {

    public HealthPotion(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    private int hp = 10;
    private int value = 200;

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setHp(hp);
        stats.setValue(value);
        stats.setItemName("HealthPotion");
        return stats;
    }

    @Override
    public int getEquipSlotX() {
        return -1;
    }

    @Override
    public int getEquipSlotY() {
        return -1;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/brilliant_blue_new.png")).toURI().toString());
    }

    @Override
    public Object getType() {
        return "HealthPotion";
    }

    @Override
    public void setCoordinates(int x, int y) {
        super.setXY(x, y);
    }

    @Override
    public void equip() {
    }

}
