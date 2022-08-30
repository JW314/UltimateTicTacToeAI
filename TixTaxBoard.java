public class TixTaxBoard implements IGame, ICloneable{

    int[] board;
    int currentPlayer;
    int currentbox;

    TixTaxVictoryTracker winTracker;
    int plays;
    int opencells; //# of cells eligible for wild play

    int winningPlayer;

    int boardsize;
    int boxsize;

    long zobristHash;

    public TixTaxBoard(int totalsize, int minisize){
        initialize(totalsize, minisize);
    }

    public void initialize(int totalsize, int minisize){
        boardsize = totalsize;
        boxsize = minisize;
        start();
    }

    public void start(){
        plays = 0;

        board = new int[9];
        zobristHash = 0;
        for (int i = 0; i < 9; i++) {
            zobristHash = zobristHash ^ TixTaxInfo.getBoardHash(board[i], i);
        }
        zobristHash = zobristHash ^ TixTaxInfo.getCurBoxHash(-1);

        winTracker = new TixTaxVictoryTracker(this);

        currentPlayer = 1;
        currentbox = -1;
        winningPlayer = 0;

        opencells = 81;
    }


    public int getBoardSize(){
        return board.length;
    }
    public int getBoxSize(){
        return boxsize;
    }
    public int getBoardID(int box){
        return board[box];
    }

    private boolean checkValidBox(int targetbox){
        if(currentbox == -1) return true; //can move any box
        if(winTracker.checkBoxWinner(currentbox) != 0){
            //current box is filled; entire board is open
            //return if attempted box to play in is not filled
            return winTracker.checkBoxWinner(targetbox) == 0;
        }else{
            //player must play in designated box
            return targetbox == currentbox;
        }
    }

    public boolean isMoveValid(int player, int boxnum, int cellnum){
        if(boxnum > 8 || boxnum < 0) return false;
        if(cellnum > 8 || cellnum < 0) return false;
        if(winningPlayer != 0) return false;
        if(player != currentPlayer) return false;
        if(!isMoveValidBoard(boxnum, cellnum)) return false;
        return true;
    }

    public boolean isMoveValidBoard(int boxnum, int cellnum){
        if(!checkValidBox(boxnum)) return false;
        if(checkCell(boxnum, cellnum) != 0) return false;
        if(TixTaxInfo.getWinner(board[boxnum]) != 0) return false;
        return true;
    }

    /**
     * Used by playmove; playmove function changes input into 3 input infos needed, player, boxnum, and cellnum
     * @params contains move info
     * @return -2 = ERROR, -1 = tie, 0 = continue game, 1 = player 1 wins, 2 = player 2 wins
     */

    private int processMove(int player, int boxnum, int cellnum){


        if(!isMoveValid(player, boxnum, cellnum)){
            //System.out.println("TIXTAX BOARD retry move");
            System.out.println("Board movecheck " + (boxnum > 8 || boxnum < 0) + " " + (cellnum > 8 || cellnum < 0) +
                    " " + (winningPlayer != 0)+ " " + (player != currentPlayer) + " " + (!checkValidBox(boxnum)) + " " + (checkCell(boxnum, cellnum) != 0));
            return -2;
        }


        //boxboard[boxnum].updateCell(cellnum, player);
        int oldBoardID = board[boxnum];
        int oldCurBox = currentbox;
        updateCell(player, boxnum, cellnum);
        currentbox = cellnum;
        int winner = winTracker.updateCell(player, boxnum, cellnum);

        if(winner != 0){
            setWinner(winner);
        }
        else{
            if(currentPlayer == 1) currentPlayer = 2;
            else currentPlayer = 1;
        }

        if(TixTaxInfo.getWinner(board[currentbox]) != 0) currentbox = -1;

        int newBoardID = board[boxnum];
        int newCurBox = currentbox;

        plays++;
        if(TixTaxInfo.getWinner(board[boxnum]) != 0){
            opencells -= 10 - (TixTaxInfo.getBoxMoveCount(board[boxnum], 1) + TixTaxInfo.getBoxMoveCount(board[boxnum], 2));
            //10 because 9 cells in total for the box, and add 1 because we just played a move
        }else{
            opencells--;
        }

        zobristHash = zobristHash ^ TixTaxInfo.getBoardHash(oldBoardID, boxnum) ^ TixTaxInfo.getBoardHash(newBoardID, boxnum);
        zobristHash = zobristHash ^ TixTaxInfo.getCurBoxHash(oldCurBox) ^ TixTaxInfo.getCurBoxHash(newCurBox);
        if(winner == 0) zobristHash = zobristHash ^ TixTaxInfo.switchPlayerHash;

        /**
         long hashCheck = reCalcHash();

         if(hashCheck != zobristHash){
         System.out.println("TIX TAX BOARD HASH ERROR");
         System.out.println("UPDATED " + zobristHash);
         System.out.println("RECALCD " + hashCheck);
         System.out.println("DIFFS " + (zobristHash ^ hashCheck));
         String[] state = stateString();
         for(String s : state) System.out.println(s);

         System.out.println(newBoardID + " " + newCurBox + " " + plays);

         //zobristHash = reCalcHash();
         }
         **/


        return winner;
    }

    public void updateCell(int player, int box, int cell){
        //this safety i guess for some reason if i need it; makes it so program doesnt die if a cell already with a
        // nonzero value doesnt die if replaced
        //if(TixTaxInfo.getPieceAt(board[box], cell) != 0) System.out.println("TIXTAX BOX: weird replacing cells or " +
        //"something");
        //board[box] -= (TixTaxInfo.powersof3modulo[cell] * TixTaxInfo.getPieceAt(board[box], cell));

        //actual code
        board[box] += TixTaxInfo.powersof3modulo[cell]*player;
    }

    public int checkCell(int box, int cell){
        return TixTaxInfo.getPieceAt(board[box], cell);
    }

    public long reCalcHash(){
        long hashCheck = 0;
        for (int i = 0; i < 9; i++) {
            hashCheck = hashCheck ^ TixTaxInfo.getBoardHash(board[i], i);
        }
        hashCheck = hashCheck ^ TixTaxInfo.getCurBoxHash(currentbox);
        if(currentPlayer == 2) hashCheck = hashCheck ^ TixTaxInfo.switchPlayerHash;
        return hashCheck;
    }

    @Override
    public int playMove(String input){
        Tester.playMoveParse -= Tester.getTesterTime();
        int player = Integer.parseInt(input.substring(0, 1));
        int boxnum = Integer.parseInt(input.substring(1, 2));
        int cellnum = Integer.parseInt(input.substring(2, 3));
        Tester.playMoveParse += Tester.getTesterTime();

        return processMove(player, boxnum, cellnum);
    }

    @Override
    public int playMove(int player, int moveID) {
        int boxnum = moveID / 9;
        int cellnum = moveID % 9;
        return processMove(player, boxnum, cellnum);
    }

    public void setWinner(int player){
        winningPlayer = player;
    }

    public int getWinningPlayer() {
        return winningPlayer;
    }

    public String[] stateString(){
        //System.out.println("TixTaxBoard create board state string");
        String[] rows = new String[9];
        for (int i = 0; i < 9; i++) {
            rows[i] = "";
        }
        for (int box = 0; box < 9; box++) {
            for (int rawrow = 0; rawrow < 3; rawrow++) {
                int row = rawrow + (box / 3) * 3;
                for (int cell = 0; cell < 3; cell++) {
                    int val = checkCell(box, rawrow*3 + cell);
                    int winner = winTracker.checkBoxWinner(box);
                    String str = "-";
                    if(val == 1) str = "X";
                    if(val == 2) str = "O";
                    //if(winner == 1) str = "X";
                    //if(winner == 2) str = "O";
                    rows[row] += str;
                }
                if(box % 3 != 2){
                    //vertical parititon
                    rows[row] += "|";
                }
            }
        }
        //add horizontal partitions
        String[] rows2 = new String[12];
        String horizPartition = "-----------";
        int currentRow = 0;
        for (int i = 0; i < 11; i++) {
            if(i == 3 || i == 7) rows2[i] = horizPartition;
            else{
                rows2[i] = rows[currentRow];
                currentRow++;
            }
        }
        String binString = Long.toBinaryString(zobristHash);
        while(binString.length() < 64) binString = "0" + binString;
        rows2[10] += "   Player: "+currentPlayer+" ; Box: " + (currentbox+1) + " ; Hash: " + zobristHash;

        //add mini supplement board to show won boards
        for (int row = 0; row < 3; row++) {
            int actualrow = row+4;
            rows2[actualrow] += "   (";
            for (int col = 0; col < 3; col++) {
                int val = winTracker.checkBoxWinner(row*3+col);
                String str = "-";
                if(val == 1) str = "X";
                if(val == 2) str = "O";
                if(val == -1) str = "/";
                rows2[actualrow] += str;
            }
            rows2[actualrow] += ")";
        }
        int xCount = 0;
        int oCount = 0;
        for(int box = 0; box < 9; box++){
            xCount += TixTaxInfo.getBoxMoveCount(board[box], 1);
            oCount += TixTaxInfo.getBoxMoveCount(board[box], 2);
        }
        rows2[1] += "   " + xCount + " X";
        rows2[2] += "   " + oCount + " O";
        rows2[11] = buildNotation();

        return rows2;
    }

    public String buildNotation() {
        String notation = "";
        for(int box = 0; box < 9; box++){
            String boxnotes = "";
            int emptystreak = 0;
            for(int cell = 0; cell < 9; cell++){
                int sym = checkCell(box, cell);
                if(sym == 0){
                    emptystreak++;
                }
                else{
                    if(emptystreak != 0) boxnotes += emptystreak;
                    emptystreak = 0;
                    if(sym == 1) boxnotes += "X";
                    else boxnotes += "O";
                }
                if(cell == 8) {
                    if (emptystreak != 0) boxnotes += emptystreak;
                    notation += boxnotes;
                }

            }
            if(box == 8) notation += " ";
            else notation += "/";

        }

        notation += ((currentPlayer == 1) ? "X" : "O") + " " + ((currentbox + 1 == 0) ? "A" : (currentbox + 1));
        return notation;
    }


    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public long getZobristHash(){
        return zobristHash;
    }

    public void setWinTracker(TixTaxVictoryTracker winTracker) {
        this.winTracker = winTracker;
    }

    public void setBoard(int[] board){
        this.board = board;
    }

    public void setCurrentbox(int currentbox) {
        this.currentbox = currentbox;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPlays(int plays) {
        this.plays = plays;
    }

    public void addBoardToVictoryTracker(){
        winTracker.setBoard(this);
    }

    public void manualForceSet(int box, int cell, int player){
        //boxboard[box].updateCell(cell,player);
        updateCell(player, box, cell);
        winTracker.reCheckVictory();
    }

    public Object clone() {
        Tester.boardCloneCount++;



        TixTaxBoard copy = new TixTaxBoard(boardsize, boxsize);

        int[] copyIDs = new int[9];
        for (int i = 0; i < 9; i++) {
            copyIDs[i] = board[i];
        }
        copy.setBoard(copyIDs);

        copy.setWinTracker((TixTaxVictoryTracker)winTracker.clone());
        copy.setCurrentbox(currentbox);
        copy.setCurrentPlayer(currentPlayer);
        copy.setPlays(plays);
        copy.addBoardToVictoryTracker();
        copy.zobristHash = zobristHash;
        return copy;
    }
}
