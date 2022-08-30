public class ConfigReporter {

    boolean printCDGOutput;

    boolean printCalculatingMessage;
    boolean printExecute;

    boolean printFinalSearchDepth;
    boolean printAllSearchDepth;
    boolean printFinalReport;
    boolean printAllReports;
    boolean printFinalMovesConsidered;
    boolean printAllMovesConsidered;
    boolean printFinalRecs;
    boolean printAllRecs;
    boolean printFinalVariations;
    boolean printAllVariations;
    boolean printFinalTimeTaken;
    boolean printAllTimeTaken;
    int numRecs = 0;


    public ConfigReporter(){

    }
    public ConfigReporter(boolean calc, boolean freports, boolean areports, boolean fconsidered, boolean aconsidered,
                          boolean frecs, boolean arecs, boolean exe, boolean ftime, boolean atime, int numRecs){
        printCalculatingMessage = calc;
        printFinalReport = freports;
        printAllReports = areports;
        printFinalMovesConsidered = fconsidered;
        printAllMovesConsidered = aconsidered;
        printFinalRecs = frecs;
        printAllRecs = arecs;
        printExecute = exe;
        printFinalTimeTaken = ftime;
        printAllTimeTaken = atime;
        this.numRecs = numRecs;
    }

    public static ConfigReporter allInfo(){
        ConfigReporter config = new ConfigReporter();
        config.printCDGOutput = false;
        config.printCalculatingMessage = true;
        config.printAllSearchDepth = true;
        config.printAllReports = true;
        config.printAllMovesConsidered = true;
        config.printAllRecs = true;
        config.printExecute = true;
        config.printAllTimeTaken = true;
        config.printAllVariations = true;
        config.numRecs = 1000;
        return config;
    }
    public static ConfigReporter noInfo(){
        ConfigReporter config = new ConfigReporter();
        return config;
    }
    public static ConfigReporter program(){
        ConfigReporter config = new ConfigReporter();
        config.printCalculatingMessage = true;
        config.printFinalSearchDepth = true;
        config.printAllReports = true;
        config.printAllMovesConsidered = true;
        config.printFinalRecs = true;
        config.printExecute = true;
        config.printAllTimeTaken = true;
        config.printFinalVariations = true;
        config.numRecs = 1000;
        return config;
    }

    public static ConfigReporter onlyCDG(){
        ConfigReporter config = new ConfigReporter();
        config.printCDGOutput = true;
        return config;
    }

}
