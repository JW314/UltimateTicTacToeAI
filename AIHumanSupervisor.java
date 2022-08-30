/**
 * NOT CDG
 */

import java.util.Scanner;

public class AIHumanSupervisor implements IAISupervisor{

    AIReporter reporter;

    int turnLength;
    Scanner scan;

    int rounds = 0;
    int turnNum = 0;
    String lastSuggestedMove = "";

    int estimatedTurns;

    public AIHumanSupervisor(int turn){
        turnLength = turn;
        scan = new Scanner(System.in);
        reporter = new AIReporter(ConfigReporter.noInfo());
        estimatedTurns = 3;
    }
    public AIHumanSupervisor(int turn, AIReporter r){
        turnLength = turn;
        scan = new Scanner(System.in);
        reporter = r;
        estimatedTurns = 3;
    }

    public void reportNode(AIPlayer pl){
        createReport(pl);
    }
    public String command(AIPlayer player){
        String input = scan.nextLine();
        if(input.length() >= 8 && input.substring(0, 9).equals("override ")){
            lastSuggestedMove = player.params.gameParams.moveConverter.convertUserToRaw(input.substring(9));
            input = "go";
        }
        if(input.equals("go")){
            rounds = 0;
            turnNum = 0;
            player.resetProgress();

            reporter.printCDG(lastSuggestedMove, player.params.gameParams.moveConverter, player);
            reporter.printClassicReport(player, rounds, true);
            reporter.printAIConsidered(player, true);
            reporter.printBestAIRecs(player, true);
            reporter.printSearchDepth(player, true);
            reporter.printVariations(player, player.params.gameParams.moveConverter, true);

            reporter.printExecutedPlay(lastSuggestedMove, player.params.gameParams.moveConverter);
            return "go "+lastSuggestedMove;
        }
        return "a";
    }

    private void createReport(AIPlayer player){
        lastSuggestedMove = reporter.printClassicReport(player, rounds, false);
        reporter.printAIConsidered(player, false);
        reporter.printBestAIRecs(player, false);
        reporter.printSearchDepth(player, false);
        reporter.printVariations(player, player.params.gameParams.moveConverter, false);

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
