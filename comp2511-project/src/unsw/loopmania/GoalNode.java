package unsw.loopmania;


public interface GoalNode {
    
    public boolean isSatisfied(Goal goal);
    public int addGoalNode(GoalNode goalNode);
    public GoalNode getChild(int index);

    // Pretty print
    public String prettyPrint();
}
