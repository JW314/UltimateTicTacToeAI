public class DebuggerMonteCarloSearch {
    public static void main(String[] args){
        aiInput();
    }
    public static void aiTester(){
        TixTaxBoard game = new TixTaxBoard(3, 3);
        AIPlayer p = new AIPlayer(game, 81, 1, new MonteCarloParameters());
        p.simulator = new MonteCarloSimulator();
        p.chooseMove(20000);
    }
    public static void aiInput(){
        TixTaxBoard game = new TixTaxBoard(3, 3);
        MonteCarloParameters p1 = new MonteCarloParameters();
        p1.gameParams.moveConverter = new TixTaxMoveConvert();
        AIInput inp = new AIInput(game, p1, 1, new AIHumanSupervisor(25000));
        int play = inp.nextPlay().playID;
        System.out.println("THE AI PLAYS " + play);
    }
}
