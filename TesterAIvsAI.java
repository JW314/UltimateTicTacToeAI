import java.util.*;

public class TesterAIvsAI {

    public static void main(String[] args){
        Initializer.initialize();
        IGame game = setGame();

        TixTaxInfo.breakTiesWithCount = true;

        List<AIInput> subjects = setSubjectsPriorsTest(game);
        List<SubjectRecord> records = new ArrayList<>();
        for (int i = 0; i < subjects.size(); i++) {
            records.add(new SubjectRecord(i+1));
        }

        Map<String, Matchup> table = new HashMap<>();

        List<String> matchStrings = new ArrayList<>();
        for (int sub1 = 1; sub1 <= subjects.size(); sub1++) {
            for (int sub2 = sub1+1; sub2 <= subjects.size(); sub2++) {
                String s = sub1 + " " + sub2;
                matchStrings.add(s);
                table.put(s, new Matchup(sub1, sub2));
            }
        }

        for (int loopNum = 0; loopNum < 1000; loopNum++) {
            for (int first = 1; first <= subjects.size(); first++) {
                for (int second = 1; second <= subjects.size(); second++) {
                    if(first == second) continue;
                    AIInput comp1 = subjects.get(first-1);
                    AIInput comp2 = subjects.get(second-1);
                    comp1.resetAI(1);
                    comp2.resetAI(2);

                    GameManager gm = new GameManager((IGame) game.clone(), comp1, comp2);
                    gm.resetGame  = false;
                    gm.printPlayer = false;
                    gm.printInvalidMsg = false;
                    gm.printInitialState = false;
                    gm.printAllStates = false;
                    gm.printFinalState = false;
                    gm.printResult = false;
                    int result = gm.playGame();

                    String matchupKey = first+" "+second;
                    if(first > second) matchupKey = second+" "+first;
                    int winner = -1;
                    if(result == 1) winner = first;
                    if(result == 2) winner = second;
                    records.get(first-1).addGame(winner);
                    records.get(second-1).addGame(winner);
                    boolean isSmallerSubjectFirst = first < second;
                    table.get(matchupKey).addGame(isSmallerSubjectFirst, winner);

                    String subject1Name = "S" + (first);
                    String subject2Name = "S" + (second);
                    System.out.println(gm.printGameRecord(subject1Name, subject2Name, new TixTaxMoveConvert()));

                    comp1.end();
                    comp2.end();

                }
            }

            System.out.println( (loopNum+1) + " LOOPS COMPLETE");
            for(String matchup : matchStrings){
                String rep = table.get(matchup).report();
                System.out.println(rep);
            }
            String scores = "TOTALS: ";
            for(SubjectRecord rec : records){
                scores += rec.report() + " ; ";
            }
            System.out.println(scores);
        }

        System.out.println("XTRA");

        System.out.println(Tester.expansion + " Time in ns spent in Expansion Function ");
        System.out.println(Tester.selection + " Time in ns spent in Selection Function ");
        System.out.println(Tester.simulation + " Time in ns spent in Simulation Function ");
        System.out.println(Tester.backpropagation + " Time in ns spent in Backpropagation Function ");
        System.out.println(Tester.setupSelect + " Time in ns setting up nodes in Selection" );
        System.out.println(Tester.updateValue + " Time in ns updating UCB evaluations" );
        System.out.println(Tester.pollValue + " Time in ns selecting UCB moves" );
        System.out.println(Tester.total *(10L*10L*10L*10L*10L*10L) + " Total time in ns spent by both players");

    }

    static class SubjectRecord{
        int subjectID;
        float points;
        int gamesPlayed;
        public SubjectRecord(int id){
            subjectID = id; points = 0; gamesPlayed = 0;
        }
        public void addGame(int winnerID){
            gamesPlayed += 1;
            if(winnerID == subjectID) points += 1;
            if(winnerID == -1) points += 0.5;
        }
        public int getScore(){
            return (int)(1000f * points / (float)gamesPlayed);
        }
        public String report(){
            return "S" + subjectID + ": " + getScore();
        }
    }
    static class Matchup{

        static class Side{
            int subAID;
            int subAWins;
            int ties;
            int subBWins;
            public Side(int ID){
                subAID = ID;
                subAWins = 0; ties = 0; subBWins = 0;
            }
            public void addResult(int winnerID){
                if(winnerID == subAID) subAWins++;
                else if(winnerID == -1) ties++;
                else subBWins++;
            }
            public int totalCount() { return subAWins + ties + subBWins; }

            public int getSubAWins() {
                return subAWins;
            }
            public int getSubBWins() {
                return subBWins;
            }
            public int getTies() {
                return ties;
            }

            public String report(int aID, int bID){
                String s = "";
                s += "S" + aID + ":" + (int)(1000f * (float)getSubAWins() / (float)totalCount()) + " ";
                s += "t:" + (int)(1000f * (float)getTies() / (float)totalCount()) + " ";
                s += "S" + bID + ":" + (int)(1000f * (float)getSubBWins() / (float)totalCount());
                return s;
            }
        }

        int subjectAID;
        int subjectBID;
        Side aFirst;
        Side bFirst;

        public Matchup(int subA, int subB){
            subjectAID = subA;
            subjectBID = subB;
            aFirst = new Side(subA);
            bFirst = new Side(subA);
        }

        public void addGame(boolean isAFirst, int idWinner){
            if(isAFirst) aFirst.addResult(idWinner);
            else bFirst.addResult(idWinner);
        }
        public String report(){
            int totalGames = aFirst.totalCount() + bFirst.totalCount();
            float totalA = aFirst.getSubAWins() + bFirst.getSubAWins() + 0.5f * (float)(aFirst.getTies()+bFirst.getTies());
            int scoreA = (int) (1000 * (totalA / (float)totalGames));
            int scoreB = (int) (1000 * (1 - totalA/(float)totalGames));
            String report = "REPORT !!! S" + subjectAID + " " + scoreA + " - " + scoreB + " S" + subjectBID +"  ;  ";
            report += "S" + subjectAID + " first - " + aFirst.report(subjectAID, subjectBID) + "  ;  ";
            report += "S" + subjectBID + " first - " + bFirst.report(subjectAID, subjectBID) + "  ;  ";
            return report;
        }

    }


    public static IGame setGame(){
        TixTaxBoard game = new TixTaxBoard(3, 3);
        //DebuggerTixTaxBoardEditor debug = new DebuggerTixTaxBoardEditor();
        //game = debug.singleBox();
        return game;
    }

    public static List<AIInput> setSubjectsRAVETest(IGame game) {
        List<AIInput> subjects = new ArrayList<>();

        int turns = 10000;

        int subjectCount = 6;
        Random rand = new Random(17);
        //ScannerInput hooman = new ScannerInput();
        AIReporter report = new AIReporter(ConfigReporter.noInfo());

        for(int i = 1; i <= subjectCount; i++){
            MonteCarloParameters params = ConfigParams.defaultStart();
            params.tiePoints = 0.5f;
            params.gameParams.moveConverter = new TixTaxMoveConvert();
            params.explorationParameter = 0.8f;

            ConfigParams.setBaseGame(params, game, rand);


            //FIX THIS AND ADD OTHER VARiABLES LIKE KILL VARIABLE
            //ADD RAVE INITIALIZE FUNCTION INTO CONFIGURER

            if(i == 1) params.rave.useRave = false;
            if(i == 2){
                ConfigParams.setRave(params, turns, 0.08f, 0.08f * 2.5f);
            }
            if(i == 3){
                ConfigParams.setRave(params, turns, 0.15f, 0.15f * 2.5f);
            }
            if(i == 4){
                ConfigParams.setRave(params, turns, 0.2f, 0.2f * 2.5f);
            }
            if(i == 5){
                ConfigParams.setRave(params, turns, 0.3f, 0.3f * 2.5f);
            }
            if(i == 6){
                ConfigParams.setRave(params, turns, 0.4f, 0.4f * 2.5f);
            }

            params.useTranspositionTable = false;

            AIInput comp = new AIInput(game, params, 1, new AIAutoSupervisor(turns, report));


            comp.resetAI();
            subjects.add(comp);
        }
        return subjects;
    }

    public static List<AIInput> setSubjectsRAVETestSimple(IGame game) {
        List<AIInput> subjects = new ArrayList<>();

        int turns = 10000;

        int subjectCount = 2;
        Random rand = new Random(17);
        //ScannerInput hooman = new ScannerInput();
        AIReporter report = new AIReporter(ConfigReporter.noInfo());

        for(int i = 1; i <= subjectCount; i++){
            MonteCarloParameters params = ConfigParams.defaultStart();
            params.tiePoints = 0.5f;
            params.gameParams.moveConverter = new TixTaxMoveConvert();
            params.explorationParameter = 0.8f;

            ConfigParams.setBaseGame(params, game, rand);


            //FIX THIS AND ADD OTHER VARiABLES LIKE KILL VARIABLE
            //ADD RAVE INITIALIZE FUNCTION INTO CONFIGURER
            if(i == 1) params.rave.useRave = false;
            if(i == 2){
                ConfigParams.setRave(params, turns, 0.02f, 0.05f);
            }

            params.useTranspositionTable = false;

            AIInput comp = new AIInput(game, params, 1, new AIAutoSupervisor(turns, report));


            comp.resetAI();
            subjects.add(comp);
        }
        return subjects;
    }

    /**
     * Testing the functionality that eliminated equivalent moves in MCTN
     */
    public static List<AIInput> setSubjectsEquMoveElimTest(IGame game) {
        List<AIInput> subjects = new ArrayList<>();

        int turns = 2400;

        int subjectCount = 2;
        Random rand = new Random(17);
        //ScannerInput hooman = new ScannerInput();
        AIReporter report = new AIReporter(ConfigReporter.noInfo());

        for(int i = 1; i <= subjectCount; i++){
            MonteCarloParameters params = ConfigParams.defaultStart();
            params.tiePoints = 0.5f;
            params.gameParams.moveConverter = new TixTaxMoveConvert();
            params.explorationParameter = 0.8f;

            ConfigParams.setBaseGame(params, game, rand);

            ConfigParams.setRave(params, turns, 0.01f, 0.05f);

            params.useUCBBonuses = true;
            params.recalcUCBCooldown = 10;

            params.useTranspositionTable = false;

            if(i == 1) params.moveSupplyFilter = EMoveSupplyFilter.filterPseudoEquivalents;
            else params.moveSupplyFilter = EMoveSupplyFilter.filterEquivalents;


            AIInput comp = new AIInput(game, params, 1, new AIAutoSupervisor(turns, report));


            comp.resetAI();
            subjects.add(comp);
        }
        return subjects;
    }

    public static List<AIInput> setSubjectsUCBBonusTest(IGame game) {
        List<AIInput> subjects = new ArrayList<>();

        int turns = 2000;

        int subjectCount = 2;
        Random rand = new Random(17);
        //ScannerInput hooman = new ScannerInput();
        AIReporter report = new AIReporter(ConfigReporter.noInfo());

        for(int i = 1; i <= subjectCount; i++){
            MonteCarloParameters params = ConfigParams.defaultStart();
            params.tiePoints = 0.5f;
            params.gameParams.moveConverter = new TixTaxMoveConvert();

            ConfigParams.setBaseGame(params, game, rand);



            params.rave.useRave = true;
            params.rave.raveEquParameter = Math.round(0.15f * turns);
            params.rave.killRaveGameCount = Math.round(0.15f * turns * 2.5f);
            ConfigParams.setRave(params, turns,0.15f, 0.15f*2.5f);

            params.useTranspositionTable = false;

            if(i == 1){
                params.explorationParameter = 0.8f;

                params.useUCBBonuses = true;
                params.recalcBonusCooldown = 10;
                params.recalcUCBCooldown = 10;

                params = ConfigParams.setUCBBonus(params, 0.07f, turns, 0.125f, 0.05f);

            }
            else if (i == 2){
                params.explorationParameter = 0.8f;

                params.useUCBBonuses = true;
                params.recalcBonusCooldown = 10;
                params.recalcUCBCooldown = 10;

                params = ConfigParams.setUCBBonus(params, 0.07f, turns, 0.125f, 0);
            }

            AIInput comp = new AIInput(game, params, 1, new AIAutoSupervisor(turns, report));

            comp.resetAI();
            subjects.add(comp);
        }
        return subjects;
    }

    public static List<AIInput> setSubjectsTurnCountTest(IGame game) {
        List<AIInput> subjects = new ArrayList<>();
        int subjectCount = 2;
        Random rand = new Random(17);
        //ScannerInput hooman = new ScannerInput();
        AIReporter report = new AIReporter(ConfigReporter.noInfo());

        for(int i = 1; i <= subjectCount; i++){
            MonteCarloParameters params = ConfigParams.defaultStart();
            params.tiePoints = 0.5f;
            params.gameParams.moveConverter = new TixTaxMoveConvert();

            ConfigParams.setBaseGame(params, game, rand);

            int turns = 2000;
            params.useTranspositionTable = false;

            if(i == 1){
                turns = 2000;
                ConfigParams.setRave(params, turns, 0.15f, 0.15f * 2.5f);
            }
            else if (i == 2){
                turns = 8000;
                ConfigParams.setRave(params, turns, 0.15f, 0.15f * 2.5f);

            }
            params.explorationParameter = 0.8f;

            params.useUCBBonuses = true;
            params.recalcBonusCooldown = 10;
            params.recalcUCBCooldown = 10;

            AIInput comp = new AIInput(game, params, 1, new AIAutoSupervisor(turns, report));
            comp.resetAI();
            subjects.add(comp);
        }
        return subjects;
    }

    public static List<AIInput> setSubjectsPriorsTest(IGame game){
        List<AIInput> subjects = new ArrayList<>();

        int turns = 2000;

        int subjectCount = 2;
        Random rand = new Random();
        //ScannerInput hooman = new ScannerInput();
        AIReporter report = new AIReporter(ConfigReporter.noInfo());

        for(int i = 1; i <= subjectCount; i++){
            MonteCarloParameters params = ConfigParams.defaultStart();
            params.tiePoints = 0.5f;
            params.gameParams.moveConverter = new TixTaxMoveConvert();
            params.explorationParameter = 0.8f;

            ConfigParams.setBaseGame(params, game, rand);


            ConfigParams.setRave(params, turns, 0.04f, 1f);


            params = ConfigParams.setUCBBonus(params, 0.07f, turns, 0.125f, 0.05f);

            if(i == 1){
                params = ConfigParams.setDomainPriors(params, 8, 8);
            }
            else params.domain.usePriors = false;


            //params.useTranspositionTable = false;

            AIInput comp = new AIInput(game, params, 1, new AITimedSupervisor(100, report));


            comp.resetAI();
            subjects.add(comp);
        }
        return subjects;
    }
}