import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;


public class TixTaxMoveSupplier implements IMoveSupplier{

    TixTaxBoard board;

    List<List<Integer>> avalBoxMoves;
    List<Integer> allAvalMoves;
    boolean[] cleansed;

    public TixTaxMoveSupplier(){initialize(null);}
    public TixTaxMoveSupplier(TixTaxBoard board){
        initialize(board);
    }

    private void initialize(TixTaxBoard b){
        if(b == null) return;
        board = b;
        reset(board.boardsize * board.boardsize);
        cleansed = new boolean[board.boardsize * board.boardsize];
    }

    /**
     * Recalculate available moves to player
     * @param size
     */
    private void reset(int size){
        avalBoxMoves = new ArrayList<>();
        allAvalMoves = new ArrayList<>();
        cleansed = new boolean[size];
        for (int boxnum = 0; boxnum < size; boxnum++) {
            avalBoxMoves.add(new ArrayList<>());
            if(board.winTracker.checkBoxWinner(boxnum) != 0){
                cleansed[boxnum] = true;
                continue;
            }
            for (int cellnum = 0; cellnum < size; cellnum++) {
                if(board.checkCell(boxnum, cellnum) == 0){
                    avalBoxMoves.get(boxnum).add(size*boxnum + cellnum);
                    allAvalMoves.add(size*boxnum + cellnum);
                }
            }
        }
    }

    @Override
    public void setBoard(IGame game, boolean recalculate) {
        board = (TixTaxBoard) game;
        if(recalculate) reset(board.boardsize*board.boardsize);
    }

    @Override
    public List<Integer> getAllValidMoves(EMoveSupplyFilter moveFilter) {
        //TODO: IMPLEMENT SORTING I GUESS IF ITS NEEDED
        //TODO: JUST REMEMBER THIS LIST IS NOT SORTED

        /**
        boolean boxFinished = true; //board.winTracker.checkBoxWinner(board.currentbox) != 0;
        if(board.currentbox == -1) boxFinished = true;
        else boxFinished = board.winTracker.checkBoxWinner(board.currentbox) != 0;**/
        /**
        List<Integer> result = new ArrayList<>();
        if(boxFinished){
            result = allAvalMoves;
        }else{
            result = avalBoxMoves.get(board.currentbox);
        }**/

        boolean boxFinished = board.currentbox == -1 || board.winTracker.checkBoxWinner(board.currentbox) != 0;

        List<Integer> validMovesTest = new ArrayList<>();
        List<Integer> validMoves = new ArrayList<>();


        for (int box = 0; box < 9; box++) {
            if(board.winTracker.checkBoxWinner(box) != 0) continue;
            if(!boxFinished && box != board.currentbox) continue;
            int[] validcells = TixTaxInfo.
                    getValidCells(board.getBoardID(box));
            List<Integer> moveNormal = new ArrayList<>(); //normal move
            List<Integer> moveBoxWin = new ArrayList<>(); //move wins box
            List<Integer> moveWild = new ArrayList<>(); //move both wins box and gives opponent wild

            for (int i : validcells) {

                boolean boxWin = TixTaxInfo.checkIfThreat(board.getBoardID(box), board.currentPlayer, i);
                //goes to a complete box: either this box, which becomes complete, or another complete box
                boolean boxWinWild =
                        boxWin && (i == box || board.winTracker.checkBoxWinner(i) != 0);
                int moveID = 9*box+i;
                if(boxWinWild) moveWild.add(moveID);
                else if(boxWin) moveBoxWin.add(moveID);
                else moveNormal.add(moveID);
            }
            //TODO: Implement sorting
            //
            for(int moveID : moveNormal) validMoves.add(moveID);
            for(int moveID : moveBoxWin){
                validMoves.add(moveID);
            }
            for (int i = 0; i < moveWild.size(); i++) {
                if(moveFilter == EMoveSupplyFilter.filterPseudoEquivalents){
                    if(moveBoxWin.size() > 0) break;
                    if(i == 0) validMoves.add(moveWild.get(i));
                    break;
                }
                else if(moveFilter == EMoveSupplyFilter.filterEquivalents){
                    if(i == 0) validMoves.add(moveWild.get(i));
                    break;
                }
                else{
                    validMoves.add(moveWild.get(i));
                }
            }
        }

        return validMoves;

        /**
        if(result.size() - newresult.size() != 0){
            System.out.println("MOVESUP - input size mismatch");
        }
        for(int i = 0; i < Math.min(result.size(), newresult.size()); i++){
            if(result.get(i) != newresult.get(i)){
                System.out.println("MOVESUP - move mismatch");
                System.out.println("old " + result);
                System.out.println("new " + newresult);
            }
        }
        System.out.println("yoyle");**/

    }

    @Override
    public int getRandomMove(Random rand) {
        //List<Integer> moves = getAllValidMoves(false);
        //return moves.get(rand.nextInt(moves.size()));


        boolean boxFinished = board.currentbox == -1 || board.winTracker.checkBoxWinner(board.currentbox) != 0;
        if(boxFinished){
            if(board.opencells <= 10) {
                int size = 0;
                int globalID = board.winTracker.globalID;

                int[] lengths = new int[9];
                for (int box = 0; box < 9; box++) {
                    if(TixTaxInfo.getPieceAt(globalID, box) != 0) continue;
                    lengths[box] = TixTaxInfo.getValidCells(board.getBoardID(box)).length;
                    size += lengths[box];
                }

                int index = rand.nextInt(size);
                if(index * 2 < size){
                    for (int box = 0; box < 9; box++) {
                        if(TixTaxInfo.getPieceAt(globalID, box) != 0) continue;
                        if(index < lengths[box]){
                            int[] validcells = TixTaxInfo.getValidCells(board.getBoardID(box));
                            return 9*box + validcells[index];
                        }else{
                            index -= lengths[box];
                        }
                    }
                }else{
                    for (int box = 8; box >= 0; box--) {
                        if(TixTaxInfo.getPieceAt(globalID, box) != 0) continue;
                        if(index < lengths[box]){
                            int[] validcells = TixTaxInfo.getValidCells(board.getBoardID(box));
                            return 9*box + validcells[index];
                        }else{
                            index -= lengths[box];
                        }
                    }
                }
                System.out.println("MOVESUP - ERROR NO MOVE FOUND");
                return 0;
            }
            int testMove = rand.nextInt(81);
            while(!board.isMoveValidBoard(testMove / 9, testMove % 9)) testMove = rand.nextInt(81);
            return testMove;
        }else{
            //System.out.println("MOVESUP - quik");
            int[] validcells = TixTaxInfo.getValidCells(board.getBoardID(board.currentbox));
            return 9*board.currentbox + validcells[rand.nextInt(validcells.length)];
        }
    }


    private void processMove(int boxnum, int cellnum){
        /**
        avalBoxMoves.get(boxnum).remove((Object)(9*boxnum + cellnum));
        allAvalMoves.remove((Object)(9*boxnum + cellnum));
        if(!cleansed[boxnum] && board.winTracker.checkBoxWinner(boxnum) != 0){
            cleansed[boxnum] = true;
            for (int i = 0; i < allAvalMoves.size(); i++) {
                if(allAvalMoves.get(i) < 9*boxnum) continue;
                if(allAvalMoves.get(i) >= 9*(boxnum+1)) break;
                allAvalMoves.remove((int)i);
                i--;
            }
        }**/
    }

    @Override
    public void playMove(String input){
        int boxnum = Integer.parseInt(input.substring(1, 2));
        int cellnum = Integer.parseInt(input.substring(2, 3));
        processMove(boxnum, cellnum);
    }

    @Override
    public void playMove(int player, int moveID) {
        processMove(moveID/9,moveID%9);
    }

    @Override
    public Object clone() {
        TixTaxMoveSupplier copy = new TixTaxMoveSupplier();
        //skip Board copy; board is set by AIGameTracker
        copy.allAvalMoves = new ArrayList<>(this.allAvalMoves);
        copy.avalBoxMoves = new ArrayList<>(this.avalBoxMoves.size());
        for(List<Integer> box : this.avalBoxMoves){
            copy.avalBoxMoves.add(new ArrayList<>(box));
        }
        copy.cleansed = new boolean[cleansed.length];
        for(int i = 0; i < cleansed.length; i++){
            copy.cleansed[i] = this.cleansed[i];
        }
        return copy;
    }
}
