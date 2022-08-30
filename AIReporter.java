import java.util.ArrayList;
import java.util.List;

import java.time.Duration;
import java.time.Instant;

public class AIReporter {

    ConfigReporter reportConfig;


    long startPlay = 0;
    long startCycle = 0;
    long timeSpentThisPlay;


    public AIReporter(ConfigReporter config){
        reportConfig = config;
    }

    //returns best move
    public String printClassicReport(AIPlayer player, int numRounds, boolean finalReport){
        //System.out.println("time " + System.currentTimeMillis(););

        int bestMoveID = player.root.bestWinRate(new ArrayList<>(), false);
        int playedMove = bestMoveID;
        float confidence = player.root.getWinRateOfMove(bestMoveID);
        int scale = (int)(confidence * 1000f);

        IConverter conv = player.params.gameParams.moveConverter;
        String play = conv.convertIDtoMove(bestMoveID);
        String display = conv.convertRawToUser(play);

        long childSimulations = player.root.getTotalSimulationsOfMove(bestMoveID);
        long totalSimulations = player.root.totalGamesPlayed;

        if( (finalReport && reportConfig.printFinalReport) || (!finalReport && reportConfig.printAllReports) ){
            //System.out.println(">> DEBUG " + player.root.totalGamesPlayed + " " + player.root.totalUpdates);
            System.out.println(">>>>>> AI REPORT: " + numRounds + " rds; PLAY: " + display + " ; CONF. " + scale +" ; CHILD SIMS: " + childSimulations+" ; TOTAL SIMS: " + totalSimulations);
        }

        long cycleTime = timeSinceStartCycle();
        if(!finalReport) timeSpentThisPlay += cycleTime;
        if(reportConfig.printAllTimeTaken && startCycle != 0 && !finalReport){
            long msCycle = cycleTime/(long)1E6;
            long msStart = timeSinceStartPlay()/(long)1E6;
            long msRaw = timeSinceRawStartPlay()/(long)1E6;
            System.out.println(">>>>>> Time: Cycle - " + msCycle + " ms ; Total - " + msStart + " ms ; raw - " + msRaw + " ms");
            if(msCycle > 101) System.out.println("OVER 100 MS REEE");
        }

        //if(finalReport) Tester.printTesterInfo();

        return play;
    }

    /**
     * Print AI Recommendations for opponent for the top move the AI is considering playing
     * @param finalPlay Whether this is the move the AI is playing; affects whether or not to print the recs
     */
    public void printBestAIRecs(AIPlayer player, boolean finalPlay){
        int bestMoveID = player.root.bestWinRate(new ArrayList<>(), false);
        int confidence = (int)(player.root.getWinRateOfMove(bestMoveID)*1000f);
        if( !(!finalPlay && reportConfig.printAllRecs) && !(finalPlay && reportConfig.printFinalRecs) ) return;
        RecsInfo inf = getAIRecs(player, player.root.children[bestMoveID], true);
        if(inf == null){
            System.out.println("AIReporter Error - RecsInfo null - recs");
            return;
        }
        String s = ">>>>>> RECOMMENDATIONS: ";
        for (int moveNum = 0; moveNum < inf.recMoves.size(); moveNum++) {
            s += "PL: " + inf.recMoves.get(moveNum) + " C. " + inf.confidenceList.get(moveNum) + " ; ";
        }
        if(inf.recMoves.size() == 0 && confidence >= 999 && player.root.grandDepth > 0) s += "take the phat L";
        if(inf.removedMoves){
            s+= "  [some moves were removed due to lack of simulations]";
        }
        System.out.println(s);
        System.out.println(">>>>>> Rec Updates: " + inf.simCountList);
    }

    /**
     * Print all of the considered moves for the AI, and the confidence and # of simulations for each
     * @param finalPlay Whether this is the move the AI is playing; affects whether or not to print the recs
     */
    public void printAIConsidered(AIPlayer player, boolean finalPlay){
        if( !(!finalPlay && reportConfig.printAllMovesConsidered) && !(finalPlay && reportConfig.printFinalMovesConsidered) ) return;
        RecsInfo inf = getAIRecs(player, player.root, false);
        if(inf == null){
            System.out.println("AIReporter Error - RecsInfo null - cons");
            return;
        }
        String s = ">>>>>> CONSIDERED: ";
        for (int moveNum = 0; moveNum < inf.recMoves.size(); moveNum++) {
            s += "PL: " + inf.recMoves.get(moveNum) + " C. " + inf.confidenceList.get(moveNum) + " ; ";
        }
        if(inf.removedMoves){
            s+= "  [some moves were removed due to lack of simulations]";
        }
        System.out.println(s);
        System.out.println(">>>>>> Cons Updates: " + inf.simCountList);
    }

    static class RecsInfo{
        List<String> recMoves;
        List<Integer> confidenceList;
        List<Long> simCountList;
        boolean removedMoves;
        public RecsInfo(List<String> a, List<Integer> b, List<Long> c, boolean d){
            recMoves = a; confidenceList = b; simCountList = c; removedMoves = d;
        }
    }
    private RecsInfo getAIRecs(AIPlayer player, MonteCarloTreeNode node, boolean isOpponent){
        List<Integer> reviewedMoveID = new ArrayList<>();
        List<Integer> recMoveID = new ArrayList<>();
        List<String> recMoves = new ArrayList<>();
        List<Integer> confidenceList = new ArrayList<>();
        List<Long> updateCountList = new ArrayList<>();

        //some moves have inaccruate confidence levels due to no simulations (caused when there is an instant win move)
        boolean removedMoves = false;
        int skipCount = 0;

        for (int i = 0; i < reportConfig.numRecs; i++) {
            int goodMove = node.bestWinRate(reviewedMoveID, isOpponent);
            if(goodMove == -1) break; //ran out of legal moves
            float conf = node.getWinRateOfMove(goodMove);
            long totalUpdates = node.getTotalUpdatesOfMove(goodMove);
            reviewedMoveID.add(goodMove);
            if(totalUpdates < 2){
                removedMoves = true;
                skipCount++;
                if(skipCount < 100) i--;
                continue;
            }
            recMoveID.add(goodMove);
            confidenceList.add((int)(conf * 1000f));
            updateCountList.add(totalUpdates);
        }
        for(int m : recMoveID){
            IConverter conv = player.params.gameParams.moveConverter;
            recMoves.add(conv.convertRawToUser(conv.convertIDtoMove(m)));
        }

        RecsInfo inf = new RecsInfo(recMoves, confidenceList, updateCountList, removedMoves);
        return inf;
    }

    public void printSearchDepth(AIPlayer player, boolean finalPlay){
        if( !(!finalPlay && reportConfig.printAllSearchDepth) && !(finalPlay && reportConfig.printFinalSearchDepth) ) return;
        float windowDepth = player.depthCalculator.getWindowAverage();
        float lifetimeDepth = player.depthCalculator.getLifetimeAverage();
        int maxDepth = Math.round(player.depthCalculator.getLifetimeMax());
        int sizeDepth = player.depthCalculator.capacity;
        String str = String.format(">>>>>> MAX DEPTH: %d;  WINDOW DEPTH: %.2f (size %d); ROOT DEPTH: %.2f", maxDepth
                , windowDepth, sizeDepth, lifetimeDepth);
        System.out.println(str);

        //OK TERMINAL ROLLOUT PROPORTION IS ALSO HERE IG
        float windowTRO = player.tRolloutTracker.getWindowAverage();
        int windowTROpc = (int)(windowTRO * 100);
        int sizeTRO = player.tRolloutTracker.capacity;
        String str2 = String.format(">>>>>> TERMINAL RO: %d%% (size %d)", windowTROpc, sizeTRO);
        System.out.println(str2);
    }
    public void printVariations(AIPlayer player, IConverter conv, boolean finalPlay){
        if( !(!finalPlay && reportConfig.printAllVariations) && !(finalPlay && reportConfig.printFinalVariations) ) return;
        int variationDepth = 7;

        List<MonteCarloTreeNode> nodeSequence = new ArrayList<>();
        nodeSequence.add(player.root);
        List<Integer> moveIDsequence = new ArrayList<>();
        List<Float> confSequence = new ArrayList<>();
        List<Integer> updateCounts = new ArrayList<>();
        for (int depth = 0; depth < variationDepth; depth++) {
            MonteCarloTreeNode currentNode = nodeSequence.get(nodeSequence.size()-1);
            if(currentNode == null || currentNode.children == null) break;

            RecsInfo recs = getAIRecs(player, currentNode, depth % 2 == 1);
            int bestMoveIDHere = currentNode.bestWinRate(new ArrayList<>(), depth % 2 == 1);
            bestMoveIDHere = currentNode.bestSearched();
            if(bestMoveIDHere == -1) break;
            moveIDsequence.add(bestMoveIDHere);
            nodeSequence.add(currentNode.children[bestMoveIDHere]);

            updateCounts.add(currentNode.totalUpdates);
            confSequence.add(currentNode.getWinRateOfMove(bestMoveIDHere));
        }

        String moveStr = "";
        for(int i = 0; i < moveIDsequence.size(); i++){
            if(i > 0){
                if((i + player.root.currentPlayer) % 2 == 0) moveStr += "/";
                else moveStr += " ";
            }else{
                if(player.root.currentPlayer == 2) moveStr += "...";
            }
            moveStr += conv.convertRawToUser(conv.convertIDtoMove(moveIDsequence.get(i)));

            if(i == moveIDsequence.size()-1 && (i + player.root.currentPlayer) % 2 == 1){
                moveStr += "...";
            }
        }
        String confStr = "C. ";
        for(int i = 0; i < confSequence.size(); i++){
            if(i > 0){
                confStr += ">";
            }
            confStr += (int)(1000f*confSequence.get(i));
        }
        String simStr = "UPDATES. ";
        for(int i = 0; i < updateCounts.size(); i++){
            if(i > 0){
                simStr += "-";
            }
            simStr += updateCounts.get(i);
        }
        String output = ">>>>>> PRINC. VAR: " + moveStr + " ; " + simStr + " ; " + confStr;
        System.out.println(output);



        debugPLOPHeuristic(player, conv, finalPlay);
    }

    public void printCDG(String play, IConverter conv, AIPlayer player){
        if(!reportConfig.printCDGOutput) return;
        String moveCDG = conv.convertRawToCDG(play);
        int bestMoveID = player.root.bestWinRate(new ArrayList<>(), false);
        float confidence = player.root.getWinRateOfMove(bestMoveID);
        int intConf = (int)(1000*confidence);
        long updates = player.root.getTotalUpdatesOfMove(bestMoveID);

        System.out.println(moveCDG + " ; C. " + intConf + " ; U. " + updates + " ; Time(ms): " + timeSpentThisPlay);
    }

    public void printCalculatingMessage(int cycleNum, AIPlayer player){
        if(cycleNum == 0){
            startPlay = System.nanoTime();;
            timeSpentThisPlay = 0;
        }
        startCycle = System.nanoTime();;


        if(!reportConfig.printCalculatingMessage) return;
        System.out.println(">>>>>> Start: " + player.root.totalUpdates + " updates; " + player.root.totalGamesPlayed +
                " " + "games");
        System.out.println(">>>>>> CALCULATING... ");
    }
    public void printExecutedPlay(String play, IConverter conv){
        Tester.total += timeSpentThisPlay;
        if(reportConfig.printFinalTimeTaken && startPlay != 0){
            //System.out.println(Instant.now());
            System.out.println(">>>>>> Total Time Taken: " + timeSpentThisPlay + " ms ; (raw time: " + timeSinceRawStartPlay() + " ms)");
        }
        if(!reportConfig.printExecute) return;
        String display = conv.convertRawToUser(play);
        System.out.println(">>>>>> AI: EXECUTING PLAY " + display);
    }


    public long timeSinceRawStartPlay(){
        if(startPlay == 0) return -1;
        long timeNow = System.nanoTime();
        long timeElapsed = timeNow - startPlay;
        return timeElapsed;
    }
    public long timeSinceStartPlay(){
        return timeSpentThisPlay;
    }
    public long timeSinceStartCycle(){
        if(startCycle == 0) return -1;
        long timeNow = System.nanoTime();
        long timeElapsed = timeNow - startCycle;
        return timeElapsed;
    }


    public static void debugPriors(AIPlayer player, IConverter conv, boolean finalPlay){
        String s = "";
        for (int i = 0; i < player.root.validMoves.size(); i++) {
        int moveNum = player.root.validMoves.get(i);
        String m = conv.convertRawToUser(conv.convertIDtoMove(moveNum));
        s += "PL: " + m + " C. " + (int)(1000*player.root.priorPoints[moveNum]/player.root.priorBase[moveNum]) + " ; ";
        }
        System.out.println(">>>>>> NODE PRIORS " + s);

        String[] report = player.root.ucbTracker.testReport();
        System.out.println(report[1]);
        System.out.println(report[5]);
    }

    public static void debugPLOPHeuristic(AIPlayer player, IConverter conv, boolean finalPlay){

        HeuristicManager heuristics = player.gameAI.heuristics;

        System.out.println(">>>>>> DEBUG PLOP: Heuristic " + heuristics.getPositionalValue(1) + " / " + heuristics.getPositionalValue(2));
        System.out.println(">>>>>> DEBUG PLOP: abs sum " + heuristics.absoluteTemperatureSum);
    }
}
