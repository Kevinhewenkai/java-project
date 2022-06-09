package unsw.loopmania;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.util.Pair;

public class Inventory implements Item {

    public int inventoryWidth;
    public int inventoryHeight;

    private List<Item> items = new ArrayList<Item>();

    public Inventory(int width, int height) {
        this.inventoryWidth = width;
        this.inventoryHeight = height;
    }
    
    public void addItem(Item item) {
        if (!isFull()) {
            Pair<Integer, Integer> slot = getFirstAvailableSlot();
            item.setCoordinates(slot.getKey(), slot.getValue());
            items.add(item);
        }
    }
    
    public void removeItem(Item item) {
        items.remove(item);
        return;
    }

    public void removeAndDestroyItem(Item item) {
        items.remove(item);
        item.destroy();
        return;
    }


    public Boolean isFull() {
        if (items.size() == inventoryWidth * inventoryHeight) {
            return true;
        }
        return false;
    }

    /**
     * remove item at a particular index in the unequipped inventory items list (this is ordered based on age in the starter code)
     * @param index index from 0 to length-1
     */
    public void removeItemByIndex(int index) {
        Item item = getItemByIndex(index);
        items.remove(index);
        item.destroy();
    }

    /**
     * remove an item by x,y coordinates
     * @param x x coordinate from 0 to width-1
     * @param y y coordinate from 0 to height-1
     */
    public void removeItemByCoordinates(int x, int y) {
        Item item = getItemByCoordinates(x, y);
        removeItem(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public Pair<Integer, Integer> getFirstAvailableSlot() {
        for (int y = 0; y < inventoryHeight; y++) {
            for (int x = 0; x < inventoryWidth; x++) {
                if (getItemByCoordinates(x, y) == null) {
                    return new Pair<Integer, Integer>(x, y);
                }
            }
        }
        return null;
    }

    public Item getItemByIndex(int index) {
        return items.get(index);
    }

    /**
     * return an inventory item by x and y coordinates
     * assumes that no 2 inventory items share x and y coordinates
     * @param x x index from 0 to width-1
     * @param y y index from 0 to height-1
     * @return inventory item at the input position
     */
    public Item getItemByCoordinates(int x, int y) {
        for (Item item : items) {
            if ((item.getX() == x) && (item.getY() == y)){
                return item;
            }
        }
        return null;
    }

    /**
     * Checks whether item object exists in items list
     * @param item
     * @return boolean
     */
    public Boolean itemExists(Item item) {
        for (Item i: items) {
            if (i.equals(item)) {
                return true;
            }
        }
        return false;
    }
    

    @Override
    public Stats getStats() {
        
        Stats totalStats = new Stats();

        // Get Stats!
        for (Item itemStat: items) {
            totalStats.addStats(itemStat.getStats());
        }
        
        return totalStats;
    }

    @Override
    public String getType() {
        return "Inventory";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // NOT SURE ABOUT THESE -> Inventory doesn't need a getX() or getY() but it implements Item
    
    @Override
    public int getX() {
        return -1;
    }

    @Override
    public int getY() {
        return -1;
    }

    @Override
    public void setCoordinates(int x, int y){}

    @Override
    public void equip() {}

    @Override
    public void destroy() {}

    @Override
    public Image getImage() {
        return new Image((new File("src/images/empty_slot.png")).toURI().toString());
    }

    @Override
    public int getEquipSlotX() {
        return -1;
    }

    @Override 
    public int getEquipSlotY() {
        return -1;
    }

}

