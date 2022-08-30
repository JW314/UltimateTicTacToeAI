import java.util.Map;
import java.util.HashMap;

public class TixTaxMoveConvert implements IConverter{

    Map<String, String> mapRawToUser;
    Map<String, String> mapUserToRaw;

    Map<String, String> mapRawToCDG;
    Map<String, String> mapCDGToRaw;

    public TixTaxMoveConvert(){
        initialize();
    }

    public void initialize(){
        mapRawToUser = new HashMap<>();
        mapUserToRaw = new HashMap<>();
        for (int row = 0; row < 9; row++) {
            String r = Integer.toString(row);
            String r1 = Integer.toString(row+1);
            for (int col = 0; col < 9; col++) {
                String c = Integer.toString(col);
                String c1 = Integer.toString(col+1);
                mapRawToUser.put(r+c, r1+c1);
                mapUserToRaw.put(r1+c1, r+c);
            }
        }

        mapRawToCDG = new HashMap<>();
        mapCDGToRaw = new HashMap<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int box = (row / 3) * 3 + (col / 3);
                int cell = (row % 3) * 3 + (col % 3);
                mapRawToCDG.put(box+""+cell, row+" "+col);
                mapCDGToRaw.put(row+" "+col, box+""+cell);
            }
        }
    }

    @Override
    public int getNumberOfMoves() {
        return 81;
    }

    public String convertIDtoMove(int move){
        int boxnum = move / 9;
        int cellnum = move % 9;
        return boxnum+""+cellnum;
    }
    public int convertMoveToID(String move){
        int boxnum = Integer.parseInt(move.substring(0, 1));
        int cellnum = Integer.parseInt(move.substring(1, 2));
        return boxnum * 9 + cellnum;
    }

    public String convertRawToUser(String move){
        if(move.length() == 2){
            return mapRawToUser.get(move);
        }else{
            return move.substring(0, 1) + mapRawToUser.get(move.substring(1));
        }
    }

    public String convertUserToRaw(String move){
        if(move.length() == 2){
            return mapUserToRaw.get(move);
        }else{
            return move.substring(0, 1) + mapUserToRaw.get(move.substring(1));
        }
    }

    public String convertRawToCDG(String move){
        return mapRawToCDG.get(move.substring(move.length()-2));
    }
    public String convertCDGToRaw(String move){
        return mapCDGToRaw.get(move);
    }

}
