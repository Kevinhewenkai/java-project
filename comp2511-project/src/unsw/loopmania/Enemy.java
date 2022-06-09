package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Enemy sub-class.
 */
public class Enemy extends MovingEntity {
    
    private boolean inCampfireRadius = false;
    private boolean moveUp = false; 
    private int battleRadius;
    private int supportRadius;
    private ArrayList<Integer> criticalDamageRange;
    private ArrayList<Integer> goldRange;
    private List<String> rareItemSettings;
    private int maxHp;
    
    public Enemy(PathPosition position, List<String> rareItemSettings) {
        super(position);
        this.rareItemSettings = rareItemSettings;
    }


    public int getMaxHp() {
        return maxHp;
    }


    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }


    public List<String> getRareItemSettings() {
        return rareItemSettings;
    }


    public void setRareItemSettings(List<String> rareItemSettings) {
        this.rareItemSettings = rareItemSettings;
    }


    public boolean getMoveUp() {
        return moveUp;
    }

    public void setMoveUp(boolean moveUp) {
        this.moveUp = moveUp;
    }

    public void setCriticalDamageRange(int min, int max) {
        criticalDamageRange = new ArrayList<>(Arrays.asList(min, max));
    }

    public void setGoldRange(int min, int max) {
        goldRange = new ArrayList<>(Arrays.asList(min, max));
    }
    /**
     * Move the enemy in a randomised fashion.
     */
    public void move() {

    }

    public boolean getInCampfireRadius() {
        return inCampfireRadius;
    }

    public void setInCampfireRadius(boolean inCampfireRadius) {
        this.inCampfireRadius = inCampfireRadius;
    }

    public List<String> getDrop() {
        return new ArrayList<String>();
    }

    public int getGold() {
        return getRandomValueInRange(goldRange);
    }

    public void setBattleRadius(int battleRadius) {
        this.battleRadius = battleRadius;
    }

    public void setSupportRadius(int supportRadius) {
        this.supportRadius = supportRadius;
    }

    public int getBattleRadius() {
        return battleRadius;
    }

    public int getSupportRadius() {
        return supportRadius;
    }

    public void reverseDirection() {
        moveUp = !moveUp;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Enemy)) {
            return false;
        }
        
        Enemy e = (Enemy) o;
        
        return x().equals(e.x()) 
            && y().equals(e.y()) && moveUp == e.getMoveUp() 
            && inCampfireRadius == e.getInCampfireRadius();
    }

    /**
     * In range specified by arraylist, return a random integer.
     * @param range
     * @return
     */
    public int getRandomValueInRange(ArrayList<Integer> range) {
        Random r = new Random();
        return r.nextInt(range.get(1) - range.get(0) + 1) + range.get(0);
    }

    public int getCriticalDamage() {
        return getRandomValueInRange(criticalDamageRange);
    }

    public String getType() {
        return "";
    }

    public void doubleRareChance() {}
    public void heal() {
        Stats enemyStats = getStats();
        enemyStats.setHp(maxHp);
    }
}
