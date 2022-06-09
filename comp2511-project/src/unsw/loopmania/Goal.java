package unsw.loopmania;

public class Goal {
    private int cycles;
    private int xp;
    private int gold;
    private int bosses;

    public Goal() {
        cycles = 0;
        xp = 0;
        gold = 0;
        bosses = 0;
    }

    public void addGoal(Goal otherGoal) {
        cycles += otherGoal.getCycles();
        xp += otherGoal.getXp();
        gold += otherGoal.getGold();
        bosses += otherGoal.getBosses();
    }

    public int getCycles() {
        return cycles;
    }
    
    public int getXp() {
        return xp;
    }

    public int getGold() {
        return gold;
    }

    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getBosses() {
        return bosses;
    }

    public void setBosses(int bosses) {
        this.bosses = bosses;
    }
}

