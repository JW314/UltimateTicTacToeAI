public class AIGameParameters {

    IConverter moveConverter;

    AIGameTracker baseGame;

    public AIGameTracker getFreshGameTracker(){
        return (AIGameTracker) baseGame.clone();
    }

}
