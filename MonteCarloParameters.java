import java.lang.Math;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class MonteCarloParameters {

    Random rand;
    AIGameParameters gameParams;

    ReportParameters reportParams;

    boolean useOpeningBook = true;

    //AI will offer draw or resign when confidence is under respective thresholds
    float resignThreshold = -0.01f;
    float offerDrawThreshold = -0.01f;

    float infEval = 100000;

    boolean useTranspositionTable = true;
    int tableCapacity = 1048576;

    boolean usePriors = false;

    MCParamsRave rave = new MCParamsRave(false);
    MCParamsDomainSpec domain = new MCParamsDomainSpec();

    float tiePoints = 0.5f;

    int simulationsPerRound = 1;
    float explorationParameter = 1.4f;
    int recalcUCBCooldown = 5;
    int recalcBonusCooldown = 10;


    boolean testytesty = false;
    boolean usenewUCBTracker = true;
    boolean trackerCrossCheck = false;

    boolean useUCBBonuses;

    EMoveSupplyFilter moveSupplyFilter;

    /**
     * CONFIDENCE CALCLULATOR KEY:
     * 0 = Don't do any confidence shenanigans; true confidence = average confidence
     */

    int confCalculatorKey = 0;
    long confReqSims = 200;
    float maxB = 1f;

    float reqScore = 0f;
    float depthWeight = 0f;
    float confDiffWeight = 0f;

    boolean useWeightFunction = false;

    /** CONFIDENCE REGION END **/


    public float calculateUCBValue(MonteCarloTreeNode child, MonteCarloTreeNode parent, int moveID){
        if(child == null){
            //child is not yet initialized: no games
            //use node priors
            float confidence = parent.priorPoints[moveID]/parent.priorBase[moveID];
            return confNodeUCBFunction(confidence, parent.priorBase[moveID], parent.totalPrior);
        }
        float confidence;
        float childGames;
        float parentGames;

        if(confCalculatorKey != 0){
            confidence = 1 - child.trueConfidence;
            float points = confidence * child.totalGamesPlayed + parent.priorPoints[moveID];
            childGames = (child.totalGamesPlayed + parent.priorBase[moveID]);
            confidence = points/childGames;
            parentGames = parent.totalGamesPlayed + parent.totalPrior;
        }else{
            //CONF CALCULATOR KEY = 0; DEFAULT CONF
            float points = (child.totalGamesPlayed - child.totalPoints + parent.priorPoints[moveID]);
            childGames = (child.totalGamesPlayed + parent.priorBase[moveID]);
            confidence = points/childGames;
            parentGames = parent.totalGamesPlayed + parent.totalPrior;
            //if(parent.priorBase[moveID] < 0.01) System.out.println("MCPARAMS " + parent.priorPoints[moveID] + " " + parent.priorBase[moveID]);
        }
        //System.out.println( (1 - child.trueConfidence) + " vs " + ((child.gamesPlayed - child.points)/ (float)child.gamesPlayed) + " " + child.gamesPlayed);
        //float a = confNodeUCBFunction((1 - child.trueConfidence), child.gamesPlayed, parent.gamesPlayed);
        //float b = confNodeUCBFunction(((child.gamesPlayed - child.points)/ (float)child.gamesPlayed), child.gamesPlayed, parent.gamesPlayed);
        //if(Math.abs(a-b) > 0.0001) System.out.println("BOI " + a + " vs " + b);

        return confNodeUCBFunction(confidence, childGames, parentGames);
    }

    public float calculateUCBValueTie(MonteCarloTreeNode child, MonteCarloTreeNode parent, float tieP){
        float confidence = tieP;
        return confNodeUCBFunction(confidence, child.totalGamesPlayed, parent.totalGamesPlayed);
    }

    public float confNodeUCBFunction(float conf, float gamesPlayed, float parentVisits){
        if(gamesPlayed == 0) return infEval; //0 games played rule is currently to maximize priority: can be changed
        // to progressive bias
        //return (float)(conf + explorationParameter * Math.sqrt( Math.log(parentVisits) / (float)gamesPlayed));
        Tester.math -= Tester.getTesterTime();
        float c = (float)(conf + explorationParameter * Math.sqrt( Math.log(parentVisits) / (float)gamesPlayed));
        Tester.math += Tester.getTesterTime();
        return c;
    }
    public float nodeUCBFunction(float points, long gamesPlayed, long parentVisits){
        if(gamesPlayed == 0) return infEval;
        //return (float)((points/(float)gamesPlayed) + explorationParameter * Math.sqrt( Ma th.log(parentVisits) /
        // (float)gamesPlayed));
        Tester.math -= Tester.getTesterTime();
        float c = (float)((points/(float)gamesPlayed) + explorationParameter * Math.sqrt( Math.log(parentVisits) / (float)gamesPlayed));
        Tester.math += Tester.getTesterTime();
        return c;
    }

    float ucbBonusStrength = 0.07f;
    float ucbBonusEquParameter = 500;
    float ucbBonusMaxPoint = 100;
    public float[] calcUCBBonuses(MonteCarloTreeNode node){
        DSMinimum2 confidences = node.confTracker;
        //System.out.println("MCPARAMS " + node + " " + node.player.root + " " + confidences);
        if(confidences == null) return new float[node.player.numMoves];
        //if(node == node.player.root) return new float[confidences.maxKey+1];
        if(!useUCBBonuses) return new float[confidences.maxKey+1];

        float variableWeight;
        if(node.totalUpdates <= ucbBonusMaxPoint) variableWeight = node.totalUpdates/ucbBonusMaxPoint;
        else variableWeight = (float)Math.sqrt(ucbBonusEquParameter/(3 * node.totalUpdates + ucbBonusEquParameter));

        float SAFETY_WEIGHT_IDK = 1;

        float[] bonuses = new float[confidences.maxKey+1];
        float stdev = calcStandardDeviation(confidences.values);
        if(stdev < 0.001f) return new float[confidences.maxKey+1];
        float bonusRange = stdev*2f;

        //assigns each move a bonus based on difference between it and min value
        for (int index = 0; index < confidences.valueCount; index++) {
            float difference = Math.abs(confidences.getRootValue() - confidences.values[index]);
            difference = difference/bonusRange;
            difference = 1-difference;
            if(difference > 0){
                if(difference > 1) difference = 1;
                bonuses[confidences.indexToKey[index]] =
                        ucbBonusStrength*(float)Math.sqrt(node.validMoves.size())*difference*variableWeight * SAFETY_WEIGHT_IDK;
                //bonuses[confidences.indexToKey[index]] = 0.04f*node.validMoves.size()*difference;
            }
        }

        /**
         System.out.println("/  /  /");
         String debug = "MCPARAMS BONUSES: ";
         for (int i = 0; i < bonuses.length; i++) {
         debug += " move " + i + ": " + bonuses[i] + " ";
         }
         System.out.println(debug);
         System.out.println("VALID MOVES " + node.validMoves);
         System.out.println("Info " + stdev + " " + bonusRange);
         String[] dsreport = confidences.testReport();
         System.out.println(dsreport[1]);
         System.out.println(dsreport[2]);
         System.out.println(dsreport[5]);
         **/


        return bonuses;
    }
    public float calcStandardDeviation(float[] vals){
        float sum = 0;
        for(float v : vals) sum += v;
        float avg = sum/(float)vals.length;
        float variance = 0;
        for(float v : vals) variance += (v-avg)*(v-avg);
        variance = variance/(float)vals.length;
        return (float)Math.sqrt(variance);
    }


    public float calcTrueConfidence(MonteCarloTreeNode node){
        if(confCalculatorKey == 0){
            return node.avgConfidence;
        }else if(confCalculatorKey == 1){
            if(node.confTracker == null) return node.avgConfidence;
            if(node.validMoves.size() == 0) return node.avgConfidence;
            MonteCarloTreeNode minChild = node.children[node.confTracker.getRootKey()];

            int pCount = 1;
            //if(node.player.aiPlayerNum != node.currentPlayer && node.parents.size() > 0) pCount = node.parents.get(0).parent.validMoves.size();
            //if(node.parents.size() > 0) pCount = node.parents.get(0).parent.validMoves.size();
            float expSims = (float)confReqSims / (float)node.validMoves.size() / pCount;

            float b = (float)Math.min(maxB, (float)minChild.totalGamesPlayed / expSims);
            //if(node.player.estProgress > 0.996f && node == node.player.root || (node.player.root.parents.size() > 0 && node == node.player.root.parents.get(0).parent)){
            //System.out.println("games " + (float)minChild.gamesPlayed / expSims + " " + minChild.gamesPlayed + " " + expSims);
            //}
            //if(node == node.player.root || (node.player.root.parents.size() > 0 && node == node.player.root.parents.get(0).parent)) b = node.player.estProgress/2+0.3f;
            //else b = (float)Math.min(0.3f, 0.3f*(float)minChild.gamesPlayed / expSims);

            //System.out.println(node.player.estProgress);
            //if(node == node.player.root) System.out.println("mva " + node.minConfTracker.getMinValue() + " " + node.avgConfidence + " " + b + " " + ((b) * node.minConfTracker.getMinValue() + (1-b) * node.avgConfidence));
            //if(node.minConfTracker.getMinValue() > node.avgConfidence + 0.01f){
            //System.out.println("ERR");
            //System.out.println("mva " + node.minConfTracker.getMinValue() + " " + node.avgConfidence + " " + b + " " + ((b) * node.minConfTracker.getMinValue() + (1-b) * node.avgConfidence));
            //}
            //System.out.println(node.minConfTracker.getMinValue() + " " + (1-minChild.trueConfidence));

            //if((1-node.minConfTracker.getMinValue()) - node.avgConfidence < 0.15f || b < 0.2f) b = 0;
            //if(node.validMoves.size() < 9) b = 0;
            //if(node.validMoves.size() < 9 || node.avgConfidence - (1-node.minConfTracker.getMinValue()) < 0.0f) b = 0;

            float minValue = 1-node.confTracker.getRootValue();
            //minValue -= node.validMoves.size()*2;

            //boolean testscore = (node.grandDepth + (minValue-node.avgConfidence)*10)/40 < 1;

            float score = (node.grandDepth*depthWeight + (minValue-node.avgConfidence)*confDiffWeight);
            //if(testscore != (score < reqScore)) System.out.println("ERROR");
            if(score < reqScore) return node.avgConfidence;

            float conf = (b * minValue) + ((1-b) * node.avgConfidence);


            if(node.avgConfidence - (minValue) > 0.1f){
                //if(node.gamesPlayed > 100) System.out.println("GAMES PLAYED ERROR " + node.gamesPlayed);
                //System.out.println("avg: " + node.avgConfidence + "; min: " + (1-node.minConfTracker.getMinValue()) + "; b: " + b + "; conf: " + conf);
                //System.out.println(node.validMoves.size());
                //System.out.println("err " + node.currentPlayer);
                //System.out.println(node == node.player.root);
            }else{
                //System.out.println("opposite");
                //System.out.println("opp " + node.currentPlayer);
            }
            if(node.avgConfidence - (minValue) > 0.01f) return node.avgConfidence;


            /**
             if(! (node.grandDepth >= 40 || (node.grandDepth >= 10 && minValue-node.avgConfidence > 0.2f))){
             return node.avgConfidence;
             }**/

            //System.out.println("gd " + node.grandDepth);


            if(conf < 0) conf = 0f;
            if(conf > 1) conf = 1f;
            return conf;
        }
        return node.avgConfidence;
    }



    public MonteCarloParameters(){
        rand = new Random();
        gameParams = new AIGameParameters();
        reportParams = new ReportParameters();
        domain = new MCParamsDomainSpec();
    }
    public void setBaseGame(AIGameTracker gameTracker){
        gameParams.baseGame = (AIGameTracker) gameTracker.clone();
        gameParams.baseGame.resetInputTrackerCall();
    }

}
