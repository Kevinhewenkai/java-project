package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class EnemyVampire extends Enemy {

    private static final ArrayList<Integer> HP_RANGE = new ArrayList<Integer>(Arrays.asList(80, 120)); 
    private static final ArrayList<Integer> SPEED_RANGE = new ArrayList<Integer>(Arrays.asList(5, 10)); 
    private static final ArrayList<Integer> DAMAGE_RANGE = new ArrayList<Integer>(Arrays.asList(40, 60)); 
    private static final int BATTLE_RADIUS = 5;
    private static final int SUPPORT_RADIUS = 7;
    private int RARE_ITEM_CHANCE = 50;
    private static final int CRITICAL_CHANCE = 33;
    private static final ArrayList<Integer> CRITICAL_DAMAGE_RANGE = new ArrayList<Integer>(Arrays.asList(20, 35));
    private static final ArrayList<Integer> GOLD_RANGE = new ArrayList<Integer>(Arrays.asList(100, 300)); 
    private Map<String, Integer> cardLootTable = new LinkedHashMap<String, Integer>();
    private Map<String, Integer> itemLootTable = new LinkedHashMap<String, Integer>();
    private static final int ITEM_DROP_CHANCE = 60;

    public EnemyVampire(PathPosition position, List<String> rareItemSettings) {
        super(position, rareItemSettings);
        initialiseCardLootTable();
        initialiseItemLootTable();
        
        Stats vampireStats = getStats();
        vampireStats.setDamage(getRandomValueInRange(DAMAGE_RANGE));
        vampireStats.setSpeed(getRandomValueInRange(SPEED_RANGE));
        vampireStats.setCriticalChance(CRITICAL_CHANCE);
        setMaxHp(getRandomValueInRange(HP_RANGE));
        vampireStats.setHp(getMaxHp());
        setStats(vampireStats);
        
        setMoveUp(true);
        setBattleRadius(BATTLE_RADIUS);
        setSupportRadius(SUPPORT_RADIUS);
        setCriticalDamageRange(CRITICAL_DAMAGE_RANGE.get(0), CRITICAL_DAMAGE_RANGE.get(1));

        setGoldRange(GOLD_RANGE.get(0), GOLD_RANGE.get(1));
    }

    @Override
    public void move() {
        // Vampire moves anticlockwise, unless in campfire radius, where it continually moves clockwise.
        // If vampire is in campfire radius, then it must move clockwise.

        // If Vampire is in campfire radius in loopmania world, inCampFireRadius is set to true.
        // If vampire was in campfire radius, and is no longer, then the direction is reversed in loopmania world.
        if (getInCampfireRadius()) {
            if (getMoveUp() == true) {
                moveDownPath();
            } else {
                moveUpPath();
            }
        } else {
            if (getMoveUp() == true) {
                moveUpPath();
            } else {
                moveDownPath();
            }
        }
    }

    @Override
    public List<String> getDrop() {
        // Initalise tables with percentage drops.
        
        List<String> drops = new ArrayList<>();
        
        // Add item and card to drops list.
        Random r = new Random();
        if (r.nextInt(100) < ITEM_DROP_CHANCE) {
            drops.add(randomItem(r.nextInt(100)));
        }

        if (r.nextInt(100) < ITEM_DROP_CHANCE) {
            drops.add(randomCard(r.nextInt(100)));
        }
        
        // Add rare item.
        if (getRareItemSettings().contains("Gameshark") && r.nextInt(100) < RARE_ITEM_CHANCE) {
            drops.add("Gameshark");
        }

        if (getRareItemSettings().contains("Anduril") && r.nextInt(100) < RARE_ITEM_CHANCE) {
            drops.add("Anduril");
        }
        return drops;
    }

    /**
     * Initialises itemLootTable with distribution of percentages for items.
     */
    private void initialiseItemLootTable() {
        itemLootTable.put("Sword", 15);
        itemLootTable.put("HealthPotion", 15);
        itemLootTable.put("Helmet", 15);
        itemLootTable.put("Armour", 15);
        itemLootTable.put("Stake", 15);
        itemLootTable.put("Anduril", 10);
        itemLootTable.put("Shield", 15);
    }

    /**
     * Initialises cardLootTable with distribution of percentages for cards.
     */
    private void initialiseCardLootTable() {        
        cardLootTable.put("Village", 25);
        cardLootTable.put("Barracks", 25);
        cardLootTable.put("Campfire", 25);
        cardLootTable.put("Tower", 25);
    }

    /**
     * Given a seed, a random item from the itemLootTable is returned.
     * @param seed
     * @return
     */
    private String randomItem(int seed) {
        int range = 0;

        for (Entry<String, Integer> entry: itemLootTable.entrySet()) {
            range+= entry.getValue();

            if (seed < range) {
                return entry.getKey();
            }
        }

        return "Sword";
    }

    /**
     * Given a seed, a random card from the cardLootTable is returned.
     * @param seed
     * @return
     */
    private String randomCard(int seed) {
        int range = 0;


        for (Entry<String, Integer> entry: cardLootTable.entrySet()) {
            range+= entry.getValue();

            if (seed < range) {
                return entry.getKey();
            }
        }

        return "VampireCastle";
    }

    @Override
    public String getType() {
        return "Vampire";
    }

    public int getCriticalChance() {
        return CRITICAL_CHANCE;
    }

    @Override
    public void doubleRareChance() {
        RARE_ITEM_CHANCE = 2*RARE_ITEM_CHANCE;
    };
}
