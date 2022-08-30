import java.util.*;

public class DebuggerMisc {
    public static void main(String[] args){
        Initializer.initialize();



        TixTaxBoard game = new TixTaxBoard(3, 3);
        DebuggerTixTaxBoardEditor debug = new DebuggerTixTaxBoardEditor();

        game = debug.readNotation("6XXX/6OOO/XX1OX4/1OX2OXX1/6OOO/6XXX/1XX1O2XX/1XX1X3X/6XXX X A");
        TixTaxMoveSupplier tester = new TixTaxMoveSupplier();

        /**
         tester.setBoard(game, true);
         String[] stateString = game.stateString();
         for(String l : stateString) System.out.println(l);
         System.out.println(convertIDtoRawMultiple(tester.getAllValidMoves(EMoveSupplyFilter.noFilter)));
         System.out.println(convertIDtoRawMultiple(tester.getAllValidMoves(EMoveSupplyFilter.filterEquivalents)));
         System.out.println(convertIDtoRawMultiple(tester.getAllValidMoves(EMoveSupplyFilter.filterPseudoEquivalents)));
         **/

        int boardID = 1540;
        long[] counts = TixTaxInfo.boardInfos[boardID].lopPlayoutCounts;
        float[] probs = TixTaxInfo.boardInfos[boardID].lopPlayoutProbabilities;
        float[] et = TixTaxInfo.getLOPEvenTies(boardID);
        long num = TixTaxInfo.boardInfos[boardID].lopNumPlayouts;
        System.out.println("probs " + boardID + "  X:" + counts[0] + " O:" + counts[1] + " T:" + counts[2]);
        System.out.println("probs " + boardID + "  X:" + probs[0] + " O:" + probs[1] + " T:" + probs[2]);
        System.out.println("even ties " + et[0] + " " + et[1]);

        System.out.println(TixTaxInfo.boardInfos[boardID].winner);

        System.out.println(Tester.infoInit + " INIT TIME " + (Tester.infoInit/Tester.msnsConversion) + "ms");

    }

    public static List<String> convertIDtoRawMultiple(List<Integer> ids){
        List<String> rawMoves = new ArrayList<>();
        TixTaxMoveConvert converter = new TixTaxMoveConvert();
        for(int id : ids) rawMoves.add(converter.convertRawToUser(converter.convertIDtoMove(id)));
        return rawMoves;
    }
}


