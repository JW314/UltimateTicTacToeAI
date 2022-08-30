import java.util.*;

public class MCParamsDomainSpec {

    boolean usePriors;

    float priorWeightBase;
    float priorWeightBonus;

    /**
     * output[0] = priorPoints
     * output[1] = priorBase
     */
    public void calculateNodePrior(MonteCarloTreeNode node, AIGameTracker game, List<Integer> validMoves){
        if(!usePriors){
            return;
        }
        List<Float> heuristics = new ArrayList<>();
        for (int moveID : validMoves){
            float value = game.heuristics.getMoveValue(node.currentPlayer, moveID);
            heuristics.add(value);
        }

        float maxHeuristic = 0;
        for(float h : heuristics) if(maxHeuristic < h) maxHeuristic = h;

        for (int i = 0; i < validMoves.size(); i++) {
            int moveID = validMoves.get(i);
            float startingConf = heuristics.get(i)/maxHeuristic;
            if(startingConf < 0) startingConf = 0;
            else if(startingConf > 1) startingConf = 1;

            float weight = priorWeightBonus * startingConf + priorWeightBase;
            node.priorPoints[moveID] = startingConf * weight;
            node.priorBase[moveID] = weight;

        }

        for(int moveID : validMoves) {
            node.totalPrior += node.priorBase[moveID];
        }
    }
}
