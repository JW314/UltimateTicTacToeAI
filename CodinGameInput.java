import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CodinGameInput implements IGameInput  {

    Scanner scan;
    IConverter conv;
    List<String> inputs;
    int currentInputIndex;

    public CodinGameInput(IConverter c){
        scan = new Scanner(System.in);
        conv = c;
        inputs = new ArrayList<>();
        currentInputIndex = 0;
    }
    public List<String> getInputs() { return inputs; }
    public void addInput(String in){
        inputs.add(in);
    }

    //I don't really care about the valid actions, I calculate those myself
    public String readCycle(){
        int oppRow = scan.nextInt();
        int oppCol = scan.nextInt();
        String move = oppRow + " " + oppCol;

        int count = scan.nextInt();
        for (int i = 0; i < count*2; i++) {
            int uhhhsurethanks = scan.nextInt();
        }
        return move;
    }

    public RealMove nextPlay(){
        while(currentInputIndex >= inputs.size()){
            inputs.add(readCycle());
        }
        String moveStr = conv.convertCDGToRaw(inputs.get(currentInputIndex));
        currentInputIndex++;
        int moveID = conv.convertMoveToID(moveStr);
        return new RealMove(moveID, false, false);
    }
    public void processPlay(int player, int moveID){

    }
    public void end(){

    }
}
