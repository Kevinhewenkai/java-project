package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped sword in the backend world
 */
public class Sword extends Weapon {
    public Sword(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        setDamage(15);
        this.value = 100;
    }

    @Override
    public Stats getStats() {
        // TODO Auto-generated method stub
        Stats result = super.getStats();
        result.setItemName("Sword");
        return result;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/basic_sword.png")).toURI().toString());
    }
}
