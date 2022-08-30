import java.util.*;

/**
 * Data Structure that keeps track of the minimum of a set of values (that also have keys)
 *
 */


public class DSMinimum1 {


    static class Node{
        float value;
        int key;
        public Node(float v, int k){
            value = v; key = k;
        }
    }
    Node[] tree;
    int valueCount;

    int[] indexToKey;
    int[] keyToIndex;
    int maxKey;

    public DSMinimum1(List<Integer> keys){
        maxKey = keys.get(0);
        for (int i = 1; i < keys.size(); i++) {
            maxKey = Math.max(maxKey, keys.get(i));
        }
        valueCount = keys.size();
        tree = new Node[valueCount*2-1];

        indexToKey = new int[valueCount*2-1];
        keyToIndex = new int[maxKey+1];

        int index = valueCount-1;
        for (int i = 0; i < keys.size(); i++) {
            indexToKey[index+i] = keys.get(i);
            keyToIndex[keys.get(i)] = index+i;
        }
    }

    private void bubbleUp(int index){
        Node setTo;
        if(tree.length == 1) return;
        Node lc = tree[index*2+1];
        Node rc = tree[index*2+2];
        if(lc == null || rc == null){
            if(lc == null) setTo = rc;
            else setTo = lc;
        }else{
            if(lc.value < rc.value) setTo = lc;
            else setTo = rc;
        }
        if(setTo != tree[index]){
            tree[index] = setTo;
            if(index > 0) bubbleUp((index-1)/2);
        }
    }

    public void updateKey(int key, float value){
        int index = keyToIndex[key];
        tree[index] = new Node(value, key);
        bubbleUp( (index-1)/2 );
    }

    public int getMinKey(){
        return tree[0].key;
    }

    public float getMinValue(){
        return tree[0].value;
    }

    public MoveValuePair getMVPair() {
        Node min = tree[0];
        return new MoveValuePair(min.key, min.value);
    }

}
