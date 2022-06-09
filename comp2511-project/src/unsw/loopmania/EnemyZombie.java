package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class EnemyZombie extends Enemy {

    private static final ArrayList<Integer> HP_RANGE = new ArrayList<Integer>(Arrays.asList(25, 35)); 
    private static final ArrayList<Integer> SPEED_RANGE = new ArrayList<Integer>(Arrays.asList(1, 2)); 
    private static final ArrayList<Integer> DAMAGE_RANGE = new ArrayList<Integer>(Arrays.asList(15, 25)); 
    private static final int CRITICAL_CHANCE = 20;
    private static final int BATTLE_RADIUS = 3;
    private static final int SUPPORT_RADIUS = 2;
    private int RARE_ITEM_CHANCE = 4;
    private static final ArrayList<Integer> GOLD_RANGE = new ArrayList<Integer>(Arrays.asList(30, 50)); 
    private Map<String, Integer> lootTable = new LinkedHashMap<String, Integer>();
    private static final int ITEM_DROP_CHANCE = 50;

    public EnemyZombie(PathPosition position, List<String> rareItemSettings) {
        super(position, rareItemSettings);
        
        Stats zombieStats = getStats();
        initialiseLootTable();
        setMaxHp(getRandomValueInRange(HP_RANGE));
        zombieStats.setHp(getMaxHp());
        zombieStats.setDamage(getRandomValueInRange(DAMAGE_RANGE));
        zombieStats.setSpeed(getRandomValueInRange(SPEED_RANGE));
        zombieStats.setCriticalChance(CRITICAL_CHANCE);
        setStats(zombieStats);

        setBattleRadius(BATTLE_RADIUS);
        setSupportRadius(SUPPORT_RADIUS);

        setGoldRange(GOLD_RANGE.get(0), GOLD_RANGE.get(1));
    }

    @Override
    public void move() {
        // moves in a random direction... 25% chance up or down, 50% chance not at all...
        int directionChoice = (new Random()).nextInt(4);
        if (directionChoice == 0){ 
            moveUpPath();
        }
        else if (directionChoice == 1){
            moveDownPath();
        }
    }

    @Override
    public List<String> getDrop() {
         
        List<String> drops = new ArrayList<String>();

        // Generate random number between 1 and 100. And return random item from loot table.
        Random r = new Random();
        if (r.nextInt(100) < ITEM_DROP_CHANCE) {
            drops.add(randomItem(r.nextInt(100)));
        }

        if (getRareItemSettings().contains("DenimShorts") && r.nextInt(100) < RARE_ITEM_CHANCE) {
            drops.add("DenimShorts");
        }
        
        return drops;
    }

    /**
     * Initialise lootTable with percentage chance of an item or card being dropped.
     */
    private void initialiseLootTable() {
        lootTable.put("Village", 5);
        lootTable.put("Barracks", 5);
        lootTable.put("HealthPotion", 1);
        lootTable.put("Destroyer", 5);
        lootTable.put("Campfire", 4);
        lootTable.put("VampireCastle", 5);
        lootTable.put("Tower", 5);
        lootTable.put("Trap", 10);
        lootTable.put("Shield", 10);
        lootTable.put("Helmet", 10);
        lootTable.put("Sword", 10);
        lootTable.put("Stake", 10);
        lootTable.put("Staff", 10);
        lootTable.put("Armour", 10);
    }

    /**
     * Returns static entity object that is the result of a random number being generated between 1 and 100 (value).
     * @param value
     * @return 
     */
    private String randomItem(int seed) {
        int range = 0;

        for (Entry<String, Integer> entry: lootTable.entrySet()) {
            range+= entry.getValue();

            if (seed < range) {
                return entry.getKey();
            }
        }

        return "VampireCastle";
    }

    @Override
    public String getType() {
        return "Zombie";
    }

    @Override
    public void doubleRareChance() {
        RARE_ITEM_CHANCE = 2*RARE_ITEM_CHANCE;
    }
}
