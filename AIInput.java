import java.util.ArrayList;

public class AIInput implements IGameInput {

    AIPlayer player;
    int aiPlayerNum;
    IAISupervisor supervisor;

    IGame baseGame;

    public AIInput(IGame game, MonteCarloParameters parameters, int aiNum, IAISupervisor s) {
        aiPlayerNum = aiNum;
        baseGame = (IGame) game.clone();
        player = new AIPlayer(game, parameters.gameParams.moveConverter.getNumberOfMoves(), aiPlayerNum, parameters);
        player.simulator = new MonteCarloSimulator(); //if this changes, update ResetAI method
        supervisor = s;
    }

    public void refreshParams(MonteCarloParameters p){
        player.params = p;
        updateParams();
    }
    public void setParams(MonteCarloParameters p){
        player.params = p;
    }

    private void updateParams(){

    }

    public RealMove nextPlay(){
        Tester.playNum += 1;
        //System.out.println(Tester.playNum);


        String play = "";
        int roundnum = 0;
        while(true){
            supervisor.runningCycle(player);
            roundnum = supervisor.getRoundCount();

            String comm = supervisor.command(player);
            if(comm.length() < 3) continue;
            String prefix = comm.substring(0, 3);
            if(prefix.equals("go ")){
                play = comm.substring(3);
                //System.out.println("AIINPUT " + play);
                break;
            }
        }

        Tester.roundCounts.add(roundnum);

        int moveID = player.params.gameParams.moveConverter.convertMoveToID(play);

        int bestMoveID = player.root.bestWinRate(new ArrayList<>(), false);
        float confidence = player.root.getWinRateOfMove(bestMoveID);

        if(player.params.useOpeningBook && player.root.grandDepth == 0 && player.root.currentPlayer == 1) moveID = 40;

        return new RealMove(moveID, confidence < player.params.resignThreshold, confidence < player.params.offerDrawThreshold);
    }

    @Override
    public void processPlay(int player, int moveID) {
        this.player.playMove(player, moveID);
    }

    public void resetAI(){
        resetAI(aiPlayerNum);
    }
    public void resetAI(int newPlayerNum){
        MonteCarloParameters reuseParams = player.params;
        aiPlayerNum = newPlayerNum;
        player = new AIPlayer((IGame) baseGame.clone(), reuseParams.gameParams.moveConverter.getNumberOfMoves(), aiPlayerNum, reuseParams);
        player.simulator = new MonteCarloSimulator();
    }

    @Override
    public void end() {
        player.end();
    }
}
