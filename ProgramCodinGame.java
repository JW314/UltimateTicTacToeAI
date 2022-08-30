import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class ProgramCodinGame {
    public static void main(String[] args){
        TixTaxInfo.initialize();        Tester.roundCounts = new ArrayList<>();
        TixTaxInfo.breakTiesWithCount = true;
        DebuggerTixTaxBoardEditor debug = new DebuggerTixTaxBoardEditor();

        TixTaxBoard game = new TixTaxBoard(3, 3);

        AIReporter report = new AIReporter(ConfigReporter.onlyCDG());

        MonteCarloParameters p1 = ConfigParams.defaultStart();
        p1.simulationsPerRound = 1;
        p1.explorationParameter = 0.8f;
        p1.tiePoints = 0.5f;

        int turns = 1000;
        p1.rave.useRave = true;
        p1 = ConfigParams.setRave(p1, turns, 0.125f, 0.4f);
        //p1.rave.raveEquParameter = Math.round(0.15f * turns);
        //p1.rave.killRaveGameCount = Math.round(0.15f * turns * 2.5f);
        //p1.rave.raveAmplify = 0.1f;

        p1.useTranspositionTable = false;

        ConfigParams.setBaseGame(p1, game, new Random());

        p1.recalcUCBCooldown = 10;
        p1.recalcBonusCooldown = 10;
        p1 = ConfigParams.setUCBBonus(p1, 0.01f, turns, 0.125f, 0.025f);
        p1 = ConfigParams.setDomainPriors(p1, 4, 0);

        CodinGameInput codingame = new CodinGameInput(new TixTaxMoveConvert());
        String firstmove = codingame.readCycle();
        int aiNum;
        if(firstmove.equals("-1 -1")){
            aiNum = 1;
        }else{
            aiNum = 2;
            codingame.addInput(firstmove);
        }

        AIInput intellibae = new AIInput(game, p1, aiNum, new AITimedSupervisor(50, report));
        intellibae.resetAI(aiNum);
        intellibae.refreshParams(p1);
        IGameInput player1;
        IGameInput player2;

        if(aiNum == 1){
            player1 = intellibae;
            player2 = codingame;
        }else{
            player1 = codingame;
            player2 = intellibae;
        }

        GameManager gm = new GameManager(game, player1, player2);
        gm.setAllSettingsFalse();
        gm.playGame();

        System.out.println(gm.printGameRecord((aiNum == 1) ? "INTELLIBAE" : "CHALLENGER", (aiNum == 2) ? "INTELLIBAE" : "CHALLENGER",
                new TixTaxMoveConvert()));
    }

    /**
    public static void gameManagerTest(){
        TixTaxBoard game = new TixTaxBoard(3, 3);
        ScannerInput scan = new ScannerInput();
        RandomInput rand = new RandomInput();

        GameManager gm = new GameManager(game, scan, rand);

        for (int i = 0; i < 500; i++) {
            gm.playGame();
        }
    }**/
}
