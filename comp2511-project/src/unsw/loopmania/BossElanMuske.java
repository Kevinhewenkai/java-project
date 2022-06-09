package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BossElanMuske extends Boss {

    private static final ArrayList<Integer> HP_RANGE = new ArrayList<Integer>(Arrays.asList(250, 350)); 
    private static final ArrayList<Integer> SPEED_RANGE = new ArrayList<Integer>(Arrays.asList(18, 22)); 
    private static final ArrayList<Integer> DAMAGE_RANGE = new ArrayList<Integer>(Arrays.asList(125, 175)); 
    private static final int BATTLE_RADIUS = 2;
    private static final int SUPPORT_RADIUS = 2;
    private static final ArrayList<Integer> GOLD_RANGE = new ArrayList<Integer>(Arrays.asList(4500, 5000)); 

    public BossElanMuske(PathPosition position, List<String> rareItemSettings) {
        super(position, rareItemSettings);
        Stats elanMuskeStats = new Stats();
        setMaxHp(getRandomValueInRange(HP_RANGE));
        elanMuskeStats.setHp(getMaxHp());
        elanMuskeStats.setDamage(getRandomValueInRange(DAMAGE_RANGE));
        elanMuskeStats.setSpeed(getRandomValueInRange(SPEED_RANGE));
        setStats(elanMuskeStats);
        setBattleRadius(BATTLE_RADIUS);
        setSupportRadius(SUPPORT_RADIUS);
        setGoldRange(GOLD_RANGE.get(0), GOLD_RANGE.get(1));
    }
    
    @Override
    public void move() {
        Random r = new Random();

        int value = r.nextInt(10);
        if (value == 0) {
            moveUpPath();
        } else if (value == 1) {
            moveDownPath();
        }
    }

    @Override
    public List<String> getDrop() {
        
        List<String> drops = new ArrayList<String>();
        for (int i = 5; i > 0; i--) {
            drops.add("HealthPotion");
        }

        drops.add("VampireCastle");

        return drops;
    }

    @Override
    public String getType() {
        return "Elan Muske";
    }
}
