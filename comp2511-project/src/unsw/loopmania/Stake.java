package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped sword in the backend world
 */
public class Stake extends Weapon {
    public Stake(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        setDamage(5);
        this.value = 500;
    }

    @Override
    public Stats getStats() {
        // TODO Auto-generated method stub
        Stats result = super.getStats();
        result.setItemName("Stake");
        return result;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/stake.png")).toURI().toString());
    }
}
