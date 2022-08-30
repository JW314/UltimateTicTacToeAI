public interface IGameInput {

    public RealMove nextPlay();
    public void processPlay(int player, int moveID);
    public void end();
}
