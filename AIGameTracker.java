import java.util.List;

public class AIGameTracker implements ICloneable{

    IGame board;
    IStateHasher hasher;

    IMoveSupplier moveSupplier;

    IAISimulationInput simulationInput;

    HeuristicManager heuristics;

    public AIGameTracker(IGame g){
        board = g;
    }

    public IAISimulationInput getSimulationInput() {
        return simulationInput;
    }
    public void setSimulationInput(IAISimulationInput input) {
        simulationInput = input;
    }
    public void setMoveSupplier(IMoveSupplier moveSupplier) {
        this.moveSupplier = moveSupplier;
        this.moveSupplier.setBoard(board, true);
    }
    public void setHasher(IStateHasher hasher) {
        this.hasher = hasher;
        if(hasher == null) return;
        this.hasher.setBoard(board, true);
    }
    public void setHeuristics(){
        this.heuristics = new HeuristicManager();
        heuristics.setBoard((TixTaxBoard)board);
    }

    public List<Integer> moveSupplierGetValidMoves(EMoveSupplyFilter filterEquivalents) {
        return moveSupplier.getAllValidMoves(filterEquivalents);
    }

    /**
    public HashKey getHash() {
        if(hasher == null) return null;
        return hasher.makeHashKey();
    }
     **/

    public long getHash(){
        return board.getZobristHash();
    }


    public int getCurrentPlayer(){
        return board.getCurrentPlayer();
    }
    public void start(){
        board.start();
    }

    //STRING USE IS DEPRECATED
    /**
    public int playMove(String input){
        int result = board.playMove(input);
        if(result == -2) return result;

        if(moveSupplier != null) moveSupplier.playMove(input);
        if(hasher != null) hasher.playMove(input);
        //dont update the input; the input updates this class (Tracker)

        return result;
    }
     **/

    public int playMove(int player, int moveID){

        //CODE ASSUMES MOVE IS VALID
        //IF IT ISNT THEN THE FAULTY MOVE GETS COMMITTED TO GAMETRACKER COMPONENTS

        //Tester.gameMoveSupAITracker -= Tester.getTesterTime();
        if(moveSupplier != null) moveSupplier.playMove(player, moveID);
        //Tester.gameMoveSupAITracker += Tester.getTesterTime();

        if(hasher != null) hasher.playMove(player, moveID);
        //dont update the input; the input updates this class (Tracker)

        if(heuristics != null) heuristics.playMove(player, moveID);

        Tester.gamePlayAITracker -= Tester.getTesterTime();
        int result = board.playMove(player, moveID);
        if(result == -2) return result;
        Tester.gamePlayAITracker += Tester.getTesterTime();

        return result;
    }

    public String[] stateString(){
        return board.stateString();
    }

    public void resetInputTrackerCall(){
        simulationInput.setAIGameTracker(this);
    }

    @Override
    public Object clone() {
        long a = Tester.getTesterTime();
        IGame newg = (IGame) this.board.clone();

        AIGameTracker copy = new AIGameTracker(newg);
        long b = Tester.getTesterTime();
        if(this.moveSupplier != null) copy.moveSupplier = (IMoveSupplier) this.moveSupplier.clone();
        if(this.moveSupplier != null) copy.moveSupplier.setBoard(copy.board, false);
        if(this.hasher != null) copy.hasher = (IStateHasher) this.hasher.clone();
        if(this.hasher != null) copy.hasher.setBoard(copy.board, false);
        if(this.heuristics != null) copy.setHeuristics();
        long c = Tester.getTesterTime();

        copy.simulationInput = (IAISimulationInput) this.simulationInput.clone();
        copy.resetInputTrackerCall();

        Tester.cloneBoard += b-a;
        Tester.cloneOther += c-b;

        return copy;
    }
}


