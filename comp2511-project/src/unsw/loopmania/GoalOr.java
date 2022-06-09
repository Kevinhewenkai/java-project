package unsw.loopmania;


public class GoalOr implements GoalNode {

    private GoalNode left;
    private GoalNode right;

    public GoalOr() {
        left = null;
        right = null;
    }

    @Override
    public boolean isSatisfied(Goal goal) {
        if (left == null || right == null) {
            return false;
        }

        if (left.isSatisfied(goal) || right.isSatisfied(goal)) {
            return true;
        }
        return false;
    }

    @Override
    public GoalNode getChild(int index) {
        if (index == 0) {
            return left;
        } else if (index == 1) {
            return right;
        }
        return null;
    }

    @Override
    public int addGoalNode(GoalNode goalNode) {
        int index = -1;
        if (left == null) {
            left = goalNode;
            index = 0;
        } else if (right == null) {
            right = goalNode;
            return 1;
        }
        return index;
    }

    @Override
    public String prettyPrint() {
        
        return "-- " + left.prettyPrint() +"\n\tor\n" + "-- " + right.prettyPrint();
    }
}
