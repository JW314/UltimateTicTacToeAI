public interface IAISupervisor {
    public void reportNode(AIPlayer player);
    public void runningCycle(AIPlayer player);
    public String command(AIPlayer player);
    public int getTurnLength();
    public int getRoundCount();
}
