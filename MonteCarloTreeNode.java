import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MonteCarloTreeNode {

    int gameResult;
    AIPlayer player;

    MonteCarloTreeNode[] children;
    boolean[] childrenSet;
    List<Integer> winningMoves;
    List<Integer> tieingMoves;
    List<Integer> losingMoves;
    List<Integer> validMoves;
    int numChildren;

    long DEBUG_HASH;

    int grandDepth;

    float totalPoints;
    long totalGamesPlayed;
    float[] moveRoutePoints;
    long[] moveRouteGames;

    float[] priorPoints;
    float[] priorBase;
    float totalPrior;

    int totalUpdates = 0;

    float trueConfidence;
    float avgConfidence;
    DSMinimum2 confTracker;
    long totalWeight;

    //DSMinimum2 class is the new way to keep track of UCB values
    DSMinimum2 ucbTracker;
    TreeSet<MoveValuePair> valueTracker;

    float[] ucbBonuses;

    /**
     * CHILD INFO RECORDS
     * Stores the last known 3Confidence and Simulation Count
     * With a transposition table a parent's child node can be updated without an update to the parent
     * These values ensure that new node values are calculated correctly
     * **/
    //CURRENTLY KIND OF DEPRECATED: WILL FIX LATER I DUNNO
    float[] childConfidence;
    float[] childSims;



    int currentPlayer;

    int loopsToRecalcUCB;
    int loopsToRecalcBonus;

    List<Parent> parents;

    static class Parent{
        MonteCarloTreeNode parent;
        int parentMoveID;
        public Parent(MonteCarloTreeNode p, int m){
            //parent = p;
            parentMoveID = m;
        }
    }

    public MonteCarloTreeNode(AIPlayer ai, int numMoves, int playernum, int gameResult){
        initialize(ai, numMoves, playernum, gameResult);
    }


    private void initialize(AIPlayer ai, int numMoves, int playernum, int gr){
        gameResult = gr;
        currentPlayer = playernum;
        player = ai;
        grandDepth = 0;
        children = new MonteCarloTreeNode[numMoves];
        childrenSet = new boolean[numMoves];
        childConfidence = new float[numMoves];
        childSims = new float[numMoves];
        numChildren = 0;
        totalPoints = 0f;
        totalGamesPlayed = 0;
        moveRoutePoints = new float[numMoves];
        moveRouteGames = new long[numMoves];
        priorBase = new float[numMoves];
        priorPoints = new float[numMoves];
        totalWeight = 0;
        validMoves = new ArrayList<>();
        losingMoves = new ArrayList<>();
        winningMoves = new ArrayList<>();
        tieingMoves = new ArrayList<>();
        loopsToRecalcUCB = 0;

        if(player.params.usenewUCBTracker){
            //wait until we have valid move list to initialize
            if(player.params.trackerCrossCheck){
                valueTracker = new TreeSet<>();
            }
        }else{
            valueTracker = new TreeSet<>();
        }
        ucbBonuses = new float[numMoves];

        parents = new ArrayList<>();
    }

    public void addParent(MonteCarloTreeNode par, int move){
        //parents.add(new Parent(par, move));
    }

    private void expansion(AIGameTracker currGame){

        long a = Tester.getTesterTime();
        currentPlayer = currGame.getCurrentPlayer();

        List<Integer> expansions = new ArrayList<>();
        if(currGame.moveSupplier != null) expansions = currGame.moveSupplierGetValidMoves(player.params.moveSupplyFilter);
        else if (validMoves.size() > 0) expansions = validMoves;
        else{
            for (int moveID = 0; moveID < children.length; moveID++) {
                expansions.add(moveID);
            }
        }

        //if(validMoves.size() == 0 && currGame.moveSupplier != null) validMoves = new ArrayList<>(expansions.size());
        validMoves = new ArrayList<>();
        for(int i :expansions) validMoves.add(i);

        //if(DEBUG_HASH == 0) System.out.println("MCTN Expansion " + validMoves + " " + this.grandDepth + " " + player.root.grandDepth);
        //System.out.println("MCTN Expansion " + validMoves + " " + this.grandDepth + " " + player.root.grandDepth);
        confTracker = new DSMinimum2(true, validMoves);

        //takes some time to calculate how to save some memory
        //ucbTracker = new DSMinimum2(validMoves);
        //more memory but slightly faster
        ucbTracker = new DSMinimum2(true, validMoves, player.numMoves);
        List<Float> initialValues = new ArrayList<>();

        float inf = player.params.infEval;

        player.params.domain.calculateNodePrior(this, currGame, validMoves);

        for (int moveID : validMoves) {
            float val = player.params.calculateUCBValue(null, this, moveID);
            val += player.params.rave.calculateRaveValue(null, this, moveID, player.rave);
            //val += ucbBonuses[moveID]; no bonuses in initialization

            if(player.params.domain.usePriors){
                initialValues.add(val);
            }else{
                initialValues.add(inf);

            }
        }

        ucbTracker.updateAllValues(initialValues);


        long b = Tester.getTesterTime();
        //System.out.println(b-a);
        Tester.expansion += b-a;

        //System.out.println("MCTN EXPAND TIME " + (b-a));
    }

    public MoveValuePair updateValue(int childMoveID){
        if(childMoveID == 43 && this == player.root && this.totalGamesPlayed >= 450000){
            int DEBUGCATCH = 0;
        }
        MoveValuePair newMVP;

        if(!childrenSet[childMoveID]) {
            newMVP = new MoveValuePair(childMoveID, player.params.infEval);

        }
        else{
            MonteCarloTreeNode child = children[childMoveID];
            float newValue = 0;


            if(child.gameResult != 0) {
                if (child.gameResult == this.currentPlayer) newValue = player.params.infEval;
                else if (child.gameResult == -1) {
                    float tieP = player.params.tiePoints;
                    if (currentPlayer == player.aiPlayerNum) tieP = 1 - tieP;

                    newValue = player.params.calculateUCBValueTie(child, this, tieP);
                    newValue += player.params.rave.calculateRaveValue(child, this, childMoveID, player.rave);
                    newValue += ucbBonuses[childMoveID];
                    //newValue = player.params.nodeUCBFunction(child.gamesPlayed * tieP, child.gamesPlayed, this.gamesPlayed);
                } else newValue = 0;
            }
            else{
                //float trueChildPoints = child.points;
                //if(currentPlayer != player.aiPlayerNum) trueChildPoints = child.gamesPlayed - child.points;
                //System.out.println("update " + currentPlayer + " " + childMoveID +" " + child.points + " " + trueChildPoints + " " + child.gamesPlayed + " " + this.gamesPlayed);

                newValue = player.params.calculateUCBValue(child, this, childMoveID);
                newValue += player.params.rave.calculateRaveValue(child, this, childMoveID, player.rave);
                newValue += ucbBonuses[childMoveID];
                //newValue = player.params.nodeUCBFunction(child.gamesPlayed - child.points, child.gamesPlayed, this.gamesPlayed);
            }
            //if(newValue < -0.5f) System.out.println("MCTN ERROR VALUE UNDER 0: debugKey " + debugKey );
            newMVP = new MoveValuePair(childMoveID, newValue);
        }

        return newMVP;




    }

    private void recalcValues(){
        if(Tester.debug) System.out.println("MCTN calls the recalc");
        if(validMoves.size() == 0) return;
        loopsToRecalcUCB = 0;
        //System.out.println("MCTN recalcing a thing " + this);

        //ucbTracker = new DSMinimum2(validMoves, player.numMoves);

        List<Float> newValues = new ArrayList<>();
        for(int move : validMoves){
            newValues.add(updateValue(move).value);
        }
        ucbTracker.updateAllValues(newValues);

        /**
        try{

        }
        catch(Exception up){
            System.out.println("MCTN UCBTRACKER: "  + ucbTracker);
            System.out.println("MCTN valid moves " + validMoves);
            throw up; //hehe
        }**/

    }

    public void selection(int prevResult, AIGameTracker currGame, List<MonteCarloTreeNode> path, List<Integer> moveSeq){
        boolean rootselection = path.size() == 0;
        if(rootselection) Tester.rootSelection -= Tester.getTesterTime();

        //System.out.println("start " + Tester.selection);
        //System.out.printf("loopy");
        long a = 0;

        path.add(this);

        //a -= Tester.getTesterTime();

        //prev result is the result for the current game position
        if(prevResult != 0 || this.gameResult != 0){
            //a += Tester.getTesterTime();

            terminalBackpropagationPrep(path, moveSeq);

        }else{
            //a += Tester.getTesterTime();

            boolean expand = this.validMoves.size() == 0;
            //System.out.printf("MCTNH" + DEBUG_HASH+" ");

            Tester.checkPoint("MCTN pre-expand");

            if(expand){
                //LEAF NODE REACHED

                //a += Tester.getTesterTime();
                expansion(currGame);

                //a -= Tester.getTesterTime();
            }

            Tester.checkPoint("MCTN post-expand");

            //a -= Tester.getTesterTime();
            a -= Tester.pollValue;
            a -= Tester.selectionGame;
            a -= Tester.setupSelect;

            //if(this == player.root && this.gamesPlayed < 1) System.out.println("INITIAL " + valueTracker);

            Tester.pollValue -= Tester.getTesterTime();

            MoveValuePair mvp;
            if(player.params.usenewUCBTracker){
                mvp = ucbTracker.getMVPair();
                if(player.params.trackerCrossCheck){
                    mvp = valueTracker.pollLast();
                }
            }else{
                mvp = valueTracker.pollLast();
            }
            int mvpmove = mvp.moveID;
            if(winningMoves.size() != 0){
                mvpmove = winningMoves.get(0);
            }
            Tester.pollValue += Tester.getTesterTime();
            //System.out.println("MCTN move " + mvpmove + " " + winningMoves);


            //System.out.println("MCTN " + this + " " + valueTracker);

            Tester.selectionGame -= Tester.getTesterTime();

            //String play = player.params.gameParams.moveConverter.convertIDtoMove(mvpmove);
            //int result = currGame.playMove(currentPlayer+""+play);
            int result = currGame.playMove(currentPlayer, mvpmove);
            moveSeq.add(mvpmove);

            Tester.selectionGame += Tester.getTesterTime();

            Tester.setupSelect -= Tester.getTesterTime();

            Tester.checkPoint("MCTN start child-set");

            if(!childrenSet[mvpmove]){
                int moveID = mvpmove;
                //System.out.printf("MCTNconfig " + moveID + " ");
                if(result == -2){
                    System.out.println("BAD - MonteCarloTreeNode selection function");
                    System.out.println(this + "  " + this.validMoves + " " + moveID);
                    System.out.println(valueTracker);

                    String[] s = currGame.stateString();
                    for(String ss : s) System.out.println(ss);

                    //Valid move is not actually a valid move; delete it
                    //im super lazy and dont want to code a while loop and so im using recursion on self to redo this part
                    //hopefully this code never needs to actually run because then it will die lmao
                    validMoves.remove((Object)moveID);
                    path.remove(this);
                    moveSeq.remove((int)moveSeq.size()-1);

                    //now that value tracker is like retired idk what to replace this with
                    valueTracker.remove(mvp);

                    //this game is still valid because an invalid move was inputted
                    this.selection(prevResult, currGame, path, moveSeq);
                }
                numChildren++;
                if(result != 0){
                    if(result == -1){
                        tieingMoves.add(moveID);
                    }
                    else if(result == currentPlayer){
                        winningMoves.add(moveID);
                    }else{
                        //for most other games this is impossible, you can't lose on your current turn
                        //HOWEVER, with the change so that in case of tie most boxes act as tiebreaker, losing moves are possible
                        //System.out.println("ALSO BAD - MonteCarloTreeNode selection function");
                        //System.out.println(this + "  " + this.validMoves + " " + moveID);
                        losingMoves.add(moveID);
                    }
                }
                long hash = currGame.getHash();
                //System.out.println("MCTN hash " + hash);

                MonteCarloTreeNode child;
                if(player.table != null && player.table.containsKey(hash)){
                    System.out.println("MCTN use hash");
                    child = player.table.get(hash);
                }else{
                    int nextPlayer = 3-this.currentPlayer;
                    child = new MonteCarloTreeNode(player, children.length, nextPlayer, result);
                    if(player.table != null) player.table.put(hash, child);
                }
                child.gameResult = result;
                child.DEBUG_HASH=hash;

                children[moveID] = child;

                childrenSet[moveID] = true;
                children[moveID].grandDepth = this.grandDepth + 1;
            }
            else{
                //System.out.println("MCTN: Children set fails");
            }
            MonteCarloTreeNode child = children[mvpmove];
            child.addParent(this, mvpmove);

            Tester.checkPoint("MCTN end child-set");


            Tester.setupSelect += Tester.getTesterTime();

            a += Tester.pollValue;
            a += Tester.selectionGame;
            a += Tester.setupSelect;

            if(expand && result != 0){
                terminalBackpropagationPrep(path, moveSeq);
            }else if(expand){

                path.add(child);
                int simulationsToPlay = player.params.simulationsPerRound;

                float tieP = player.params.tiePoints;


                Tester.simulation -= Tester.getTesterTime();
                float pointsWon = player.simulator.simulateRounds(player.aiPlayerNum, simulationsToPlay, currGame, tieP,
                        player.rave);
                Tester.simulation += Tester.getTesterTime();
                backpropagation(player.aiPlayerNum, pointsWon, simulationsToPlay, path, moveSeq, false);
            }else{
                child.selection(result, currGame, path, moveSeq);
            }

            a -= Tester.getTesterTime();
            Tester.updateValue -= Tester.getTesterTime();

            MoveValuePair newmvp = updateValue(mvpmove);
            if(player.params.usenewUCBTracker){
                //System.out.println("MCTN SELECTION update the ucb man " + newmvp);
                ucbTracker.updateKey(newmvp.moveID, newmvp.value);
                if(player.params.trackerCrossCheck){
                    valueTracker.add(newmvp);
                }
            }else{
                valueTracker.add(newmvp);
            }

            Tester.checkPoint("MCTN end simulations");



            Tester.updateValue += Tester.getTesterTime();

            a += Tester.getTesterTime();
        }
        a -= Tester.getTesterTime();
        Tester.recalcValue -= Tester.getTesterTime();

        this.loopsToRecalcUCB += 1;
        if(this.loopsToRecalcUCB >= player.params.recalcUCBCooldown) this.recalcValues();

        this.loopsToRecalcBonus += 1;
        if(this.loopsToRecalcBonus >= player.params.recalcBonusCooldown){
            ucbBonuses = player.params.calcUCBBonuses(this);
            this.loopsToRecalcBonus -= player.params.recalcBonusCooldown;
        }

        Tester.recalcValue += Tester.getTesterTime();
        a += Tester.getTesterTime();
        Tester.selection += a;
        //System.out.println("end " + Tester.selection);

        if(rootselection) Tester.rootSelection += Tester.getTesterTime();
    }

    private void terminalBackpropagationPrep(List<MonteCarloTreeNode> path, List<Integer> moveSeq){
        //TERMINAL NODE REACHED; immediate backpropagation
        int simulationsToPlay = player.params.simulationsPerRound;
        float pointsWon = simulationsToPlay;

        if(gameResult == -1){
            if(gameResult != player.aiPlayerNum) pointsWon *= player.params.tiePoints;
            else pointsWon *= (1-player.params.tiePoints);
        }
        else if(gameResult != player.aiPlayerNum) pointsWon = 0;

        backpropagation(player.aiPlayerNum, pointsWon, simulationsToPlay, path, moveSeq, true);
    }


    static class ConfidenceChange{
        float oldConfidence;
        float newConfidence;
        float oldSims;
        float newSims;
        float extraSims;

        @Override
        public String toString() {
            return "{" +
                    "oldc=" + oldConfidence +
                    ", newc=" + newConfidence +
                    ", olds=" + oldSims +
                    ", news=" + newSims +
                    '}';
        }
    }

    private void backpropagation(int aiPlayer, float points, int totalSims, List<MonteCarloTreeNode> path,
                                 List<Integer> moveSeq, boolean isTerminal){
        player.lastIsRolloutTerminal = isTerminal;
        Tester.backpropagation -= Tester.getTesterTime();
        player.lastDepth = path.size();

        ConfidenceChange curr = null;
        ConfidenceChange next = new ConfidenceChange();
        for(int nodeNum = path.size()-1; nodeNum >= 0; nodeNum--){
            MonteCarloTreeNode node = path.get(nodeNum);
            if(node == null) continue;
            //if(node.gameResult != 0) continue;
            float truePoints = points;
            if(node.currentPlayer != aiPlayer) truePoints = totalSims - points;

            next.oldConfidence = 1-node.trueConfidence;
            //System.out.println("MCTN backprop " + path.size() + " " +moveSeq.size() + " " + nodeNum);
            if(nodeNum > 0) next.oldConfidence = 1-path.get(nodeNum-1).childConfidence[moveSeq.get(nodeNum-1)];

            next.oldSims = node.totalGamesPlayed;
            if(nodeNum > 0) next.oldSims = path.get(nodeNum-1).childSims[moveSeq.get(nodeNum-1)];

            //node.points += truePoints;
            //node.gamesPlayed += totalSims;
            MonteCarloTreeNode parent = (nodeNum > 0) ? path.get(nodeNum-1) : null;
            int curMoveID = nodeNum < moveSeq.size() ? moveSeq.get(nodeNum) : -1;
            int prevMoveID = nodeNum > 0 ? moveSeq.get(nodeNum-1) : -1;
            node.updateConfidence(curr, parent, truePoints, totalSims, prevMoveID, curMoveID);
            //System.out.println(node.trueConfidence);
            if(nodeNum > 0){
                path.get(nodeNum-1).childConfidence[moveSeq.get(nodeNum-1)] = node.trueConfidence;
                next.extraSims = node.totalGamesPlayed - path.get(nodeNum-1).childSims[moveSeq.get(nodeNum-1)] - totalSims;
                //path.get(nodeNum-1).childSims[moveSeq.get(nodeNum-1)] += totalSims;
                path.get(nodeNum-1).childSims[moveSeq.get(nodeNum-1)] = node.totalGamesPlayed;
            }

            next.newConfidence = 1-node.trueConfidence;
            next.newSims = node.totalGamesPlayed;

            curr = next;
            next = new ConfidenceChange();

            node.totalUpdates++;
        }

        Tester.backpropagation += Tester.getTesterTime();
    }

    private void updateConfidence(ConfidenceChange change, MonteCarloTreeNode parent, float totalPoints, int totalSims,
                                  int prevMoveID, int curMoveID){
        if(change == null){
            //initial node in the backpropagation sequence: LAST NODE, leaf
            this.totalPoints += totalPoints;
            totalGamesPlayed += totalSims;

            if(parent != null){
                parent.moveRoutePoints[prevMoveID] += totalPoints;
                parent.moveRouteGames[prevMoveID] += totalSims;
            }


            totalWeight += weightFunction(totalSims);
            avgConfidence = this.totalPoints /(float)totalGamesPlayed;

            //float inputConf;
            //if(currentPlayer == player.aiPlayerNum) inputConf = 1-avgConfidence;
            //else inputConf = avgConfidence;

            if(curMoveID != -1 && confTracker != null) confTracker.updateKey(curMoveID, avgConfidence);
            //if(minConfTracker == null || moveID == -1) System.out.println("skipped " + minConfTracker);

        }else{
            totalSims += change.extraSims;
            //if(change.extraSims > 0.01f) System.out.println("EX SIMS " + change.extraSims);
            if(player.params.useWeightFunction){
                System.out.println("FIX WEIGHT STUFF REEEEEEE");
                //PROBABLY BROKEN AND I DONT WANNA FIX IT. Too bad!
                //probably has something do with gamesPlayed/weight calculations in a  transposition table
                //i havent actaully tested it so maybe its fine
                this.totalPoints += totalPoints;
                this.totalGamesPlayed += totalSims;

                if(parent != null){
                    parent.moveRoutePoints[prevMoveID] += totalPoints;
                    parent.moveRouteGames[prevMoveID] += totalSims;
                }

                float oldSum = avgConfidence * totalWeight;
                float newSum = oldSum + ((change.newConfidence) * weightFunction(change.newSims)) - ((change.oldConfidence) * weightFunction(change.oldSims));
                totalWeight += weightFunction(change.newSims) - weightFunction(change.oldSims);
                avgConfidence = newSum / totalWeight;

                float inputConf;
                if(currentPlayer == player.aiPlayerNum) inputConf = 1-change.newConfidence;
                else inputConf = change.newConfidence;

                if(curMoveID != -1 && confTracker != null) confTracker.updateKey(curMoveID, change.newConfidence);
            }else{
                this.totalPoints += totalPoints;
                float oldSum = avgConfidence * this.totalGamesPlayed;
                float newSum = oldSum + ((change.newConfidence) * change.newSims) - ((change.oldConfidence) * change.oldSims);
                totalGamesPlayed += totalSims;

                /**
                 parent.moveRoutePoints[prevMoveID] += totalPoints;
                 oldSum = avgConfidence * parent.moveRouteGames[prevMoveID];
                 newSum = oldSum + ((change.newConfidence) * change.newSims) - ((change.oldConfidence) * change.oldSims);
                 parent.moveRouteGames[prevMoveID] += totalSims;**/

                avgConfidence = newSum / totalGamesPlayed;


                float inputConf;
                if(currentPlayer == player.aiPlayerNum) inputConf = 1-change.newConfidence;
                else inputConf = change.newConfidence;

                if(curMoveID != -1 && confTracker != null) confTracker.updateKey(curMoveID, change.newConfidence);
            }
        }
        trueConfidence = player.params.calcTrueConfidence(this);

        /**
         if(totalWeight-gamesPlayed != 0){
         //System.out.println("WEIGHT ERROR");
         }

         if(Math.abs(avgConfidence - points/(float)gamesPlayed) > 0.001){
         //System.out.println("CONF ERR " + avgConfidence + " vs " + (float)points/(float)gamesPlayed + " -- " + points + " " + gamesPlayed + " " + change + " -- " + this);
         }**/
    }

    private float weightFunction(float simsPlayed){
        //if(player.params.confCalculatorKey == 2) return (float)Math.pow(simsPlayed, 1.8f);
        if(player.params.confCalculatorKey == 2 && this.grandDepth >=  40) return simsPlayed * simsPlayed;
        return simsPlayed;
    }

    public int bestWinRate(List<Integer> excluded, boolean opponent){
        //System.out.println("MCTN Query " + this + " " + this.DEBUG_HASH + " vm: " + validMoves + " exc: " + excluded + " pvm: " + player.root.validMoves + " " + player.root.grandDepth);
        if(validMoves.size() == 0){
            int catchy = 1;
            return -1;
        }
        int bestmove = -1;
        float winRate = -0.5f;
        if(opponent) winRate = 1.5f;
        for(int move : validMoves){
            if(!childrenSet[move]) continue;
            if(excluded.contains(move)) continue;
            if(bestmove == -1) bestmove = move;
            MonteCarloTreeNode child2 = children[move];
            float newRate;
            if(child2.gameResult == player.aiPlayerNum) return move;
            else if(child2.gameResult == -1) newRate = player.params.tiePoints;
            else if(child2.gameResult == 0) newRate = child2.calcWinRate();
            else newRate = 0; //LOSING MOVE
            if(opponent){
                if(newRate < winRate){
                    winRate = newRate;
                    bestmove = move;
                }
            }else{
                if(newRate > winRate){
                    winRate = newRate;
                    bestmove = move;
                }
            }
        }
        //if(bestmove == -1) bestmove = validMoves.get(0);
        //System.out.println("MCTN query " + bestmove + " " + validMoves);
        return bestmove;
    }

    public int bestSearched(){
        if(validMoves.size() == 0) return -1;
        int bestmove = validMoves.get(0);
        MonteCarloTreeNode child = children[validMoves.get(0)];
        if(child == null) return -1;
        long winPlayed = child.totalGamesPlayed;
        for(int move : validMoves){
            MonteCarloTreeNode child2 = children[move];
            if(child2 == null) continue;
            long newPlayed = child2.totalGamesPlayed;
            if(newPlayed  > winPlayed){
                winPlayed = newPlayed;
                bestmove = move;
            }
        }
        return bestmove;
    }
    public float getWinRateOfMove(int move){
        if(move == -1) return 0; //bro somehow this actually triggered idefk
        MonteCarloTreeNode child = children[move];
        float winRate = child.calcWinRate();
        return winRate;
    }
    public long getTotalSimulationsOfMove(int move){
        MonteCarloTreeNode child = children[move];
        return child.totalGamesPlayed;
    }
    public long getTotalUpdatesOfMove(int move){
        MonteCarloTreeNode child = children[move];
        return child.totalUpdates;
    }

    public float calcWinRate(){
        int otherPlayer = 1;
        if(player.aiPlayerNum == 1) otherPlayer = 2;

        if(this.gameResult == player.aiPlayerNum) return 1;
        else if(this.gameResult == otherPlayer) return 0;
        else if(this.gameResult == -1) return player.params.tiePoints;

        /**
         float truePoints;
         if(this.currentPlayer == player.aiPlayerNum) truePoints = this.points;
         else truePoints = this.gamesPlayed - this.points;
         float value = truePoints / (float) this.gamesPlayed;
         return value;
         **/

        float trueConf;
        if(this.currentPlayer == player.aiPlayerNum) trueConf = this.trueConfidence;
        else trueConf = 1 - this.trueConfidence;
        return trueConf;
    }
}
