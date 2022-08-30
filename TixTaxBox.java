/**
 * THIS CLASS IS NOW DEPRECATED AND UNUSED
 * All functionalities has been shifted into TixTaxBoard class
 */

public class TixTaxBox implements ICloneable {

    int boardID = 0;

    public TixTaxBox(int size){
        initiate(size);
    }

    public void initiate(int size){
        boardID = 0;
    }

    public void updateCell(int cell, int player){
        //this safety i guess for some reason if i need it; makes it so program doesnt die if a cell already with a
        // nonzero value doesnt die if replaced
        //if(TixTaxInfo.getPieceAt(boardID, cell) != 0) System.out.println("TIXTAX BOX: weird replacing cells or " +
                //"something");
        //boardID -= (TixTaxInfo.powersof3modulo[cell] * TixTaxInfo.getPieceAt(boardID, cell));

        //actual code
        boardID += TixTaxInfo.powersof3modulo[cell]*player;
    }

    public int checkCell(int cellnum){
        return TixTaxInfo.getPieceAt(boardID, cellnum);
    }

    public int[] getCells(){
        return TixTaxInfo.getCells(boardID);
    }
    public Object clone(){
        TixTaxBox copy = new TixTaxBox(9);
        copy.boardID = boardID;
        return copy;
    }
}



