import java.util.*;

public class AIPlayer {

    public int aiPlayerNum;

    AIGameTracker gameAI;

    Map<Long, MonteCarloTreeNode> table;

    RaveTracker rave;

    //each move is represented by an integer between (0, numMoves) [0 inclusive, numMoves exclusive]
    int numMoves;

    float estProgress = 0f;

    public MonteCarloParameters params;
    public MonteCarloSimulator simulator;

    MonteCarloTreeNode root;

    //most recent MCTN cycle info:
    float lastDepth = 0;
    boolean lastIsRolloutTerminal = false;

    DSSlidingWindow depthCalculator;
    DSSlidingWindow tRolloutTracker;
    int allTimeMaxDepth = 0;

    public AIPlayer(IGame g, int nummoves, int aiPlayer, MonteCarloParameters params){
        initialize(g, nummoves, aiPlayer, params);
    }

    private void initialize(IGame g, int nummoves, int aiPlayer, MonteCarloParameters p){
        params = p;
        gameAI = params.gameParams.getFreshGameTracker();

        numMoves = nummoves;
        aiPlayerNum = aiPlayer;

        root = new MonteCarloTreeNode(this, numMoves, 1, 0);

        depthCalculator = new DSSlidingWindow(params.reportParams.searchDepthWindowSize);
        tRolloutTracker = new DSSlidingWindow(params.reportParams.tRolloutWindowSize);

        if(p.useTranspositionTable) table = new HashMap<>(p.tableCapacity);
        if(p.rave.useRave) rave = new RaveTracker(numMoves, p.tiePoints);
    }

    //Total Est Rounds is a number that estimates the total number of rounds the program will run before committing to a move
    //Can be useful for certain hueristics
    public void chooseMove(int numRounds, int totalEstRounds){
        long startstart = Tester.getTesterTime();
        long totaltime1 = 0;
        long totaltime2 = 0;
        for (int i = 0; i < numRounds; i++) {
            long start = Tester.getTesterTime();

            //System.out.println("AIPlayer - start " + i);
            AIGameTracker currGame = (AIGameTracker) gameAI.clone();
            for (int j = 0; j < 0; j++) {
                currGame = (AIGameTracker) currGame.clone();
            }

            long end = Tester.getTesterTime();
            Tester.cloneBoardPlayer += end-start;

            Tester.checkPoint("Player start selection");

            root.selection(0, currGame, new ArrayList<>(8), new ArrayList<>(8));
            depthCalculator.addItem(lastDepth);
            tRolloutTracker.addItem((lastIsRolloutTerminal) ? 1 : 0);
            changeEstProgress(1f/(float)totalEstRounds);

            long end2 = Tester.getTesterTime();
            Tester.chooseMovePlayer += end2-end;

            if(Tester.debug) System.out.println("CHOOSEMOVE ROUND " + i + ": " + (end2-end) + "ns");


            if(end-start >= 50L){
                //System.out.println("AIPLAYER - UBER LONG CLONE " + i + " " + (end-start));
            }
            if(end2-end >= 50L){
                //System.out.println("AIPLAYER - UBER LONG SEARC " + i + " " + (end2-end));
            }
            totaltime1 += end-start;
            totaltime2 += end2-end;

            /**
            if(Tester.debug){
                System.out.println("AI PLAYER TESTER INFOSSS");
                String[] info = Tester.getTesterInfo();
                for(String s : info) System.out.println(s);
                Tester.resetTesterInfo();
            }**/


        }
        if(Tester.debug){
            //System.out.println("AIPLAYER - avgclonelength " + (totaltime1/numRounds));
            //System.out.println("AIPLAYER - avgsearclength " + (totaltime2/numRounds));

        }
        Tester.roundCount += numRounds;

        //System.out.println("PLAYER - debug point check valid moves stuff");

        allTimeMaxDepth = Math.round(Math.max(allTimeMaxDepth, depthCalculator.getLifetimeMax()));

        /**
         System.out.println("AIPLAYER ROOT HASH: " + root.DEBUG_HASH);
         if(root.DEBUG_HASH == 924468757864899900L){
         int DEBUGGGG = 1;
         }**/


        //String[] test = root.ucbTracker.testReport();
        //for(String t : test) System.out.println(t);
        /**
         MonteCarloTreeNode check = root.children[28];
         if(check != null){
         System.out.println("/  /  /");
         String debug = "MCPARAMS BONUSES: ";
         for (int i = 0; i < check.ucbBonuses.length; i++) {
         debug += " move " + i + ": " + check.ucbBonuses[i] + " ";
         }
         System.out.println(debug);
         System.out.println("VALID MOVES " + check.validMoves);
         String[] dsreport = check.confTracker.testReport();
         System.out.println(dsreport[1]);
         System.out.println(dsreport[2]);
         System.out.println(dsreport[5]);
         }**/
        return;
    }
    public void chooseMove(int numRounds){
        chooseMove(numRounds, numRounds);
    }

    public void playMove(int player, int moveID){
        gameAI.playMove(player, moveID);
        //String rawmove = move.substring(1);
        //int moveID = params.gameParams.moveConverter.convertMoveToID(rawmove);
        root = root.children[moveID];
        if(root == null) root = new MonteCarloTreeNode(this, numMoves, gameAI.getCurrentPlayer(), 0);

        //if(rave != null) rave.testPrint();
        if(rave != null) rave.reduceWeights(params.rave.raveAmplify);
        depthCalculator.reset();
    }

    public void changeEstProgress(float change){
        estProgress += change;
        if(estProgress > 1) estProgress = 1;
        if(estProgress < 0) estProgress = 0;
    }
    public void resetProgress(){
        estProgress = 0;
    }

    public void end(){
        table = null;
        root = null;
        depthCalculator = null;
        //System.out.println("AIPLAYER ALL TIME MAX DEPTH: " + allTimeMaxDepth);
        allTimeMaxDepth = 0;
    }
}
