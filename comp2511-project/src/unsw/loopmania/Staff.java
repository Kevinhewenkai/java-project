package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped staff in the backend world
 */
public class Staff extends Weapon {
    public Staff(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        setDamage(2);
        this.value = 100;
    }

    @Override
    public Stats getStats() {
        // TODO Auto-generated method stub
        Stats result = super.getStats();
        result.setItemName("Staff");
        return result;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/staff.png")).toURI().toString());
    }
}
