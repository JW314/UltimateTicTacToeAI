public class RaveTracker {

    class RaveInfo{
        float[] values;
        int[] weights;
        int weightsum;

        public RaveInfo(int numMoves){
            values = new float[numMoves];
            weights = new int[numMoves];
            weightsum = 0;
        }
        public void incrementValue(int move, float newvalue, float newweight){
            float sum = values[move] * weights[move];
            sum += newvalue * newweight;
            weights[move] += newweight;
            values[move] = sum / weights[move];
            weightsum += newweight;
        }
        public void reduceWeights(float factor){
            int newsum = 0;
            for(int i = 0; i < weights.length; i++){
                int newweight = Math.round(weights[i]*factor);
                newsum += factor;
                weights[i] = newweight;
            }
            weightsum = newsum;
        }
    }

    RaveInfo[] raveInfos;
    float tieValue;


    public RaveTracker(int numMoves, float tie){
        raveInfos = new RaveInfo[2];
        raveInfos[0] = new RaveInfo(numMoves);
        raveInfos[1] = new RaveInfo(numMoves);
        tieValue = tie;
    }

    public void reduceWeights(float factor){
        raveInfos[0].reduceWeights(factor);
        raveInfos[1].reduceWeights(factor);
    }

    public float getValue(int player, int moveID){
        return raveInfos[(player-1)].values[moveID];
    }

    public void testPrint(){
        for(int p = 0; p < 2; p++){
            float[] vals = raveInfos[p].values;
            int[] weights = raveInfos[p].weights;
            String s1 = "RAVETRACKER VALS: ";
            String s2 = "RAVETRACKER WEIGHTS: ";
            for(int i = 0; i < vals.length; i++){
                s1 += "m" + i + " " + vals[i] + " ";
                s2 += "m" + i + " " + weights[i] + " ";
            }
            System.out.println("RAVE TRACKER PLAYER " + ((p == 0) ? "ONE" : "TWO"));
            System.out.println(s1);
            System.out.println(s2);
        }

    }
}
