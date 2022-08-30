import java.util.ArrayList;

public class Initializer {

    public static boolean STARTED = initialize();
    public static boolean initialize(){
        //System.out.println("INITIALIZER INIT");
        TixTaxInfo.initialize();
        Tester.roundCounts = new ArrayList<>();

        return true;
    }
}
