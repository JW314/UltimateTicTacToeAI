import java.util.Scanner;

public class ScannerInput implements IGameInput {

    Scanner scan;
    IConverter conv;

    public ScannerInput(){
        scan = new Scanner(System.in);
    }
    public ScannerInput(IConverter c){
        scan = new Scanner(System.in);
        conv = c;
    }
    public RealMove nextPlay(){
        String input = scan.nextLine();
        String[] tokens = input.split(" ");
        if(tokens[0].equals("resign")) return new RealMove(0, true, false);
        String action = (tokens.length >= 2) ? tokens[1] : "";

        int playID;
        if(conv == null) playID = Integer.parseInt(tokens[0]); //input is in raw format
        else playID = conv.convertMoveToID(conv.convertUserToRaw(tokens[0]));

        return new RealMove(playID, (action.equals("resign")), action.equals("draw"));
    }

    @Override
    public void processPlay(int player, int moveID) {

    }

    @Override
    public void end() {

    }
}
