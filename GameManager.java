import java.util.List;
import java.util.ArrayList;

enum GameWinInfo{
    undecided,
    winByGame,
    drawByGame,
    winByResign,
    drawByAgreement,
}

/**
 * Manages a Game (IPlayable object) between two players, tracking moves and victory
 * Players are represented as IGameInput objects
 *
 * Settings can be configured to perfrom various actions or print various statements about the game
 */
public class GameManager {

    IGame game;
    IGameInput inputPlayer1;
    IGameInput inputPlayer2;

    ISimulationAnalyzer analyzer;

    List<RealMove> history;

    GameWinInfo winInfo;

    public int gameResult = 0;
    public boolean resetGame = true;
    public boolean printPlayer = true;
    public boolean printInvalidMsg = true;
    public boolean printInitialState = true;
    public boolean printAllStates = true;
    public boolean printFinalState = true;
    public boolean printResult = true;

    public GameManager(IGame g, IGameInput in){
        initialize(g, in, in);
    }
    public GameManager(IGame g, IGameInput in1, IGameInput in2){
        initialize(g, in1, in2);
    }
    private void initialize(IGame g, IGameInput in1, IGameInput in2){
        game = g;
        inputPlayer1 = in1;
        inputPlayer2 = in2;
        gameResult = 0;
        history = new ArrayList<>();
        winInfo = GameWinInfo.undecided;
    }
    //shhhhhhhhh
    public void setAllSettingsFalse(){
        resetGame = false;
        printPlayer = false;
        printInvalidMsg = false;
        printInitialState = false;
        printAllStates = false;
        printFinalState = false;
        printResult = false;
    }
    public void addSimAnalyzer(ISimulationAnalyzer a){
        analyzer = a;
    }

    //ignores all prints, resign/draw shenanigans
    //used for quick and clean MC simulations
    public int playQuickGame(){
        int currentPlayer = game.getCurrentPlayer();

        while(true){
            int result = 0;
            RealMove play;
            while(true){
                if(currentPlayer == 1){
                    play = inputPlayer1.nextPlay();
                }else{
                    play = inputPlayer2.nextPlay();
                }

                //no try catch here so be careful ig
                result = game.playMove(currentPlayer, play.playID);

                if(result != -2) break;
            }

            inputPlayer1.processPlay(currentPlayer, play.playID);
            inputPlayer2.processPlay(currentPlayer, play.playID);
            if(analyzer != null) analyzer.processPlay(currentPlayer, play.playID);

            if(result != 0){
                //game over
                if(analyzer != null) analyzer.setWinner(result);
                gameResult = result;
                return result;
            }

            currentPlayer = 3-currentPlayer;
        }
    }
    public int playGame(){
        Tester.checkPoint("GM - pregame start");
        if(resetGame) game.start();
        Tester.checkPoint("GM - postgame start");

        winInfo = GameWinInfo.undecided;

        int currentPlayer = game.getCurrentPlayer();
        boolean[] drawOffers = new boolean[2];

        Tester.checkPoint("GM - preprint");
        if(printInitialState){
            System.out.println("INITIAL: ");
            String[] gameString = game.stateString();
            for(String s : gameString){
                System.out.println(s);
            }
        }
        Tester.checkPoint("GM - postprint");

        while(true){
            if(printPlayer) System.out.println("Current Player: " + currentPlayer);
            int result = 0;
            RealMove play = null;
            while(true){
                if(currentPlayer == 1){
                    Tester.checkPoint("GM - into AI Input");
                    play = inputPlayer1.nextPlay();
                    Tester.checkPoint("GM - outta AI Input");
                }else{
                    play = inputPlayer2.nextPlay();
                }
                if(play.resign) break;
                try{
                    result = game.playMove(currentPlayer, play.playID);
                }
                catch(Exception e){
                    result = -2;
                }
                if(result != -2) break;
                if(printInvalidMsg) System.out.println("Invalid Move; Please Retry");
            }
            history.add(play);

            drawOffers[currentPlayer-1] = play.offerdraw;
            boolean endInAgreedDraw = true;
            for(boolean offer : drawOffers) endInAgreedDraw = endInAgreedDraw && offer;
            if(endInAgreedDraw){
                result = -1;
            }else{
                if(play.resign == false){
                    inputPlayer1.processPlay(currentPlayer, play.playID);
                    inputPlayer2.processPlay(currentPlayer, play.playID);
                    if(analyzer != null) analyzer.processPlay(currentPlayer, play.playID);
                }
                else{
                    if(currentPlayer == 1) result = 2;
                    else if(currentPlayer == 2) result = 1;
                }

            }

            if(printAllStates || (result != 0 && printFinalState) ){
                if(result != 0) System.out.println("FINAL: ");
                String[] gameString = game.stateString();
                for(String s : gameString){
                    System.out.println(s);
                }

                List<String> drawInfo = new ArrayList<>();
                if(drawOffers[0]) drawInfo.add("X offers Draw");
                if(drawOffers[1]) drawInfo.add("O offers Draw");
                String drawDetails = "";
                for(int i = 0; i < drawInfo.size(); i++){
                    if(i > 0) drawDetails += " ; ";
                    drawDetails += drawInfo.get(i);
                }
                if(!drawDetails.equals("")) System.out.println(drawDetails);

            }

            if(result != 0){
                //game over
                if(result == -1){
                    if(endInAgreedDraw) winInfo = GameWinInfo.drawByAgreement;
                    else winInfo = GameWinInfo.drawByGame;
                    if(printResult){
                        if(endInAgreedDraw) System.out.println("Game Ends in a Tie by Agreement");
                        else System.out.println("Game Ends in a Tie");
                    }
                }else{
                    if(play.resign) winInfo = GameWinInfo.winByResign;
                    else winInfo = GameWinInfo.winByGame;
                    if(printResult){
                        String winner = (result == 1) ? "X" : "O";
                        if(play.resign) System.out.println("Player " + winner + " wins by Resignation!");
                        else System.out.println("Player " + winner + " wins!");
                    }
                }
                if(analyzer != null) analyzer.setWinner(result);
                gameResult = result;
                return result;
            }

            if(currentPlayer == 1) currentPlayer = 2;
            else currentPlayer = 1;
        }
    }

    public List<RealMove> getHistory() {
        return history;
    }

    public String printGameRecord(String p1name, String p2name, IConverter conv){

        //just using string interpolation for the heck of it
        String record = String.format("%s[X] vs %s[O] ", p1name, p2name);
        if(gameResult == 0){
            record += "In Progress";
            return record;
        }

        record += "Res: ";
        if(gameResult == 1) record += String.format("%s", p1name);
        if(gameResult == 2) record += String.format("%s", p2name);
        record += ((gameResult == -1) ? "D" : (gameResult == 1) ? "[X]" : "[O]");
        record += "; ";

        boolean formalGameEnd = winInfo == GameWinInfo.winByResign || winInfo == GameWinInfo.drawByAgreement;
        for (int moveNum = 0; moveNum < history.size(); moveNum++) {
            RealMove move = history.get(moveNum);

            //use a space if new turn, or this is the final move
            boolean nullmove = moveNum == history.size()-1 && formalGameEnd;

            if(nullmove) continue;

            if(moveNum % 2 == 0) record += (moveNum/2+1) + ".";
            record += conv.convertRawToUser(conv.convertIDtoMove(move.playID));

            boolean newturn = moveNum % 2 == 0 && moveNum < history.size()-1;
            record += (newturn && !(moveNum == history.size()-2 && formalGameEnd)) ? "/" : " ";
        }
        record += gameResult == 0 ? "cont" : ((gameResult == -1) ? "Draw" : (gameResult == 1) ? "X wins" : "O wins");
        if(winInfo == GameWinInfo.winByResign) record += " by Res";
        if(winInfo == GameWinInfo.drawByAgreement) record += " by Agree";
        return record;
    }
}
