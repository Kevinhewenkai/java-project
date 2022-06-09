package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class DoggieCoin extends StaticEntity implements Item {
    private int value = 100;

    public DoggieCoin(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        stats.setValue(value);
        stats.setItemName("DoggieCoin");
        return stats;
    }

    @Override
    public Object getType() {
        return "DoggieCoin";
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
        return new Image((new File("src/images/doggie_coin.png")).toURI().toString());
    }

    @Override
    public void equip() {
    }

    @Override
    public void setCoordinates(int x, int y) {
        super.setXY(x, y);
    }

    public void alternatePrice() {
        switch (value) {
            case 100:
                this.value = 15000;
                break;
            case 15000:
                this.value = 2;
                break;
            default:
                break;
        }
        return;
    }
}
