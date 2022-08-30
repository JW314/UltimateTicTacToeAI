import java.util.*;

public class Tester {
    public static boolean debug = false;
    public static boolean debugTimesOn = true;
    public static boolean testerOn = true;

    public static long checkpointTime;

    public static long infoInit = 0;
    public static long expansion = 0;
    public static long selection = 0;
    public static long simulation = 0;
    public static long backpropagation = 0;
    public static long total = 0;
    public static long expClone = 0;
    public static long setupSelect = 0;
    public static long updateValue = 0;
    public static long recalcValue = 0;
    public static long pollValue = 0;
    public static long selectionGame = 0;
    public static long gameWinCheck = 0;

    public static long gamePlayAITracker = 0;
    public static long gameMoveSupAITracker = 0;

    public static long cloneBoardPlayer = 0;
    public static long chooseMovePlayer = 0;
    public static long chooseMove = 0;
    public static long insideTimeLoop = 0;
    public static long rootSelection = 0;

    public static long playMoveParse = 0;
    public static long cloneBoard = 0;
    public static long cloneOther = 0;
    public static long math = 0;
    public static long hash = 0;
    public static long hashStateCopy = 0;

    public static long playNum = 0;

    public static long roundCount = 0;

    public static long boardCloneCount = 0;


    public static long msnsConversion = (10L*10L*10L*10L*10L*10L);

    public static List<Integer> roundCounts = new ArrayList<>();
    public static List<Integer> roundTimes = new ArrayList<>();

    public static void resetTesterInfo(){
        infoInit = 0;
        expansion = 0;
        selection = 0;
        simulation = 0;
        backpropagation = 0;
        total = 0;
        expClone = 0;
        setupSelect = 0;
        updateValue = 0;
        pollValue = 0;
        selectionGame = 0;

        gamePlayAITracker = 0;
        gameMoveSupAITracker = 0;

        playMoveParse = 0;
        cloneBoard = 0;
        cloneOther = 0;
        math = 0;
        hash = 0;
        hashStateCopy = 0;

        playNum = 0;

        roundCount = 0;

        boardCloneCount = 0;

    }

    public static String[] getTesterInfo(){
        List<String> testInfo = new ArrayList<>();
        testInfo.add(Tester.expansion + " Time in ns spent in Expansion Function : " + String.valueOf(Tester.expansion).length());
        testInfo.add(Tester.selection + " Time in ns spent in Selection Function : " + String.valueOf(Tester.selection).length());
        testInfo.add(Tester.simulation + " Time in ns spent in Simulation Function : " + String.valueOf(Tester.simulation).length());
        testInfo.add(Tester.backpropagation + " Time in ns spent in Backpropagation Function : " + String.valueOf(Tester.backpropagation).length());
        //testInfo.add(Tester.expClone + " Time in ns spent cloning board for Expansion Prep : " + String.valueOf(Tester.expClone).length());
        testInfo.add(Tester.setupSelect + " Time in ns -Selection- setting up nodes : " + String.valueOf(Tester.setupSelect).length());
        testInfo.add(Tester.updateValue + " Time in ns -Selection- updating UCB evaluations : " + String.valueOf(Tester.updateValue).length());
        testInfo.add(Tester.recalcValue + " Time in ns -Selection- recalcing UCB evaluations : " + String.valueOf(Tester.recalcValue).length());
        testInfo.add(Tester.pollValue + " Time in ns -Selection- selecting/polling UCB moves : " + String.valueOf(Tester.pollValue).length());
        testInfo.add(Tester.selectionGame + " Time in ns -Selection- playing game for node : " + String.valueOf(Tester.selectionGame).length());

        testInfo.add(Tester.gameWinCheck + " Time in ns in TixTaxBoard updating VictoryTracker : " + String.valueOf(Tester.selectionGame).length());


        //testInfo.add(Tester.playMoveParse + " Time in ns -Board- parsing String : " + String.valueOf(Tester.playMoveParse).length());
        testInfo.add(Tester.gamePlayAITracker + " TIme in ns -Board- playing from AI (Selection) : " + String.valueOf(Tester.gamePlayAITracker).length());
        testInfo.add(Tester.gameMoveSupAITracker + " TIme in ns -Board- playing from AI (Selection) : " + String.valueOf(Tester.gameMoveSupAITracker).length());

        testInfo.add(Tester.total * msnsConversion + " Total time in ns spent by both players : " + String.valueOf(Tester.total * msnsConversion).length());

        testInfo.add(Tester.cloneBoardPlayer + " Time in ns spent cloning board in AIPlayer (starting each rollout) : " + String.valueOf(Tester.cloneBoardPlayer).length());

        testInfo.add(Tester.chooseMovePlayer + " Time in ns spent performing a move in AIPlayer : " + String.valueOf(Tester.chooseMovePlayer).length());
        testInfo.add(Tester.chooseMove + " Time in ns spent inside choose move : " + String.valueOf(Tester.chooseMove).length());
        testInfo.add(Tester.rootSelection + " Time in ns spent COMPLETE selection : " + String.valueOf(Tester.rootSelection).length());
        testInfo.add(Tester.insideTimeLoop + " Time in ns spent inside RunningCycle: " + String.valueOf(Tester.insideTimeLoop).length());



        testInfo.add(Tester.cloneBoard + " : " + String.valueOf(Tester.cloneBoard).length());
        testInfo.add(Tester.cloneOther + " : " + String.valueOf(Tester.cloneOther).length());
        //testInfo.add(Tester.boardCloneCount + " # of times board is cloned : " + String.valueOf(Tester
        // .boardCloneCount).length());
        //testInfo.add(Tester.hash + " hash : " + String.valueOf(Tester.hash).length());
        //testInfo.add(Tester.hashStateCopy + " hash state : " + String.valueOf(Tester.hashStateCopy).length());

        //testInfo.add(Tester.math + " : " + String.valueOf(Tester.math).length());

        Collections.sort(Tester.roundCounts);
        //testInfo.add(Tester.roundCounts + " : " + String.valueOf(Tester.roundCounts).length());

        //System.out.println((System.currentTimeMillis()-start) + " absolute time in ns");
        //System.out.println(System.currentTimeMillis());

        //average gets pretty skewed by rounds that happen late game
        //System.out.println("rounds per 100ms " + (double)(Tester.roundCount*100) / ((double)Tester.total));

        String[] report = new String[testInfo.size()];
        for (int i = 0; i < testInfo.size(); i++) {
            report[i] = testInfo.get(i);
        }
        return report;
    }

    public static long getTesterTime(){
        if(!testerOn) return 0;
        return System.nanoTime();
    }

    public static void checkPoint(String msg){
        if(!testerOn) return;
        if(!debug) return;
        long time = System.nanoTime();
        long diff = time - checkpointTime;
        System.out.println("TEST CP " + msg);
        System.out.println(System.nanoTime() + " ; " + diff);
        checkpointTime = time;

    }
}
