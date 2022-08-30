import java.util.*;

public class DebuggerDSMinimum {
    public static void main(String[] args){

        int size = 6;
        long timeTree = 0;
        long timeArray = 0;

        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            keys.add(i*2);
        }

        DSMinimum1 minFinder = new DSMinimum1(keys);

        float[] verifier = new float[size];
        int veriCurMin = 0;
        for (int i = 0; i < size; i++) {
            timeArray -= System.currentTimeMillis();
            verifier[i] = 1f;
            timeArray += System.currentTimeMillis();

            timeTree -= System.currentTimeMillis();
            minFinder.updateKey(keys.get(i), 1);
            timeTree += System.currentTimeMillis();

        }

        Random rand = new Random(69);

        for (int i = 0; i < 100; i++) {
            float nextVal = rand.nextFloat();
            int nextKey = rand.nextInt(size);

            timeTree -= System.currentTimeMillis();
            minFinder.updateKey(keys.get(nextKey), nextVal);

            int minKey = minFinder.getMinKey();
            timeTree += System.currentTimeMillis();

            //check
            timeArray -= System.currentTimeMillis();

            float oldVal = verifier[nextKey];
            verifier[nextKey] = nextVal;

            int realMinKey = veriCurMin;
            float realMinValue = verifier[veriCurMin];

            if(veriCurMin == nextKey){
                if(nextVal < oldVal){
                    realMinKey = nextKey;
                    realMinValue = verifier[nextKey];
                }
                else{
                    for (int j = 0; j < size; j++) {
                        if(verifier[j] < realMinValue){
                            realMinKey = j;
                            realMinValue = verifier[j];
                        }
                    }
                }
            }else{
                if(nextVal < verifier[veriCurMin]){
                    realMinKey = nextKey;
                    realMinValue = verifier[nextKey];
                }
            }
            veriCurMin = realMinKey;
            timeArray += System.currentTimeMillis();

            if(realMinKey*2 != minKey) System.out.println("ERROR");
            System.out.println(minKey + " " + realMinKey);
        }
        System.out.println(timeTree);
        System.out.println(timeArray);
    }
}

