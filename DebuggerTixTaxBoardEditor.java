public class DebuggerTixTaxBoardEditor {

    public TixTaxBoard readNotation(String notation) {
        TixTaxBoard g = new TixTaxBoard(3, 3);

        int playcount = 0;
        int curBox = 0;
        int curCell = 0;
        int index = 0;
        for(index = 0; index < notation.length(); index++){
            char symbol = notation.charAt(index);
            if(symbol == 'X'){
                g.manualForceSet(curBox, curCell, 1);
                curCell++; playcount++;
            }
            else if(symbol == 'O'){
                g.manualForceSet(curBox, curCell, 2);
                curCell++; playcount++;
            }
            else if(symbol == '/'){
                curBox++;
                curCell = 0;
            }
            else if (symbol >= '1' && symbol <= '9'){
                curCell += (symbol - '1') + 1;
            }
            else if (symbol == ' '){
                index++;
                break;
            }
        }
        if(curBox != 8) System.out.println("DEBUG EDITOR NOTATION CONVERT ERROR: FINAL BOX NOT REACHED");


        g.currentPlayer = (notation.charAt(index) == 'X') ? 1 : 2;
        index += 2;
        g.currentbox = (notation.charAt(index) == 'A') ? -1 : (notation.charAt(index) - '1');
        g.plays = playcount;
        int res = g.winTracker.reCheckVictory();
        g.reCalcHash();
        System.out.println("EDITOR RECHECK " + res);

        String crossCheckNotation = g.buildNotation();
        if(!crossCheckNotation.equals(notation)){
            System.out.println("DEBUG EDITOR NOTATION CONVERT ERROR: MISMATCH");
        }


        return g;

    }

    //only one box is unfilled. whoever wins that box wins the game
    public TixTaxBoard singleBox(){
        return readNotation("9/6XXX/6XXX/6XXX/6OOO/6XXX/6XXX/6XXX/6OOO X A");
    }

    public TixTaxBoard drawTest(){
        TixTaxBoard g = new TixTaxBoard(3, 3);
        g.manualForceSet(1, 0, 1);
        g.manualForceSet(1, 1, 1);
        g.manualForceSet(1, 2, 1);
        g.manualForceSet(2, 0, 2);
        g.manualForceSet(2, 1, 2);
        g.manualForceSet(2, 2, 2);
        g.manualForceSet(3, 0, 2);
        g.manualForceSet(3, 1, 2);
        g.manualForceSet(3, 2, 2);
        g.manualForceSet(4, 0, 2);
        g.manualForceSet(4, 1, 2);
        g.manualForceSet(4, 2, 2);
        g.manualForceSet(5, 0, 1);
        g.manualForceSet(5, 1, 1);
        g.manualForceSet(5, 2, 1);
        g.manualForceSet(6, 0, 1);
        g.manualForceSet(6, 1, 1);
        g.manualForceSet(6, 2, 1);
        g.manualForceSet(7, 0, 2);
        g.manualForceSet(7, 1, 2);
        g.manualForceSet(7, 2, 2);
        g.manualForceSet(8, 0, 1);
        g.manualForceSet(8, 1, 1);
        g.manualForceSet(8, 2, 1);

        g.manualForceSet(0, 1, 1);
        g.manualForceSet(0, 2, 2);
        g.manualForceSet(0, 4, 2);
        g.manualForceSet(0, 7, 2);
        g.manualForceSet(0, 8, 1);

        g.currentPlayer = 1;
        g.currentbox = 0;
        g.plays = 26;
        int res = g.winTracker.reCheckVictory();
        System.out.println("EDITOR RECHECK " + res);

        return g;
    }



    public TixTaxBoard suddenDeath(){
        TixTaxBoard g = new TixTaxBoard(3, 3);

        /**
        g.manualForceSet(0, 0, 2);
        g.manualForceSet(0, 1, 1);
        g.manualForceSet(0, 2, 2);
        g.manualForceSet(0, 3, 2);
        g.manualForceSet(0, 4, 1);
        g.manualForceSet(0, 5, 2);
         **/

        g.manualForceSet(0, 0, 2);
        g.manualForceSet(0, 1, 2);
        g.manualForceSet(0, 3, 1);

        g.manualForceSet(1, 0, 1);
        g.manualForceSet(1, 1, 1);
        g.manualForceSet(1, 2, 1);
        g.manualForceSet(2, 0, 1);
        g.manualForceSet(2, 1, 1);
        g.manualForceSet(2, 2, 1);
        g.manualForceSet(3, 0, 2);
        g.manualForceSet(3, 1, 2);
        g.manualForceSet(3, 2, 2);
        g.manualForceSet(4, 0, 1);
        g.manualForceSet(4, 1, 1);
        g.manualForceSet(4, 2, 1);
        g.manualForceSet(5, 0, 2);
        g.manualForceSet(5, 1, 2);
        g.manualForceSet(5, 2, 2);
        g.manualForceSet(6, 0, 2);
        g.manualForceSet(6, 1, 2);
        g.manualForceSet(6, 2, 2);
        g.manualForceSet(7, 0, 2);
        g.manualForceSet(7, 1, 2);
        g.manualForceSet(7, 2, 2);
        g.manualForceSet(8, 0, 1);
        g.manualForceSet(8, 1, 1);
        g.manualForceSet(8, 2, 1);

        g.currentPlayer = 1;
        g.currentbox = 0;
        g.plays = 26;
        int res = g.winTracker.reCheckVictory();
        System.out.println("EDITOR RECHECK " + res);

        return g;
    }

    public TixTaxBoard jva(){
        TixTaxBoard g = new TixTaxBoard(3, 3);

        g.manualForceSet(0, 0, 1);
        g.manualForceSet(0, 1, 1);
        g.manualForceSet(0, 2, 1);
        g.manualForceSet(1, 0, 2);
        g.manualForceSet(1, 1, 2);
        g.manualForceSet(1, 2, 2);
        g.manualForceSet(2, 0, 1);
        g.manualForceSet(2, 1, 1);
        g.manualForceSet(2, 2, 1);

        g.manualForceSet(3, 0, 1);
        g.manualForceSet(3, 2, 1);
        g.manualForceSet(3, 8, 1);
        g.manualForceSet(3, 3, 2);
        g.manualForceSet(3, 7, 2);

        g.manualForceSet(4, 0, 1);
        g.manualForceSet(4, 1, 1);
        g.manualForceSet(4, 2, 1);

        g.manualForceSet(5, 0, 2);
        g.manualForceSet(5, 1, 1);
        g.manualForceSet(5, 2, 1);

        g.manualForceSet(6, 0, 2);
        g.manualForceSet(6, 1, 2);
        g.manualForceSet(6, 2, 2);

        g.manualForceSet(7, 1, 1);
        g.manualForceSet(7, 7, 1);
        g.manualForceSet(7, 0, 2);
        g.manualForceSet(7, 2, 2);
        g.manualForceSet(7, 5, 2);

        g.manualForceSet(8, 1, 1);
        g.manualForceSet(8, 7, 1);
        g.manualForceSet(8, 0, 2);


        g.currentPlayer = 2;
        g.currentbox = 0;
        g.plays = 0;
        int res = g.winTracker.reCheckVictory();
        System.out.println("EDITOR RECHECK " + res);

        return g;
    }


}
