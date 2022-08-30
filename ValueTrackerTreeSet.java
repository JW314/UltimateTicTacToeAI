import java.util.*;

public class ValueTrackerTreeSet {

    TreeSet<MoveValuePair> valueTracker;

    public ValueTrackerTreeSet(){
        valueTracker = new TreeSet<>();
    }
    public void addValue(int moveID, int val){
        valueTracker.add(new MoveValuePair(moveID, val));
    }

}
