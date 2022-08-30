import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebuggerAIvsAIHumanS {
    public static void main(String[] args){
        List<Integer> games = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            TixTaxBoard game = new TixTaxBoard(3, 3);

            DebuggerTixTaxBoardEditor debug = new DebuggerTixTaxBoardEditor();
            //game = debug.suddenDeath();
            //game = debug.singleBox();

            MonteCarloParameters p1 = new MonteCarloParameters();
            MonteCarloParameters p2 = new MonteCarloParameters();
            p1.tiePoints = 0.5f;
            p2.tiePoints = 0.5f;
            p1.gameParams.moveConverter = new TixTaxMoveConvert();
            p2.gameParams.moveConverter = new TixTaxMoveConvert();
            p1.explorationParameter = 0.5f;
            p2.explorationParameter = 0.5f;

            p1.confCalculatorKey = 0;
            p1.maxB = 1.0f;
            p1.confReqSims = 10;
            p2.confCalculatorKey = 0;
            /**
             *             p1.confCalculatorKey = 1;
             *             p1.maxB = 0.9f;
             *             p1.confReqSims = 50;
             *             p2.confCalculatorKey = 0;
             */
            //SEED 12, 15
            Random rand = new Random(15);

            AIGameTracker base1 = new AIGameTracker(game);
            base1.setMoveSupplier(new TixTaxMoveSupplier());
            base1.setSimulationInput(new AISimulationInput2(rand, new TixTaxMoveConvert()));
            base1.resetInputTrackerCall();
            base1.setHeuristics();

            AIGameTracker base2 = new AIGameTracker(game);
            base2.setMoveSupplier(new TixTaxMoveSupplier());
            base2.setSimulationInput(new AISimulationInput2(rand, new TixTaxMoveConvert()));
            base2.resetInputTrackerCall();
            base2.setHeuristics();


            p1.setBaseGame(base1);
            p2.setBaseGame(base2);

            //ScannerInput hooman = new ScannerInput();
            AIReporter report = new AIReporter(ConfigReporter.allInfo());
            AIInput comp1 = new AIInput(game, p1, 1, new AIHumanSupervisor(1200, report));
            AIInput comp2 = new AIInput(game, p2, 2, new AIHumanSupervisor(1200, report));

            comp1.resetAI();
            comp2.resetAI();

            GameManager gm = new GameManager(game, comp1, comp2);
            gm.resetGame = false;
            //gm.printAllStates = false;
            //gm.printInitialState = false;
            int result = gm.playGame();


            games.add(result);
            System.out.println("CURRENT RECORD : " + games);

        }
        System.out.println(Tester.expansion + " Time in ns spent in Expansion Function ");
        System.out.println(Tester.expClone + " Time in ns spent cloning board for Expansion Prep " );
        System.out.println(Tester.total *(10L*10L*10L*10L*10L*10L) + " Total time in ns spent by both players");

        System.out.println(Tester.cloneBoard);
        System.out.println(Tester.cloneOther);

        System.out.println(Tester.math);
    }

}