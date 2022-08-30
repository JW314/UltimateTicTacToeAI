import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class DebuggerTwoPlayer {
    public static void main(String[] args){
        TixTaxInfo.initialize();        Tester.roundCounts = new ArrayList<>();

        TixTaxBoard game = new TixTaxBoard(3, 3);
        ScannerInput human1 = new ScannerInput(new TixTaxMoveConvert());
        ScannerInput human2 = new ScannerInput(new TixTaxMoveConvert());

        IGameInput player1;
        IGameInput player2;

        GameManager gm = new GameManager(game, human1, human2);
        gm.playGame();
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
