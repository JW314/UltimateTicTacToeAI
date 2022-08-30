import java.util.List;

public interface IStateHasher extends ICloneable{
    public void setBoard(IGame game, boolean recalculate);
    public HashKey makeHashKey();
    public void playMove(String input);
    public void playMove(int player, int moveID);
}
