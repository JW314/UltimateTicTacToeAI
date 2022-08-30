import java.util.ArrayList;

/**
 * Uses Polynomial String Hashing to store a unique string for current game state
 *
 * DEPRECATED
 * DEPRECATED
 * DEPRECATED
 */

public class TixTaxStateHasher implements IStateHasher{

    TixTaxBoard board;
    int[] boardRepresentation;

    int currentHash;
    int openBoxHash; //portion of currentHash that is from the OpenBox

    int base1; //Raised to some power to represent Hash Value for Board Cells
    int base2; //Multipled with Open Box
    //Hash value to store Current Player is not necessary, because Current Player can be deduced from the Board

    long modulus;

    public TixTaxStateHasher(long mod, boolean calculate){
        currentHash = 0;
        openBoxHash = 0;
        modulus = mod;
        base1 = 3;
        base2 = 12959;

        boardRepresentation = new int[82];
        boardRepresentation[0] = -1;

        if(calculate) TixTaxInfo.setPowersof3Modulo(mod);
    }

    private void reset() {
        //Tester.hash -= Tester.getTesterTime();
        int size = 9;
        currentHash = 0;
        for (int boxnum = 0; boxnum < size; boxnum++) {
            for (int cellnum = 0; cellnum < size; cellnum++) {
                int cellvalue = board.checkCell(boxnum, cellnum);
                incrementBoardHash(cellvalue, boxnum, cellnum);
                boardRepresentation[boxnum*9+cellnum+1]=cellvalue;
            }
        }
        boardRepresentation[0] = board.currentbox;
        setOpenBoxHash();
        //Tester.hash += Tester.getTesterTime();
    }

    public void setBoard(IGame game, boolean recalculate){
        board = (TixTaxBoard) game;
        if(recalculate) reset();

    }
    public HashKey makeHashKey(){
        //return new HashKey(boardRepresentation, currentHash);
        Tester.hash -= Tester.getTesterTime();
        HashKey h = new HashKey(boardRepresentation, currentHash);
        Tester.hash += Tester.getTesterTime();
        return h;
    }

    /**
     * Precondition: Input Box Coordinates was empty before Last Play
     */
    public void incrementBoardHash(int player, int box, int cell){
        Tester.hash -= Tester.getTesterTime();
        currentHash += TixTaxInfo.getPowerof3Mod(box*9+cell)*player;
        currentHash %= modulus;
        Tester.hash += Tester.getTesterTime();

    }
    public void updateBoardRep(int player, int box, int cell){
        //Tester.hash -= Tester.getTesterTime();

        boardRepresentation[0] = board.currentbox;
        boardRepresentation[box*9+cell+1] = player;

        //Tester.hash += Tester.getTesterTime();

    }

    /**
     * OPEN BOX HASH KEY: currentboxnum + 1
     * -1 = all boxes -> hash key of 0, boxes 1-9 -> hash key 1-9
     */
    public void setOpenBoxHash(){
        currentHash -= openBoxHash;
        openBoxHash = (board.currentbox+1) * base2;
        currentHash += openBoxHash;
        while(currentHash < 0) currentHash += modulus;
        while(currentHash >= modulus) currentHash -= modulus;
    }

    private void processMove(int player, int boxnum, int cellnum){
        incrementBoardHash(player, boxnum, cellnum);
        updateBoardRep(player, boxnum, cellnum);
        setOpenBoxHash();
    }

    @Override
    public void playMove(String input){
        int player = Integer.parseInt(input.substring(0, 1));
        int boxnum = Integer.parseInt(input.substring(1, 2));
        int cellnum = Integer.parseInt(input.substring(2, 3));
        processMove(player, boxnum, cellnum);
    }

    @Override
    public void playMove(int player, int moveID) {
        processMove(player, moveID/9, moveID%9);
    }

    @Override
    public Object clone() {
        //Tester.hash -= Tester.getTesterTime();

        TixTaxStateHasher copy = new TixTaxStateHasher(modulus, false);
        //skip Board copy; board is set by AIGameTracker
        copy.currentHash = this.currentHash;
        copy.openBoxHash = this.openBoxHash;
        copy.base1 = this.base1;
        copy.base2 = this.base2;
        for (int i = 0; i < boardRepresentation.length; i++) {
            copy.boardRepresentation[i] = boardRepresentation[i];
        }

        //Tester.hash += Tester.getTesterTime();

        return copy;
    }
}
