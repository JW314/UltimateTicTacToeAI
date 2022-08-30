public interface IGame extends ICloneable{
    public int getCurrentPlayer();
    public long getZobristHash();
    public void start();
    public int playMove(String input);
    public int playMove(int player, int moveID);
    public String[] stateString();
    public String buildNotation();
}
