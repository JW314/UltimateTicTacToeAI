public class DebuggerMCSimulator {
    public static void main(String[] args){
        TixTaxBoard game = new TixTaxBoard(3, 3);
        game.playMove("155");
        game.playMove("251");
        game.playMove("111");
        game.playMove("215");
        MonteCarloSimulator mcs = new MonteCarloSimulator();
        RandomInput randInp = new RandomInput();
        float points = mcs.simulateRounds(1, 100000, new AIGameTracker(game), 0.5f, null);
        System.out.println("POINTS " + points);
    }
}
