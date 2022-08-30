import java.util.Random;
import java.util.List;

/**
 * AI Simulation Input; determines moves AI makes during Simulations
 *
 * VERSION 2: RANDOM WITH MOVE SUPPLIER
 */

public class AISimulationInput2 implements IAISimulationInput{

    Random rand;
    AIGameTracker tracker;

    IConverter converter;

    public AISimulationInput2(){ }
    public AISimulationInput2(Random r){
        setRandom(r);
    }
    public AISimulationInput2(IConverter c){
        setMoveConverter(c);
    }
    public AISimulationInput2(Random r, IConverter c){
        setRandom(r);
        setMoveConverter(c);
    }


    @Override
    public RealMove nextPlay() {
        List<Integer> possibleMoves = tracker.moveSupplierGetValidMoves(EMoveSupplyFilter.noFilter);
        int moveIndex = rand.nextInt(possibleMoves.size());
        return new RealMove(possibleMoves.get(moveIndex), false, false);

        //String play = converter.convertIDtoMove(possibleMoves.get(moveIndex));

        //System.out.println("AISIMINPUT3 " + possibleMoves);
        //System.out.println(moveIndex + " " + possibleMoves.get(moveIndex));

        //return play;
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
        AISimulationInput2 copy = new AISimulationInput2(this.rand, this.converter);
        //Dont touch tracker; tracker will be applied in the tracker class that calls clone
        return copy;
    }

    @Override
    public void end() {

    }
}
