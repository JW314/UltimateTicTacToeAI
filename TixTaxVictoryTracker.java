public class TixTaxVictoryTracker implements ICloneable {

    TixTaxBoard board;

    int globalID;
    int boxesWon;

    public TixTaxVictoryTracker(){initialize(null);}
    public TixTaxVictoryTracker(TixTaxBoard board){
        initialize(board);
    }

    private void initialize(TixTaxBoard b){
        if(b == null) return;
        board = b;
        globalID = 0;
        boxesWon = 0;
    }


    /**
     * Adds cell to Victory Tracker data
     *
     * @param boxnum  box of newly updated cell
     * @param cellnum  cell of newly updated cell
     * @return winner; 0 if inconclusive, -1 if tie
     */
    public int updateCell(int player, int boxnum, int cellnum){
        Tester.gameWinCheck -= Tester.getTesterTime();

        int localboxwinner = TixTaxInfo.getWinner(board.getBoardID(boxnum));

        if(localboxwinner > 0) globalID += TixTaxInfo.powersof3modulo[boxnum] * localboxwinner;

        if(0 != localboxwinner){
            //new box was won; check entire board
            boxesWon++;
            int result = TixTaxInfo.getWinner(globalID);
            if(result == -1) result = 0;
            if(result != 0){
                Tester.gameWinCheck += Tester.getTesterTime();
                return result;
            }
            else if(boxesWon == 9){
                if(TixTaxInfo.breakTiesWithCount){
                    int p1boxes = TixTaxInfo.getBoxMoveCount(globalID, 1);
                    int p2boxes = TixTaxInfo.getBoxMoveCount(globalID, 2); ;

                    if(p1boxes > p2boxes){
                        Tester.gameWinCheck += Tester.getTesterTime();
                        return 1;
                    }
                    if(p1boxes < p2boxes){
                        Tester.gameWinCheck += Tester.getTesterTime();
                        return 2;
                    }
                }
                Tester.gameWinCheck += Tester.getTesterTime();
                return -1;
            }
            else {
                Tester.gameWinCheck += Tester.getTesterTime();
                return 0;
            }

        }else{
            //no change to overall board
            //check for filled board
            if(TixTaxInfo.getValidCells(board.getBoardID(boxnum)).length == 0){
                boxesWon++;
                if(boxesWon == 9){
                    //all boxes filled; game over
                    int result = TixTaxInfo.getWinner(globalID);
                    if(result == -1) result = 0;
                    if(result > 0){
                        Tester.gameWinCheck += Tester.getTesterTime();
                        return result;
                    }

                    if(TixTaxInfo.breakTiesWithCount){
                        int p1boxes = TixTaxInfo.getBoxMoveCount(globalID, 1);
                        int p2boxes = TixTaxInfo.getBoxMoveCount(globalID, 2); ;

                        if(p1boxes > p2boxes){
                            Tester.gameWinCheck += Tester.getTesterTime();
                            return 1;
                        }
                        if(p1boxes < p2boxes){
                            Tester.gameWinCheck += Tester.getTesterTime();
                            return 2;
                        }
                    }
                    Tester.gameWinCheck += Tester.getTesterTime();
                    return -1;
                }
            }
            Tester.gameWinCheck += Tester.getTesterTime();
            return 0;
        }

    }

    public int reCheckVictory(){
        System.out.println("Win Tracker Recheck (bad lol)");
        globalID = 0;
        boxesWon = 0;

        for (int boxnum = 0; boxnum < 9; boxnum++) {
            int plays = 0;
            for(int cell : TixTaxInfo.getCells(board.getBoardID(boxnum))){
                if(cell != 0) plays++;
            }
            int winner = TixTaxInfo.getWinner(board.getBoardID(boxnum));
            if(winner == 0 && plays == 9) winner = -1;
            if(winner != 0) boxesWon++;
            if(winner > 0) globalID += TixTaxInfo.powersof3modulo[boxnum] * winner;
        }
        int result = TixTaxInfo.getWinner(globalID);
        if(result == -1) result = 0;
        if(boxesWon >= 9){
            if(result != 0) return result;

            if(TixTaxInfo.breakTiesWithCount){
                int p1boxes = TixTaxInfo.getBoxMoveCount(globalID, 1);
                int p2boxes = TixTaxInfo.getBoxMoveCount(globalID, 2); ;
                if(p1boxes > p2boxes) return 1;
                if(p1boxes < p2boxes) return 2;
            }

            return -1;
        }else{
            return result;
        }
    }

    public int checkBoxWinner(int box) { return TixTaxInfo.getWinner(board.getBoardID(box)); }


    public void setBoxesWon(int boxesWon) {
        this.boxesWon = boxesWon;
    }


    public void setBoard(TixTaxBoard board) {
        this.board = board;
    }

    public Object clone() {
        TixTaxVictoryTracker copy = new TixTaxVictoryTracker();
        copy.globalID = this.globalID;
        copy.setBoxesWon(boxesWon);
        return copy;
    }
}

