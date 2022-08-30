import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebuggerAIvsAI {
    public static void main(String[] args){
        Initializer.initialize();

        TixTaxInfo.breakTiesWithCount = true;

        double sumthing = 0;
        for (int i = 0; i < 20000; i++) {
            long thistime = Tester.getTesterTime();
            sumthing += thistime;
        }
        System.out.println(sumthing);

        long start = System.currentTimeMillis();

        List<Integer> games = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            Random rand = new Random(69);
            rand = new Random(420);

            TixTaxBoard game = new TixTaxBoard(3, 3);

            DebuggerTixTaxBoardEditor debug = new DebuggerTixTaxBoardEditor();
            //game = debug.suddenDeath();
            //game = debug.singleBox();
            //game = debug.readNotation("6XXX/XX2X1OO1/O1O2XO2/2X3OX1/X3O3O/1XX1X2OO/XOXOXOOXO/6OOO/1OO1O1X1X X A");
            //game = debug.readNotation("OXX2X1OX/1OOOOX1XO/XOOOOXXX1/1XXXXO1X1/OXO3X1X/OX1O1OOX1/3OXXOOO/O1XO1XO2" +
            //"/XXO3X1O X 5");
            //game = debug.readNotation("1X1O3OX/X3O4/3X2XO1/X2O2XOX/2XOO3X/9/4X4/2OXX4/OOO6 O 7");

            //THE SACRED POSITION:
            //right after AI's move 22, securing a lead against the AI's first non-creator human opponent (Ananth)
            //X1OX4O/1O1X1O1X1/6XXX/1OO2X3/1X5X1/O2O1XO1X/XXO6/1O2OO2X/5XOOO X 2

            MonteCarloParameters p1 = ConfigParams.defaultStart();
            MonteCarloParameters p2 = ConfigParams.defaultStart();




            ConfigParams.setBaseGame(p1, game, rand);
            ConfigParams.setBaseUseTable(p1, game, false);
            ConfigParams.setBaseGame(p2, game, rand);
            ConfigParams.setBaseUseTable(p2, game, false);


            p1.useTranspositionTable = false;
            p2.useTranspositionTable = false;

            p1.explorationParameter = 0.8f;
            p2.explorationParameter = 0.8f;
            p1.simulationsPerRound = 2;
            p2.simulationsPerRound = 2;

            //p1.recalcUCBCooldown = 1;
            //p2.recalcUCBCooldown = 1;

            int turns = 40000;

            p1 = ConfigParams.setRave(p1, turns, 0.01f, 0.03f);
            p1.rave.useRave = false;
            p2 = ConfigParams.setRave(p2, turns, 0.01f, 0.03f);
            p2.rave.useRave = false;

            p1.resignThreshold = -1;
            p2.resignThreshold = -1;

            p1.useUCBBonuses = true;
            p2.useUCBBonuses = true;
            p1.recalcBonusCooldown = 10;
            p2.recalcBonusCooldown = 10;

            p1.recalcUCBCooldown = 10;
            p2.recalcUCBCooldown = 10;

            p1 = ConfigParams.setUCBBonus(p1, 0.03f, turns, 0.1f, 0.02f);
            p2 = ConfigParams.setUCBBonus(p2, 0.03f, turns, 0.1f, 0.04f);


            p1.domain.usePriors = false;
            p2 = ConfigParams.setDomainPriors(p2, 6, 0);

            //ScannerInput hooman = new ScannerInput();
            AIReporter report = new AIReporter(ConfigReporter.allInfo());
            AIInput comp1 = new AIInput(game, p1, 1, new AIAutoSupervisor(turns, report));
            AIInput comp2 = new AIInput(game, p2, 2, new AIAutoSupervisor(turns, report));

            //System.out.println("comp1 " + comp1.player.params.domain);
            //System.out.println("comp2 " + comp2.player.params.domain);

            comp1.resetAI(1);
            comp2.resetAI(2);

            GameManager gm = new GameManager((IGame) game.clone(), comp1, comp2);
            gm.resetGame = false;
            //gm.printAllStates = false;
            //gm.printInitialState = false;
            Tester.checkPoint("Debugger start game");
            int result = gm.playGame();


            games.add(result);
            System.out.println("CURRENT RECORD : " + games);

            System.out.println(gm.printGameRecord("Debug1", "Debug2", new TixTaxMoveConvert()));
            System.out.println(gm.game.buildNotation());

        }

        String[] testerReport = Tester.getTesterInfo();
        for(String s : testerReport) System.out.println(s);

        System.out.println(Tester.infoInit + " INIT TIME " + (Tester.infoInit/Tester.msnsConversion) + "ms");

        /**
         for(int i = 0; i < 9; i++){
         System.out.println("on box " + i + " : " + TixTaxInfo.getCurBoxHash(i));
         }
         System.out.println("on box A : " + TixTaxInfo.getCurBoxHash(-1));
         System.out.println("change pl " + TixTaxInfo.switchPlayerHash);
         **/
    }

}