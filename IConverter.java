public interface IConverter {
    public int getNumberOfMoves();
    public String convertIDtoMove(int id);
    public int convertMoveToID(String move);
    public String convertRawToUser(String move);
    public String convertUserToRaw(String move);
    public String convertRawToCDG(String move);
    public String convertCDGToRaw(String move);
}

