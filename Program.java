import java.io.ObjectInputFilter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class Program {
    public static void main(String[] args){
        TixTaxInfo.initialize();        Tester.roundCounts = new ArrayList<>();
        TixTaxInfo.breakTiesWithCount = false;
        DebuggerTixTaxBoardEditor debug = new DebuggerTixTaxBoardEditor();

        Scanner scan = new Scanner(System.in);
        System.out.println("WHICH PLAYER IS THE AI");
        int aiNum = scan.nextInt();
        TixTaxBoard game = new TixTaxBoard(3, 3);
        //game = debug.readNotation("2O6/9/3X5/9/8X/9/X8/9/6O2 O 4");

        ScannerInput human = new ScannerInput(new TixTaxMoveConvert());
        AIReporter report = new AIReporter(ConfigReporter.program());

        MonteCarloParameters p1 = ConfigParams.defaultStart();
        p1.resignThreshold = -1;
        p1.simulationsPerRound = 3;
        p1.explorationParameter = 0.8f;
        p1.tiePoints = 0.05f;

        int turns = 28900;
        ConfigParams.setRave(p1, turns, 0.15f, 0.15f*0.25f);
        //p1.rave.raveEquParameter = Math.round(0.15f * turns);
        //p1.rave.killRaveGameCount = Math.round(0.15f * turns * 2.5f);
        //p1.rave.raveAmplify = 0.1f;

        p1.useTranspositionTable = false;

        p1.useUCBBonuses = true;
        p1.recalcUCBCooldown = 10;
        p1.recalcBonusCooldown = 10;


        ConfigParams.setUCBBonus(p1, 0.03f, turns, 0.05f, 0.01f);

        ConfigParams.setDomainPriors(p1, 6, 0);


        //Configurer.setBestMove120000rds(p1);

        ConfigParams.setBaseGame(p1, game, new Random());
        AIInput computer = new AIInput(game, p1, aiNum, new AIHumanSupervisor(turns, report));
        computer.resetAI(aiNum);


        computer.refreshParams(p1);

        IGameInput player1;
        IGameInput player2;

        if(aiNum == 1){
            player1 = computer;
            player2 = human;
        }else{
            player1 = human;
            player2 = computer;
        }


        GameManager gm = new GameManager(game, player1, player2);
        gm.resetGame = false;
        gm.playGame();

        System.out.println(gm.printGameRecord((aiNum == 1) ? "AI" : "HU", (aiNum == 2) ? "AI" : "HU",
                new TixTaxMoveConvert()));
    }
    public static void gameManagerTest(){
        TixTaxBoard game = new TixTaxBoard(3, 3);
        ScannerInput scan = new ScannerInput();
        RandomInput rand = new RandomInput();

        GameManager gm = new GameManager(game, scan, rand);

        for (int i = 0; i < 500; i++) {
            gm.playGame();
        }
    }
}
