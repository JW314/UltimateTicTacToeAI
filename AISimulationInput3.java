import java.util.Random;
import java.util.List;

/**
 * AI Simulation Input; determines moves AI makes during Simulations
 *
 * VERSION 3: REPLACE AIGAMETRACKER TO MORE SPECIFIC DATA STRUCTURES
 * Removes the need for a useless board update
 */

public class AISimulationInput3 implements IAISimulationInput{

    Random rand;

    IMoveSupplier supplier;

    IConverter converter;

    public AISimulationInput3(){ }
    public AISimulationInput3(Random r){
        setRandom(r);
    }
    public AISimulationInput3(IConverter c){
        setMoveConverter(c);
    }
    public AISimulationInput3(Random r, IConverter c){
        setRandom(r);
        setMoveConverter(c);
    }


    @Override
    public RealMove nextPlay() {
        return new RealMove(supplier.getRandomMove(rand), false, false);
        //List<Integer> possibleMoves = supplier.getAllValidMoves(false);
        //int moveIndex = rand.nextInt(possibleMoves.size());
        //return possibleMoves.get(moveIndex);

        //String play = converter.convertIDtoMove(possibleMoves.get(moveIndex));

        //System.out.println("AISIMINPUT3 " + possibleMoves);
        //System.out.println(moveIndex + " " + possibleMoves.get(moveIndex));

        //return play;
    }

    @Override
    public void processPlay(int player, int moveID) {
        if(supplier != null) supplier.playMove(player, moveID);
    }
    @Override
    public void setAIGameTracker(AIGameTracker tr) {
        supplier = tr.moveSupplier;
    }
    @Override
    public void setBoard(IGame g){
        supplier.setBoard(g, false);
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
        AISimulationInput3 copy = new AISimulationInput3(this.rand, this.converter);
        //Dont touch tracker; tracker will be applied in the tracker class that calls clone
        return copy;
    }

    @Override
    public void end() {

    }
}
