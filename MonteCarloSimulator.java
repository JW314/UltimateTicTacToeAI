/**
 * Runs simulations used by the Monte Carlo AI (tree node)
 * Receives GameTracker with current game and simulation input, plays game out a certain number of times and outputs total points earned by certain player
 */

public class MonteCarloSimulator {

    public float simulateRounds(int playerid, int gameCount, AIGameTracker game, float tiePoints, RaveTracker rave){
        float points = 0;

        //if(true) return points;

        IGame board = game.board;

        //4647938848535373243
        //System.out.println("MC SIMULATOR START");

        for (int i = 0; i < gameCount; i++) {
            IGame currGame = board;
            if(i != gameCount-1) currGame = (IGame)board.clone();
            IAISimulationInput input = game.getSimulationInput();
            input.setBoard(currGame);

            GameManager gm = new GameManager(currGame, input);
            TixTaxSimulationAnalyzer analyzer = new TixTaxSimulationAnalyzer();
            gm.addSimAnalyzer(analyzer);

            gm.printInvalidMsg = true;
            gm.printPlayer = false;
            gm.printAllStates = false;
            gm.printInitialState = false;
            gm.printFinalState = false;
            gm.printResult = false;
            gm.resetGame = false;

            int result = gm.playQuickGame();
            if(result == -1) points += tiePoints;
            else if(result == playerid) points += 1;

            if(rave != null) analyzer.updateRave(rave);
        }

        return points;
    }

}
