import java.util.*;

public class HeuristicManager {

    TixTaxBoard currentBoard;
    float tiePoints;

    //constants based on 3x3 board combos
    int[][] rowList;
    float[][] plopTemperatures; //one temperature for each line and player
    float plopSum; //perspective of X (positive means X is better, negative means O is better)
    float absoluteTemperatureSum;
    List<List<Integer>> linesByCell;


    /**
     * HOW ALGORITHM AND NET REWARDS WORKS
     * Look at each of the 8 possible global winning conditions
     * Count how close each player is to winning in each
     * Use that info to identify which boxes are important to focus on
     *
     * Net reward gives the values of each box: [player (x or o)][box #]
     * Example [0][3][1]
     *
     */


    public HeuristicManager(){
        initialize(0.5f);
    }

    public void setBoard(TixTaxBoard board){
        currentBoard = board;
    }

    public void initialize(float tp){
        rowList = new int[8][3];
        rowList[0] = new int[]{0, 1, 2};
        rowList[1] = new int[]{3, 4, 5};
        rowList[2] = new int[]{6, 7, 8};
        rowList[3] = new int[]{0, 3, 6};
        rowList[4] = new int[]{1, 4, 7};
        rowList[5] = new int[]{2, 5, 8};
        rowList[6] = new int[]{0, 4, 8};
        rowList[7] = new int[]{2, 4, 6};
        linesByCell = new ArrayList<>();
        for (int i = 0; i < 9; i++) linesByCell.add(new ArrayList<>());
        for (int i = 0; i < rowList.length; i++) {
            for (int j = 0; j < rowList[i].length; j++) {
                linesByCell.get(rowList[i][j]).add(i);
            }
        }

        plopTemperatures = new float[8][2];
        plopSum = 0;
        absoluteTemperatureSum = 0;
        if(currentBoard != null) getPositionalValue(1);
    }

    public void playMove(int player, int moveID){

    }

    public float getMoveValue(int player, int moveID){

        //LOP ALGORITHM
        int boxnum = moveID / 9;
        int cellnum = moveID % 9;

        //calculate how this move would change local box win probabilities

        float[] distBefore = TixTaxInfo.getLOPEvenTies(currentBoard.getBoardID(boxnum));
        int newbox = currentBoard.getBoardID(boxnum) + TixTaxInfo.simplePowersOf3[cellnum] * player;
        float[] distAfter = TixTaxInfo.getLOPEvenTies(newbox);
        float xChange;
        xChange = distAfter[0] - distBefore[0];

        float heuristic = xChange;

        //we solve the heuristic for X: to get heuristic from O perspective, multiply by neg 1
        if(player == 2) heuristic *= -1;
        return heuristic;
    }

    /**
     * player means perspective: positive means good for that player, negative means bad for that player
     */
    public float getPositionalValue(int player){
        //PLOP ALGORITHM
        for (int line = 0; line < 8; line++) {
            float[] lop0 = TixTaxInfo.getLOPAll(currentBoard.getBoardID(rowList[line][0]));
            float[] lop1 = TixTaxInfo.getLOPAll(currentBoard.getBoardID(rowList[line][1]));
            float[] lop2 = TixTaxInfo.getLOPAll(currentBoard.getBoardID(rowList[line][2]));
            for (int pl = 0; pl < 2; pl++) {
                plopSum -= plopTemperatures[line][pl] * (pl == 0 ? 1 : -1);
                absoluteTemperatureSum -= plopTemperatures[line][pl];

                plopTemperatures[line][pl] = lop0[pl] * lop1[pl] * lop2[pl];;

                plopSum += plopTemperatures[line][pl] * (pl == 0 ? 1 : -1);
                absoluteTemperatureSum += plopTemperatures[line][pl];
            }
        }

        float heuristic = plopSum;

        if(TixTaxInfo.breakTiesWithCount){
            //if lines seem dead (tie on total board) due to low total temperature, pay attention to tiebreaker
            float deadLinesPoint = 0.8f;
            if(absoluteTemperatureSum < deadLinesPoint){
                float lopSumDifference = 0; //perspective of X
                for (int box = 0; box < 9; box++) {
                    float[] lop = TixTaxInfo.getLOPAll(currentBoard.getBoardID(box));
                    lopSumDifference += lop[0] - lop[1];
                }

                heuristic += 0.5 * lopSumDifference * (deadLinesPoint - absoluteTemperatureSum) / deadLinesPoint;
            }
        }

        return (player == 1 ? 1 : -1) * heuristic;
    }
}
