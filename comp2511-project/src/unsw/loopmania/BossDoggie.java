package unsw.loopmania;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class BossDoggie extends Boss {
    
    private static final ArrayList<Integer> HP_RANGE = new ArrayList<Integer>(Arrays.asList(400, 600)); 
    private static final ArrayList<Integer> SPEED_RANGE = new ArrayList<Integer>(Arrays.asList(4, 5)); 
    private static final ArrayList<Integer> DAMAGE_RANGE = new ArrayList<Integer>(Arrays.asList(20, 30)); 
    private static final int BATTLE_RADIUS = 2;
    private static final int SUPPORT_RADIUS = 2;
    private static final ArrayList<Integer> GOLD_RANGE = new ArrayList<Integer>(Arrays.asList(100, 110)); 
    private static final int STUN_CHANCE = 5;
    private int straightMoves = 1;

    public BossDoggie(PathPosition position, List<String> rareItemSettings) {
        super(position, rareItemSettings);
        Stats doggieStats = new Stats();
        setMaxHp(getRandomValueInRange(HP_RANGE));
        doggieStats.setHp(getMaxHp());
        doggieStats.setDamage(getRandomValueInRange(DAMAGE_RANGE));
        doggieStats.setSpeed(getRandomValueInRange(SPEED_RANGE));
        doggieStats.setCriticalChance(STUN_CHANCE);
        setStats(doggieStats);
        setBattleRadius(BATTLE_RADIUS);
        setSupportRadius(SUPPORT_RADIUS);
        setGoldRange(GOLD_RANGE.get(0), GOLD_RANGE.get(1));
    }

    @Override
    public List<String> getDrop() {
        List<String> drops = new ArrayList<>();
        for (int i = 3; i > 0; i--) {
            drops.add("HealthPotion");
        }
        
        drops.add("DoggieCoin");
        return drops;
    }


    @Override
    public void move() {
        
        if (straightMoves <= 0) {
            Random r = new Random();

            straightMoves = r.nextInt(10);
            reverseDirection();
        }

        if (getMoveUp()) {
            moveUpPath();
        } else {
            moveDownPath();
        }
        straightMoves--;

    }

    @Override
    public String getType() {
        return "Doggie";
    }
}
