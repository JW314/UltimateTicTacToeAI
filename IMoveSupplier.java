import java.util.List;
import java.util.Random;

public interface IMoveSupplier extends ICloneable{
    public void setBoard(IGame game, boolean recalculate);

    /*
    filterEquivalents means to delete identical moves from the list
     */
    public List<Integer> getAllValidMoves(EMoveSupplyFilter filterEquivalents);
    public int getRandomMove(Random rand);


    public void playMove(String input);
    public void playMove(int player, int moveID);
}
