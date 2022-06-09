package unsw.loopmania;

public class GoalXp implements GoalNode {

    private int xp;

    public GoalXp(int xp) {
        this.xp = xp;
    }

    @Override
    public boolean isSatisfied(Goal goal) {
        if (goal.getXp() >= xp) {
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
        
        return xp + " xp";
    }
}
