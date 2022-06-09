package unsw.loopmania;

public class GoalCycles implements GoalNode {

    private int cycles;

    public GoalCycles(int cycles) {
        this.cycles = cycles;
    }

    @Override
    public boolean isSatisfied(Goal goal) {
        if (goal.getCycles() >= cycles) {
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
        
        return cycles + " cycles";
    }
}
