package unsw.loopmania;

import java.util.Random;

import javafx.beans.property.SimpleIntegerProperty;

public class Stats {

    private String itemName;
    private SimpleIntegerProperty speed = new SimpleIntegerProperty();
    private SimpleIntegerProperty hp = new SimpleIntegerProperty();
    private SimpleIntegerProperty damage = new SimpleIntegerProperty();
    private SimpleIntegerProperty defence = new SimpleIntegerProperty();
    private SimpleIntegerProperty scalarDefence = new SimpleIntegerProperty();
    private SimpleIntegerProperty value = new SimpleIntegerProperty();
    private SimpleIntegerProperty criticalChance = new SimpleIntegerProperty();
    private SimpleIntegerProperty criticalDamage = new SimpleIntegerProperty();

    public Stats() {
        this.speed.set(0);
        this.hp.set(0);
        this.damage.set(0);
        this.defence.set(0);
        this.value.set(0);
        this.criticalChance.set(0);
        this.criticalDamage.set(0);
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public String getItemName() {
        return this.itemName;
    }
    
    public void setCriticalDamage(int criticalDamage) {
        this.criticalDamage.set(criticalDamage);
    }

    public int getCriticalChance() {
        return criticalChance.intValue();
    }

    public void setCriticalChance(int criticalChance) {
        this.criticalChance.set(criticalChance);
    }

    public int getValue() {
        return value.intValue();
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    public int getScalarDefence() {
        return scalarDefence.intValue();
    }

    /**
     * Applies damage to hp stat
     * @param attackDamage
     */
    public void applyDamage(int attackDamage) {
        if (attackDamage > 0) {
            int currHp = hp.intValue();
            hp.set(currHp -= attackDamage);
        }
    }

    public int getDefence() {
        return defence.intValue();
    }

    public void setDefence(int defence) {
        this.defence.set(defence);
    }

    public SimpleIntegerProperty getDefenceProperty() {
        return defence;
    }

    public void setScalarDefence(int scalarDefence) {
        this.scalarDefence.set(scalarDefence);
    }

    public int getDamage() {
        return damage.intValue();
    }

    public SimpleIntegerProperty getDamageProperty() {
        return damage;
    }

    /**
     * Gets the result of the base damage and critical hit chance that is applied on top of it.
     * @return
     */
    public int getAttackDamage() {
        Random r = new Random();
        int attackDamage = damage.intValue();
        if (r.nextInt(100) < criticalChance.intValue()) {
            attackDamage += criticalDamage.intValue();
        }
        return attackDamage;
    }

    public void setDamage(int damage) {
        this.damage.set(damage);
    }

    public int getHp() {
        return hp.intValue();
    }

    public SimpleIntegerProperty getHpProperty() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp.set(hp);
    }


    public int getSpeed() {
        return speed.intValue();
    }
    
    public void setSpeed(int speed) {
        this.speed.set(speed);
    }

    /**
     * Given another set of stats, adds the stats as to formulate a team of entities with combined stats.
     * @param otherStats
     */
    public void addStats(Stats otherStats) {
        if (otherStats.getSpeed() > speed.intValue()) {
            speed.set(otherStats.getSpeed());
        }

        if (otherStats.getCriticalChance() > criticalChance.intValue()) {
            criticalChance.set(otherStats.getCriticalChance());
        }

        int hpInt = hp.intValue();
        hp.set(hpInt += otherStats.getHp());

        int damageInt = damage.intValue();
        damage.set(damageInt += otherStats.getDamage());

        int defenceInt = defence.intValue();
        defence.set(defenceInt += otherStats.getDefence());
        
        int scalarDefenceInt = scalarDefence.intValue();
        scalarDefence.set(scalarDefenceInt += otherStats.getScalarDefence());
        
        int valueInt = value.intValue();
        value.set(valueInt += otherStats.getValue());
    }

    public void subtractStats(Stats otherStats) {
        int speedInt = speed.intValue();
        speed.set(speedInt -= otherStats.getSpeed());

        int criticalChanceInt = criticalChance.intValue();
        criticalChance.set(criticalChanceInt -= otherStats.getCriticalChance());
        
        int hpInt = hp.intValue();
        hp.set(hpInt -= otherStats.getHp());

        int damageInt = damage.intValue();
        damage.set(damageInt -= otherStats.getDamage());

        int defenceInt = defence.intValue();
        defence.set(defenceInt -= otherStats.getDefence());

        int scalarDefenceInt = scalarDefence.intValue();
        scalarDefence.set(scalarDefenceInt -= otherStats.getScalarDefence());
        
        int valueInt = value.intValue();
        value.set(valueInt -= otherStats.getValue());
    }
}
