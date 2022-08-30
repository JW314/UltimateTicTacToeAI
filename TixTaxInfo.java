import java.util.*;

public class TixTaxInfo {

    static boolean breakTiesWithCount = false;

    static int[] simplePowersOf2;
    static int[] simplePowersOf3;
    static long[] powersof3modulo;
    static long[] factorials;

    static int[][] rowList;

    static long[] completeXHashes;
    static long[] completeOHashes;
    static long[] completeTieHashes;
    static class BoardInfo{
        int id;
        int[] cells;
        int winner;
        int majority;
        int[] counts;

        int[] validMoves;
        int[] xConnectionCount;
        int[] oConnectionCount;

        int[] xThreatsLookup;
        int[] xThreatsList;
        int[] oThreatsLookup;
        int[] oThreatsList;

        long[] lopPlayoutCounts;
        long lopNumPlayouts;
        float[] lopPlayoutProbabilities;
        float[] lopStrengthEvenTies; //ties are worth 50% to X, 50% to O
        boolean searched = false;

        //one hash per box
        long[] zobristHash;
    }
    //0-8 for each box, 9 = any box
    static long[] currentBoxHashes;
    static long switchPlayerHash;
    static BoardInfo[] boardInfos;

    static Random randHash;



    public static void setPowersof3Modulo(long mod){
        powersof3modulo = new long[81];
        powersof3modulo[0] = 1;
        for (int i = 1; i < 81; i++) {
            powersof3modulo[i] = (powersof3modulo[i-1]*3) % mod;
        }
    }
    public static void setSimplePowersOf3(){
        simplePowersOf3 = new int[10];
        simplePowersOf3[0] = 1;
        for (int i = 1; i < 10; i++) {
            simplePowersOf3[i] = 3*simplePowersOf3[i-1];
        }
        simplePowersOf2 = new int[10];
        simplePowersOf2[0] = 1;
        for (int i = 1; i < 10; i++) {
            simplePowersOf2[i] = 2*simplePowersOf2[i-1];
        }
        factorials = new long[10];
        factorials[0] = 1;
        for (int i = 1; i < 10; i++) {
            factorials[i] = i * factorials[i-1];
        }
    }
    public static long getPowerof3Mod(int exp){
        return powersof3modulo[exp];
    }
    public static void setRows(){
        rowList = new int[8][3];
        rowList[0] = new int[]{0, 1, 2};
        rowList[1] = new int[]{3, 4, 5};
        rowList[2] = new int[]{6, 7, 8};
        rowList[3] = new int[]{0, 3, 6};
        rowList[4] = new int[]{1, 4, 7};
        rowList[5] = new int[]{2, 5, 8};
        rowList[6] = new int[]{0, 4, 8};
        rowList[7] = new int[]{2, 4, 6};
    }
    public static void initialize(){
        //System.out.println("TIXTAXINFO INIT");
        Tester.infoInit -= Tester.getTesterTime();
        setRows();
        setSimplePowersOf3();
        setPowersof3Modulo(Integer.MAX_VALUE);
        makeBoardInfo();
        Tester.infoInit += Tester.getTesterTime();
        BoardInfo DEBUGCATCH = boardInfos[1367];
    }

    public static void makeBoardInfo(){
        randHash = new Random(24);

        completeXHashes = new long[9];
        completeOHashes = new long[9];
        completeTieHashes = new long[9];
        currentBoxHashes = new long[10];
        for (int i = 0; i < 9; i++) {
            completeXHashes[i] = randHash.nextLong();
            completeOHashes[i] = randHash.nextLong();
            completeTieHashes[i] = randHash.nextLong();
            currentBoxHashes[i] = randHash.nextLong();
        }
        currentBoxHashes[9] = randHash.nextLong();
        switchPlayerHash = randHash.nextLong();

        int[] cells = new int[9];
        boardInfos = new BoardInfo[19683];
        for (int i = 0; i < 19683; i++) {
            makeBoard(i, cells);
            for (int pos = 0; pos < 9; pos++) {
                if(cells[pos] == 2){
                    if(pos == 8){
                        break;
                    }
                    cells[pos] = 0;
                }else{
                    cells[pos]++;
                    break;
                }
            }
        }
        for (int i = 0; i < 19683; i++) {
            updateBoard(i);
        }

        searchGameTree();;
    }
    public static void makeBoard(int id, int[] cells){
        BoardInfo b = new BoardInfo();
        b.id = id;
        b.cells = new int[9];
        for (int i = 0; i < cells.length; i++) b.cells[i] = cells[i];
        b.winner = boardInfoWinner(cells);

        b.counts = new int[2];
        for (int i = 0; i < cells.length; i++) if(cells[i] > 0) b.counts[cells[i]-1]++;
        if(b.counts[0] > b.counts[1]) b.majority = 1;
        else if(b.counts[1] > b.counts[0]) b.majority = 2;
        else b.majority = 0;


        //VALID MOVES
        int zeroCount = 0;
        for (int i = 0; i < cells.length; i++) if(cells[i] == 0) zeroCount++;
        b.validMoves = new int[zeroCount];
        int vmcount = 0;
        for (int i = 0; i < cells.length; i++) if(cells[i] == 0){
            b.validMoves[vmcount] = i;
            vmcount++;
        }
        if(b.winner == 0 && vmcount == 0) b.winner = -1;


        //CONNECTION COUNT
        b.xConnectionCount = boardConnectionCounter(cells, 1);
        b.oConnectionCount = boardConnectionCounter(cells, 2);

        //HASHES
        b.zobristHash = new long[9];
        if(b.winner == 0){
            if(vmcount == 9){
                for (int i = 0; i < 9; i++) {
                    b.zobristHash[i] = completeTieHashes[i];
                }
            }else{
                for (int i = 0; i < 9; i++) {
                    b.zobristHash[i] = randHash.nextLong();
                }
            }
        }else{
            if(b.winner == 1){
                for (int i = 0; i < 9; i++) {
                    b.zobristHash[i] = completeXHashes[i];
                }
            }else{
                for (int i = 0; i < 9; i++) {
                    b.zobristHash[i] = completeOHashes[i];
                }
            }
        }

        boardInfos[id] = b;
    }
    public static void updateBoard(int id){
        List<Integer> xThreats = threatSearch(id, 1);
        List<Integer> oThreats = threatSearch(id, 2);

        BoardInfo b = boardInfos[id];
        b.xThreatsList = new int[xThreats.size()];
        b.xThreatsLookup = new int[9];
        for (int i = 0; i < xThreats.size(); i++) {
            b.xThreatsList[i] = xThreats.get(i);
            b.xThreatsLookup[xThreats.get(i)] = 1;
        }
        b.oThreatsList = new int[oThreats.size()];
        b.oThreatsLookup = new int[9];
        for (int i = 0; i < oThreats.size(); i++) {
            b.oThreatsList[i] = oThreats.get(i);
            b.oThreatsLookup[oThreats.get(i)] = 1;
        }
    }
    public static List<Integer> threatSearch(int id, int player){
        List<Integer> threats = new ArrayList<>();
        BoardInfo currBoard = boardInfos[id];
        for (int newcell = 0; newcell < 9; newcell++) {
            if(currBoard.cells[newcell] != 0) continue;
            int newid = id + player*simplePowersOf3[newcell];
            boolean isThreat = boardInfos[newid].winner == player;
            if(isThreat) threats.add(newcell);
        }
        return threats;
    }

    public static int boardInfoWinner(int[] box){
        for (int i = 0; i < rowList.length; i++) {
            if(box[rowList[i][0]] != 0 && box[rowList[i][0]] == box[rowList[i][1]] && box[rowList[i][1]] == box[rowList[i][2]]){
                return box[rowList[i][0]];
            }
        }
        return 0;
    }
    public static int[] boardConnectionCounter(int[] box, int target){
        int[] conn = new int[4];
        int[] matchbox = new int[box.length];
        for (int i = 0; i < box.length; i++) {
            matchbox[i] = (box[i] == target) ? 1 : 0;
        }
        for (int i = 0; i < rowList.length; i++) {
            conn[matchbox[rowList[i][0]]+matchbox[rowList[i][1]]+matchbox[rowList[i][2]]]++;
        }
        return conn;
    }

    public static void searchGameTree(){
        searchTreeID(0);
    }

    public static long[] searchTreeID(int boxID){
        if(boardInfos[boxID].searched) return boardInfos[boxID].lopPlayoutCounts;
        boardInfos[boxID].searched = true;
        long[] probs = new long[3];
        if(boardInfos[boxID].winner != 0){
            int validmoves = boardInfos[boxID].validMoves.length;
            long weight = simplePowersOf2[validmoves] * factorials[validmoves];
            if(boardInfos[boxID].winner == 1) probs = new long[]{weight, 0, 0};
            if(boardInfos[boxID].winner == 2) probs = new long[]{0, weight, 0};
            if(boardInfos[boxID].winner == -1) probs = new long[]{0, 0, weight};
        }
        else{
            for (int move : boardInfos[boxID].validMoves) {
                //pl = 1 means X, pl = 2 means O
                for (int pl = 1; pl <= 2; pl++) {
                    long[] movepp = searchTreeID(boxID + simplePowersOf3[move] * pl);
                    probs[0] += movepp[0];
                    probs[1] += movepp[1];
                    probs[2] += movepp[2];

                    //sum should be 2^(number of empty cells)
                    long sum = (movepp[0] + movepp[1] + movepp[2]);
                }
            }
        }
        boardInfos[boxID].lopPlayoutCounts = probs;
        float sum = probs[0] + probs[1] + probs[2];
        boardInfos[boxID].lopNumPlayouts = probs[0] + probs[1] + probs[2];
        boardInfos[boxID].lopPlayoutProbabilities = new float[]{probs[0]/sum, probs[1]/sum, probs[2]/sum};
        boardInfos[boxID].lopStrengthEvenTies = new float[2];
        boardInfos[boxID].lopStrengthEvenTies[0] = probs[0]/sum + probs[2]/(2*sum);
        boardInfos[boxID].lopStrengthEvenTies[1] = probs[1]/sum + probs[2]/(2*sum);

        return boardInfos[boxID].lopPlayoutCounts;
    }


    public static int getPieceAt(int id, int cell){
        return boardInfos[id].cells[cell];
    }
    public static int[] getCells(int id){
        return boardInfos[id].cells;
    }
    public static int getWinner(int id){
        return boardInfos[id].winner;
    }
    public static int[] getValidCells(int id){
        return boardInfos[id].validMoves;
    }
    public static boolean checkIfThreat(int id, int player, int cell){
        if(player == 1) return boardInfos[id].xThreatsLookup[cell] == 1;
        return boardInfos[id].oThreatsLookup[cell] == 1;
    }
    public static int getBoxMoveCount(int id, int player){
        return boardInfos[id].counts[player-1];
    }
    public static long getBoardHash(int id, int box){
        return boardInfos[id].zobristHash[box];
    }
    public static long getCurBoxHash(int curBox){
        return currentBoxHashes[(curBox == -1) ? 9 : curBox];
    }
    public static float[] getLOPAll(int id){
        return boardInfos[id].lopPlayoutProbabilities;
    }
    public static float[] getLOPEvenTies(int id){
        return boardInfos[id].lopStrengthEvenTies;
    }
}

