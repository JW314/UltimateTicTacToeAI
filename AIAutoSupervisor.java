/**
 * NOT CDG
 */

public class AIAutoSupervisor implements IAISupervisor{

    AIReporter reporter;

    int turnLength;
    int rounds = 0;
    int turnNum = 0;

    int maxTurnsAlloted = 1;
    int estimatedTurns = 1;
    int commitNum = 0;

    String currentPlay = "";
    int inARow = 0;


    public AIAutoSupervisor(int turn){
        turnLength = turn;
        reporter = new AIReporter(ConfigReporter.noInfo());
    }
    public AIAutoSupervisor(int turn, AIReporter r){
        turnLength = turn;
        reporter = r;
    }

    public void reportNode(AIPlayer pl){
        createReport(pl);
    }
    private void createReport(AIPlayer player){
        String nextPlay = reporter.printClassicReport(player, rounds, false);
        reporter.printAIConsidered(player, false);
        reporter.printBestAIRecs(player, false);
        reporter.printSearchDepth(player, false);
        reporter.printVariations(player, player.params.gameParams.moveConverter, false);

        addReport(nextPlay);
    }

    public void addReport(String play){
        turnNum++;
        if(currentPlay.equals(play)) inARow++;
        else{
            currentPlay = play;
            inARow = 1;
        }
    }
    public String command(AIPlayer player){
        String c = "a";
        if(turnNum >= maxTurnsAlloted) c = "go "+currentPlay;
        if(inARow >= commitNum && commitNum != 0) c = "go "+currentPlay;
        if(!c.equals("a")){
            reporter.printCDG(currentPlay, player.params.gameParams.moveConverter, player);

            reporter.printClassicReport(player, rounds, true);
            reporter.printAIConsidered(player, true);
            reporter.printBestAIRecs(player, true);
            reporter.printSearchDepth(player, true);
            reporter.printVariations(player, player.params.gameParams.moveConverter, true);

            reporter.printExecutedPlay(currentPlay, player.params.gameParams.moveConverter);

            /**
             reporter.printFinalReport = true;
             if(player.root.grandDepth == 40) {
             reporter.printClassicReport(player, rounds, true);
             }
             reporter.printFinalReport = false;
             **/

            reset(player);
        }
        return c;
    }

    private void reset(AIPlayer player){
        currentPlay = "";
        inARow = 0;
        rounds = 0;
        turnNum = 0;
        player.resetProgress();
    }

    @Override
    public void runningCycle(AIPlayer player) {
        rounds += turnLength;
        reporter.printCalculatingMessage(turnNum, player);
        turnNum++;

        int estTurns = getEstimatedTurns();
        if(estTurns <= 0){
            player.chooseMove(turnLength);
        }else{
            player.chooseMove(turnLength, turnLength*estTurns);
        }

        reportNode(player);
    }

    @Override
    public int getTurnLength() {
        return turnLength;
    }

    public int getEstimatedTurns() {
        return estimatedTurns;
    }

    @Override
    public int getRoundCount() {
        return rounds;
    }
}
