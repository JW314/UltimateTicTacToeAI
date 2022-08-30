//A real move is a move that would be played in an actual game: isn't really used for AI tree traversal

public class RealMove {

    //actual move
    int playID;

    boolean resign;
    boolean offerdraw;

    public RealMove(int pid, boolean r, boolean od){
        resign = r;
        offerdraw = od;
        playID = pid;
    }
}
