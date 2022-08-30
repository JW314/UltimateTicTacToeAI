public interface IAISimulationInput extends IGameInput, IRandom, ICloneable{
    public void setAIGameTracker(AIGameTracker tr);
    public void setMoveConverter(IConverter conv);
    public void setBoard(IGame game);
}
