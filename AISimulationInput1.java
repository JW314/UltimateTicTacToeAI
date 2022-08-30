import java.util.Random;
import java.util.List;

/**
 * AI Simulation Input; determines moves AI makes during Simulations
 *
 * VERSION 1: COMPLETELY RANDOM (including illegal plays; in that case, retries)
 */

public class AISimulationInput1 implements IAISimulationInput{

    Random rand;
    AIGameTracker tracker;

    IConverter converter;

    public AISimulationInput1(){ }
    public AISimulationInput1(Random r){
        setRandom(r);
    }
    public AISimulationInput1(IConverter c){
        setMoveConverter(c);
    }
    public AISimulationInput1(Random r, IConverter c){
        setRandom(r);
        setMoveConverter(c);
    }


    @Override
    public RealMove nextPlay() {
        return new RealMove(rand.nextInt(81), false, false);
    }

    @Override
    public void processPlay(int player, int moveID) {
        if(tracker != null) tracker.playMove(player, moveID);
    }
    @Override
    public void setAIGameTracker(AIGameTracker tr) {
        tracker = tr;
    }
    @Override
    public void setBoard(IGame g){
    }
    @Override
    public void setRandom(Random r) {
        rand = r;
    }

    @Override
    public void setMoveConverter(IConverter conv) {
        converter = conv;
    }

    @Override
    public Object clone() {
        AISimulationInput1 copy = new AISimulationInput1(this.rand, this.converter);
        //Dont touch tracker; tracker will be applied in the tracker class that calls clone
        return copy;
    }

    @Override
    public void end() {

    }
}
