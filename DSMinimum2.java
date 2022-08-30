import java.util.ArrayList;
import java.util.List;

public class DSMinimum2 {

    float[] values;
    int valueCount;

    //TREE FINISHED WITH INDEXXX
    int[] tree;


    int[] indexToKey;
    int[] keyToIndex;
    int maxKey;

    //false = root is minimum value, true = root is maximum value
    boolean getMaximum = false;

    public DSMinimum2(boolean isMax, List<Integer> keys) {
        maxKey = keys.get(0);
        for (int i = 1; i < keys.size(); i++) {
            maxKey = Math.max(maxKey, keys.get(i));
        }
        initialize(isMax, keys, maxKey);
    }

    public DSMinimum2(boolean isMax, List<Integer> keys, int maxKey){
        initialize(isMax, keys, maxKey);
    }

    private void initialize(boolean isMax, List<Integer> keys, int maxKey){
        getMaximum = isMax;

        valueCount = keys.size();
        tree = new int[valueCount*2-1];
        values = new float[valueCount];

        indexToKey = new int[valueCount];
        keyToIndex = new int[maxKey+1];

        this.maxKey = maxKey;


        for (int i = 0; i < keys.size(); i++) {
            indexToKey[i] = keys.get(i);
            keyToIndex[keys.get(i)] = i;
            tree[valueCount-1+i] = i;
        }
    }

    private void bubbleUp(int current){
        //System.out.println("DSMINIMUM2 CALL BUBBLEUP" + current);
        //System.out.println(testReport()[4]);
        if(tree.length == 1) return;

        int left = current*2+1;
        int right = left+1;

        if(getMaximum == !(compareValues(tree[left], tree[right]) < 0)){
            //System.out.println("left is more");
            //if(tree[current] != tree[left]){
                tree[current] = tree[left];
                if(current > 0) bubbleUp((current-1)/2);
            //}
        }
        else{
            //System.out.println("right is more");
            //if(tree[current] != tree[right]){
                tree[current] = tree[right];
                if(current > 0) bubbleUp((current-1)/2);
            //}
        }
    }

    public void updateAllValues(List<Float> newValues){
        for (int i = 0; i < valueCount; i++) {
            values[i] = newValues.get(i);
        }

        refresh();
    }

    public void refresh(){
        for (int current = valueCount-2; current >= 0; current--) {
            int left = current*2+1;
            int right = left+1;
            if(getMaximum == !(compareValues(tree[left], tree[right]) < 0)){
                tree[current] = tree[left];
            }else{
                tree[current] = tree[right];
            }
        }
    }

    public void updateKey(int key, float value){
        int index = keyToIndex[key];
        float oldvalue = values[index];
        values[index] = value;
        if(getRootKey() == key){
            if(getMaximum && value > oldvalue) return;
            else if(!getMaximum && value < oldvalue) return;
        }
        bubbleUp( (index+valueCount-2)/2 );
    }

    public int getRootKey(){
        return indexToKey[tree[0]];
    }

    public float getRootValue(){
        return values[tree[0]];
    }

    public MoveValuePair getMVPair() {
        return new MoveValuePair(indexToKey[tree[0]], values[tree[0]]);
    }


    public int compareValues(int index1, int index2){
        //MoveValuePair mvp1 = new MoveValuePair(indexToKey[index1], values[index1]);
        //MoveValuePair mvp2 = new MoveValuePair(indexToKey[index2], values[index2]);
        //return mvp1.compareTo(mvp2);

        float diff = values[index1]-values[index2];
        //i should use an epsilon but no :) this is a bit faster ig than calling some outside epsilon
        if(diff > 0.0000001f) return 1;
        if(diff < -0.0000001f) return -1;

        //this mimics the exact behavior of the compare  function prior to compareTo update where -1 was not made
        //equal to infinity...
        //moveID is equal to key
        //System.out.println("DSMinimum2 compare move order " + indexToKey[index2] + " " + indexToKey[index1]);
        //if(values[index1] > 10000) return indexToKey[index2] - indexToKey[index1];
        //else return (diff > 0) ? 1 : -1;

        //...but this code should be pretty much the same thing and a little bit faster

        return indexToKey[index2] - indexToKey[index1];
    }
    public int compareKeys(int key1, int key2){
        return compareValues(keyToIndex[key1], keyToIndex[key2]);
    }


    public String[] testReport(){
        String valuesLine = "";
        for (int i = 0; i < values.length; i++) {
            valuesLine += "[(" + i + ") " + values[i] + "] ";
        }
        List<Float> reportIndexToKey = new ArrayList<>();
        for (float i : indexToKey) {
            reportIndexToKey.add(i);
        }
        String keyToIndexLine = "";
        for (int i = 0; i < keyToIndex.length; i++) {
            keyToIndexLine += "[" + i + ": " + keyToIndex[i] + "] ";
        }
        String treeLine = "";
        for (int i = 0; i < tree.length; i++) {
            treeLine += "[" + i + ": " + tree[i] + "] ";
        }

        String[] report = new String[7];
        report[0] = "DSMINIMUM2 TEST REPORT";
        report[1] = "values: " + valuesLine;
        report[2] = "indexToKey: " + reportIndexToKey;
        report[3] = "keyToIndex: " + keyToIndexLine;
        report[4] = "tree: " + treeLine;
        report[5] = "queryres: " + getMVPair();
        report[6] = "chosen index: " + keyToIndex[getMVPair().moveID];
        return report;
    }
}
