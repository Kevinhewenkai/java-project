package unsw.loopmania;

import javafx.scene.image.Image;

public interface Item {

    public Stats getStats();

    public Object getType();

    // NEW
    public int getEquipSlotX();

    // NEW
    public int getEquipSlotY();

    // NEW
    public Image getImage();

    public int getX();

    public int getY();

    public void setCoordinates(int x, int y);

    public void equip();

    public void destroy();
}
