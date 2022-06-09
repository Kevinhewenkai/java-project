package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class EnemySlug extends Enemy {

    private static final int BATTLE_RADIUS = 2;
    private static final int SUPPORT_RADIUS = 2;
    private static final ArrayList<Integer> HP_RANGE = new ArrayList<Integer>(Arrays.asList(1, 5)); 
    private static final ArrayList<Integer> SPEED_RANGE = new ArrayList<Integer>(Arrays.asList(2, 4)); 
    private static final ArrayList<Integer> DAMAGE_RANGE = new ArrayList<Integer>(Arrays.asList(5, 10)); 
    private static final ArrayList<Integer> GOLD_RANGE = new ArrayList<Integer>(Arrays.asList(5, 10)); 
    private LinkedHashMap<String, Integer> lootTable;
    private int RARE_ITEM_CHANCE = 2;
    private static final int ITEM_DROP_CHANCE = 60;

    public EnemySlug(PathPosition position, List<String> rareItemSettings) {
        super(position, rareItemSettings);
        lootTable = initialiseLootTable();
        Stats slugStats = getStats();
        setMaxHp(getRandomValueInRange(HP_RANGE));
        slugStats.setHp(getMaxHp());
        slugStats.setDamage(getRandomValueInRange(DAMAGE_RANGE));
        slugStats.setSpeed(getRandomValueInRange(SPEED_RANGE));
        setStats(slugStats);
        
        setSupportRadius(SUPPORT_RADIUS);
        setBattleRadius(BATTLE_RADIUS);
        setCriticalDamageRange(0, 0);

        setGoldRange(GOLD_RANGE.get(0), GOLD_RANGE.get(1));
    }

    @Override
    public void move() {
        // moves in a random direction... 33% chance up or down, 33% chance not at all...
        int directionChoice = (new Random()).nextInt(3);
        if (directionChoice == 0) { 
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
        
        if (getRareItemSettings().contains("OneRing") && r.nextInt(100) < RARE_ITEM_CHANCE) {
            drops.add("OneRing");
        }

        if (getRareItemSettings().contains("TreeStump") && r.nextInt(100) < RARE_ITEM_CHANCE) {
            drops.add("TreeStump");
        }
        
        return drops;
    }

    /**
     * Adds percentage chances of acquiring a card or item to loot table.
     */
    private LinkedHashMap<String, Integer> initialiseLootTable() {
        LinkedHashMap<String, Integer> table = new LinkedHashMap<String, Integer>();
        table.put("Sword", 15);
        table.put("Staff", 5);
        table.put("Armour", 5);
        table.put("Village", 5);
        table.put("Tower", 5);
        table.put("Trap", 20);
        table.put("ZombiePit", 20);
        table.put("Shield", 6);
        table.put("Helmet", 10);
        table.put("Destroyer", 3);
        table.put("HealthPotion", 1);
        table.put("Stake", 5);
        return table;
    }

    /**
     * Returns static entity object that is the result of a random number being generated between 1 and 100 (value).
     * @param value
     * @return 
     */
    private String randomItem(int value) {
        int range = 0;
        Set<Entry<String, Integer>> entrySet = lootTable.entrySet();

        
        for (Entry<String, Integer> entry: entrySet) {
            range += entry.getValue();
            
            if (value < range) {
                return entry.getKey();
            }
        }
        
        return "VampireCastle";
    }

    @Override
    public String getType() {
        return "Slug";
    }

    @Override
    public void doubleRareChance() {
        RARE_ITEM_CHANCE = 2*RARE_ITEM_CHANCE;
    };

}
