package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;

/**
 * represents the main character in the backend of the game world
 */
public class Character extends MovingEntity {

    private SimpleIntegerProperty gold = new SimpleIntegerProperty();
    private SimpleIntegerProperty xp = new SimpleIntegerProperty();
    private Boolean inCampfireRadius;
    private ArrayList<Ally> allies = new ArrayList<>();
    private Inventory equipped = new Inventory(3, 2);
    private Inventory unequipped = new Inventory(5, 3);
    private ArrayList<Item> boughItems = new ArrayList<Item>();
    private static final ArrayList<Integer> CRITICAL_DAMAGE_RANGE = new ArrayList<Integer>(Arrays.asList(10, 15));
    private Stats characterStats = new Stats();
    private Stats totalStats = new Stats();
    private Enemy allyTraitor;
    private int traitorMoves = 0;

    // MODE!
    // COMPOSITE PATTERN!
    public Character(PathPosition position, int hp, int damage, int speed, int defence) {
        super(position);
        characterStats.setHp(hp);
        characterStats.setDamage(damage);
        characterStats.setSpeed(speed);
        characterStats.setDefence(defence);
        totalStats.addStats(characterStats);
        inCampfireRadius = false;
        gold.set(0);
    }

    public void revalueDoggieCoin() {
        List<Item> unequippedItems = unequipped.getItems();
        for (Item item : unequippedItems) {
            if (item.getStats().getItemName().equals("DoggieCoin")) {
                DoggieCoin doggieCoin = (DoggieCoin) item;
                doggieCoin.alternatePrice();
            }
        }
    }

    public void decrementTraitorMoves() {
        traitorMoves -= 1;
    }

    public int getTraitorMoves() {
        return traitorMoves;
    }

    public void setTraitorMoves(int traitorMoves) {
        this.traitorMoves = traitorMoves;
    }

    public Enemy getAllyTraitor() {
        return allyTraitor;
    }

    public void setAllyTraitor(Enemy allyTraitor) {
        this.allyTraitor = allyTraitor;
    }

    // Generic function to check if character has an item of a specific type
    // equipped
    public boolean isItemEquipped(Class<?> cls) {
        for (Item i : equipped.getItems()) {
            if (cls == i.getClass()) {
                return true;
            }
        }
        return false;
    }

    public Item getItemOfType(Class<?> cls) {
        for (Item i : equipped.getItems()) {
            if (cls == i.getClass()) {
                return i;
            }
        }
        return null;
    }

    public ArrayList<Ally> getAllies() {
        return allies;
    }

    public void addAlly(Ally a) {
        allies.add(a);
    }

    public void removeAlly(Ally a) {
        allies.remove(a);
    }

    // MAKE ADD ALLY AND REMOVE ALLY

    public void setAllies(ArrayList<Ally> allies) {
        this.allies = allies;
    }

    @Override
    public Stats getStats() {
        // Stats totalStats = characterStats;
        // totalStats.addStats(equipped.getStats());
        return characterStats;
    }

    public Stats getTotalStats() {
        return totalStats;
    }

    public Inventory getEquipped() {
        return equipped;
    }

    public Inventory getUnequipped() {
        return unequipped;
    }

    /**
     * Add an item to character's unequipped inventory
     * 
     * @param item
     */
    public Item pickupItem(Item item) {
        Pair<Integer, Integer> slot = unequipped.getFirstAvailableSlot();
        if (slot == null) {
            // All slots are taken, so destroy the oldest item
            // TO-DO: give some cash/experience rewards for discarding item
            unequipped.removeItemByIndex(0);
            slot = unequipped.getFirstAvailableSlot();
        }
        item.setCoordinates(slot.getKey(), slot.getValue());
        unequipped.addItem(item);
        return item;
    }

    /**
     * Takes an item from the unequipped inventory and adds it to the equipped. Will
     * only work if there is an open weapon, helmet, shield, armour or rare item
     * slot. Equip() function call is defined by these classes respectively (since
     * they use specific slots). In rare item case: If First rareItem slot is open,
     * that will be used. If not Second will be used.
     * 
     * @param item
     * @return void
     */
    public void equipItem(Item item) {

        // HANDLES ITEM BEING NULL OR NOT IN UNEQUIPPED LIST
        if (item == null || !unequipped.getItems().contains(item))
            return;

        // UNEQUIPPING THE CURRENTLY EQUIPPED ITEM
        for (Item i : equipped.getItems()) {
            if (i.getType() == item.getType()) {
                unequipItem(i);
                break;
            }
        }
        unequipped.removeItem(item);
        equipped.addItem(item);

        // SETS COORDINATES FOR ITEM IN EQUIPPED SLOT
        item.equip();

        totalStats.addStats(item.getStats());
        return;
    }

    /**
     * Takes an item from the equipped inventory and moves it to the unequipped.
     * Unequipped inventory MUST have an open slot, otherwise nothing will happen.
     * 
     * @param item
     */
    public void unequipItem(Item item) {
        // HANDLES ITEM BEING NULL OR NOT IN EQUIPPED LIST
        if (item == null || !equipped.getItems().contains(item))
            return;

        totalStats.subtractStats(item.getStats());
        equipped.removeAndDestroyItem(item);
    }

    /**
     * user can use this function to sell items in the hero castle
     * 
     * @param character
     * @param e         the item can be either in the equipped inventory or
     *                  unequipped inventory
     * @throws Exception
     */
    // You can only sell an item from the unequipped inventory
    // It sells the first item in the inventory matching the item object passed in
    public void sell(int x, int y) {
        Item item = unequipped.getItemByCoordinates(x, y);
        if (!unequipped.itemExists(item) && item instanceof RareItem) {
            return;
        }
        unequipped.removeAndDestroyItem(item);
        int goldInt = gold.intValue();
        gold.set(goldInt + item.getStats().getValue());
    }

    // You can only buy an item if your unequipped inventory is not full
    // Bought item only goes to unequipped inventory
    public Item buy(Item item) {
        int itemValue = item.getStats().getValue();

        if (!unequipped.isFull() && gold.intValue() >= itemValue) {
            unequipped.addItem(item);
            int goldInt = gold.intValue();
            gold.set(goldInt -= item.getStats().getValue());
            boughItems.add(item);
            return item;
        }

        return null;
    }

    public void pickUpGold(int newGold) {
        int goldInt = gold.intValue();
        gold.set(goldInt + newGold);
    }

    public int getGold() {
        return gold.intValue();
    }

    public SimpleIntegerProperty getGoldProperty() {
        return gold;
    }

    public int getXp() {
        return xp.intValue();
    }

    public SimpleIntegerProperty getXpProperty() {
        return xp;
    }

    public ArrayList<Item> getBoughItems() {
        return boughItems;
    }

    public void clearBoughtItem() {
        boughItems.clear();
    }

    public void pickUpXp(int newXp) {
        int xpInt = xp.intValue();
        xp.set(xpInt + newXp);
    }

    public Boolean getInCampfireRadius() {
        return inCampfireRadius;
    }

    public void setInCampfireRadius(Boolean val) {
        inCampfireRadius = val;
    }

    public int getRandomValueInRange(ArrayList<Integer> range) {
        Random r = new Random();
        return r.nextInt(range.get(1) - range.get(0) + 1) + range.get(0);
    }

    public int getCriticalDamage() {
        return getRandomValueInRange(CRITICAL_DAMAGE_RANGE);
    }
}
