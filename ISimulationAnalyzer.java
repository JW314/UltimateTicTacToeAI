public interface ISimulationAnalyzer {
    public void updateRave(RaveTracker rave);
    public void processPlay(int player, int moveID);
    public void setWinner(int w);
}
