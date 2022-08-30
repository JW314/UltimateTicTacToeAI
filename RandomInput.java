import java.util.Random;

public class RandomInput implements IGameInput, IRandom {

    Random rand;

    public RandomInput(){
        rand = new Random();
    }
    public RandomInput(Random r) {rand = r;}
    public void setRandom(Random r) {rand = r;}
    public RealMove nextPlay(){
        return new RealMove(rand.nextInt(81), false, false);
    }

    @Override
    public void processPlay(int player, int moveID) {

    }

    @Override
    public void end() {

    }
}