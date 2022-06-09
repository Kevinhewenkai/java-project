package unsw.loopmania;

public class GoalBosses implements GoalNode {
    
    private int bosses;

    public GoalBosses(int bosses) {
        this.bosses = bosses;
    }
    
    @Override
    public boolean isSatisfied(Goal goal) {
        if (goal.getBosses() >= bosses) {
            return true;
        }
        return false;
    }

    @Override
    public int addGoalNode(GoalNode goalNode) {
        return -1;
    }

    @Override
    public GoalNode getChild(int index) {
        return null;
    }

    @Override
    public String prettyPrint() {
        
        return bosses + " bosses";
    }
}
