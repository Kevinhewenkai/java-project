package unsw.loopmania;

public class Ally extends MovingEntity {

    private Stats stats;
    
    public Ally(PathPosition position, Stats stats) {
        super(position);
        this.stats = stats;
    }

    public Stats getStats() {
        return stats;
    }

    // public void setStats(Stats stats) {
    //     this.stats = stats;
    // }

    
}
