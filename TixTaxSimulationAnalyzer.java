import java.util.*;


public class TixTaxSimulationAnalyzer implements ISimulationAnalyzer {

    List<Integer> p1moves = new ArrayList<>();
    List<Integer> p2moves = new ArrayList<>();

    int winner = -1;

    public void processPlay(int player, int moveID){
        if(player == 1) p1moves.add(moveID);
        else p2moves.add(moveID);
    }

    public void setWinner(int w){
        winner = w;
    }

    public void updateRave(RaveTracker rave){
        float tieValue = rave.tieValue;
        for(int p1move : p1moves){
            float value = tieValue;
            if(winner == 1) value = 1;
            else if(winner == 2) value = 0;
            rave.raveInfos[0].incrementValue(p1move, value, 1);
        }
        for(int p2move : p2moves){
            float value = tieValue;
            if(winner == 1) value = 0;
            else if(winner == 2) value = 1;
            rave.raveInfos[1].incrementValue(p2move, value, 1);
        }
    }
}
