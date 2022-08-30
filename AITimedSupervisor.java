import java.time.Duration;
import java.time.Instant;

public class AITimedSupervisor implements IAISupervisor{

    AIReporter reporter;

    int msAllotedPerTurn = 0;
    int rounds = 0;
    int turnNum = 0;

    int partTurnLength = 1;

    int turnsAlloted = 1;
    int estimatedTurns = 1;

    String lastSuggestedPlay = "";



    public AITimedSupervisor(int time){
        msAllotedPerTurn = time;
        reporter = new AIReporter(ConfigReporter.noInfo());
    }
    public AITimedSupervisor(int time, AIReporter r){
        msAllotedPerTurn = time;
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
        lastSuggestedPlay = play;
    }

    public String command(AIPlayer player){
        String c = "a";
        if(turnNum >= turnsAlloted) c = "go "+lastSuggestedPlay;
        if(!c.equals("a")){
            reporter.printCDG(lastSuggestedPlay, player.params.gameParams.moveConverter, player);
            reporter.printClassicReport(player, rounds, true);
            reporter.printAIConsidered(player, true);
            reporter.printBestAIRecs(player, true);
            reporter.printSearchDepth(player, true);
            reporter.printVariations(player, player.params.gameParams.moveConverter, true);

            reporter.printExecutedPlay(lastSuggestedPlay, player.params.gameParams.moveConverter);


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
        lastSuggestedPlay = "";
        rounds = 0;
        turnNum = 0;
        player.resetProgress();
    }

    @Override
    public void runningCycle(AIPlayer player) {
        double sumthing = 0;
        for (int i = 0; i < 20000; i++) {
            long thistime = Tester.getTesterTime();
            sumthing += thistime;
        }
        //System.out.println(sumthing);

        reporter.printCalculatingMessage(turnNum, player);
        turnNum++;

        long totalRounds = 0;
        String[] report1 = new String[1];
        String[] report2 = Tester.getTesterInfo();
        long timeSinceStart = 0;

        long loopstart = Tester.getTesterTime();

        double nsAllotedPerTurn = msAllotedPerTurn * 1E6;
        while(timeSinceStart < nsAllotedPerTurn){
            long time = Tester.getTesterTime();
            //System.out.println("AITS choose move begin " + time);

            //System.out.println(reporter.timeSinceStartCycle());
            //System.out.println("time " + System.currentTimeMillis(););

            int estTurns = getEstimatedTurns();

            if(estTurns <= 0){
                player.chooseMove(partTurnLength);
            }else{
                int estTurnRounds = (int)((float)totalRounds * msAllotedPerTurn / timeSinceStart);
                player.chooseMove(partTurnLength, estTurnRounds*estTurns);
            }


            rounds += partTurnLength;
            totalRounds += partTurnLength;

            if(Tester.debug){
                report1 = report2;
                report2 = Tester.getTesterInfo();
            }
            long time2 = Tester.getTesterTime();
            //System.out.println("AITS choose move end   " + time2);
            //System.out.println("AITS DIFF " + (time2 - time));
            Tester.chooseMove += time2 - time;

            timeSinceStart = reporter.timeSinceStartCycle();

            //long time = reporter.timeSinceStartCycle();
            //if(time > 90) System.out.println("AITIMEDSUPER: rounds " + totalRounds + ", time " + time );

            //System.out.println("timed round finish");

        }
        long loopend = Tester.getTesterTime();
        Tester.insideTimeLoop += loopend-loopstart;
        //System.out.println("AITIMED TIME OF STUFF: " + (loopend-loopstart) + " " + Tester.insideTimeLoop);
        //report2 = Tester.getTesterInfo();

        if(Tester.debug){
            System.out.println("AITIMEDSUPER BEFORE TIMEOUT");
            for(String s : report1) System.out.println("|| " + s);
            System.out.println("AITIMEDSUPER AFTER  TIMEOUT");
            for(String s : report2) System.out.println("|| " + s);
        }

        reportNode(player);
    }

    @Override
    public int getTurnLength() {
        return partTurnLength;
    }

    public int getEstimatedTurns() {
        return estimatedTurns;
    }

    @Override
    public int getRoundCount() {
        return rounds;
    }
}
