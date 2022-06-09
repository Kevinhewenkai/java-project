package unsw.loopmania;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

/**
 * represents an equipped or unequipped Anduril sword in the backend world
 */
public class Anduril extends Weapon {

    private int damage = 85;

    public Anduril(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y);
        setDamage(damage);
        this.value = 1000;
    }

    @Override
    public Stats getStats() {
        Stats result = super.getStats();
        result.setItemName("Anduril");
        return result;
    }

    @Override
    public Image getImage() {
        return new Image((new File("src/images/anduril_flame_of_the_west.png")).toURI().toString());
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public String getType() {
        return "Anduril";
    }
}

