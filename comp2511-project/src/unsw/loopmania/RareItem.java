package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

public class RareItem extends StaticEntity implements Item {

    public RareItem(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
    }

    @Override
    public void equip() {
        super.setXY(0, 1);
    }

    @Override
    public int getEquipSlotX() {
        return 0;
    }

    @Override
    public int getEquipSlotY() {
        return 1;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/vampire.png")).toURI().toString()); // TODO: UPDATE (TWEAKED FOR TESTING)
    }

    @Override
    public Stats getStats() {
        Stats stats = new Stats();
        return stats;
    }

    @Override
    public String getType() {
        return "RareItem";
    }

    @Override
    public void setCoordinates(int x, int y) {
        super.setXY(x, y);        
    }
    
}
