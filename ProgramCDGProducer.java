import java.util.*;
import java.io.*;

public class ProgramCDGProducer {
    public static void main(String[] args) throws Exception
    {
        //Scanner scan = new Scanner(new File("testtext.in"));
        //PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("name.out")));
        Scanner scan = new Scanner(System.in);
        List<String> code = new ArrayList<>();
        for (int i = 0; i < 166; i++) {
            code.add(scan.nextLine());
        }
        System.out.println("ALL LINES READ");
        List<String> filteredCode = new ArrayList<>();

        boolean insideCommentBlock = false;
        for (int i = 0; i < code.size(); i++) {
            String cLine = code.get(i);
            if(cLine.equals("")) continue;
            if(cLine.length() >= 6 && cLine.substring(0, 6).equals("import")) continue;

            String newLine = "";
            boolean indent = true;
            boolean deleteLine = false;
            for(int index = 0; index < cLine.length(); index++){
                if( (cLine).startsWith("//", index)) break;
                if( (cLine).startsWith("/*", index) || (cLine).startsWith("*/", index)){
                    insideCommentBlock = (cLine).startsWith("/*", index);
                    newLine = "";
                    index++;
                    continue;
                }

                if(index >= 1 && (index + 1) < cLine.length()-1 && cLine.substring(index, index+1).equals(" ")){
                    char before = cLine.charAt(index-1);
                    char after = cLine.charAt(index+1);
                    boolean beforeIsLetter = isSpecial(before);
                    boolean afterIsLetter = isSpecial(after);
                    if(!beforeIsLetter || !afterIsLetter){
                        continue;
                    }
                }

                if(indent && cLine.charAt(index) == ' '){
                    continue;
                }
                indent = false;
                newLine += cLine.substring(index, index+1);
            }
            if(newLine.equals("")) continue;
            if(insideCommentBlock) continue;
            if(!deleteLine){
                filteredCode.add(newLine);
            }
            //if(i % 100 == 0) System.out.println("PROGRESS " + i);
        }
        System.out.println("import java.lang.Math;");
        System.out.println("import java.util.*;");
        for (String line : filteredCode){
            System.out.println(line);
        }
        //pw.println("");


        System.out.flush();
        //pw.flush();
    }

    public static boolean isSpecial(char t){
        return (t >= 'a' && t <= 'z') || (t >= 'A' && t <= 'Z') || (t >= '0' && t <= '9') || (t == '-') || (t == '"');
    }
}
