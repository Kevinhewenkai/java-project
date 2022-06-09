package unsw.loopmania;

public class GoalGold implements GoalNode {
    
    private int gold;

    public GoalGold(int gold) {
        this.gold = gold;
    }

    @Override
    public boolean isSatisfied(Goal goal) {
        if (goal.getGold() >= gold) {
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
        
        return gold + " gold";
    }
}
