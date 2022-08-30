public class MCParamsRave {
    boolean useRave;

    float raveWeight = 1f;
    float raveAmplify = 0.75f;

    float raveEquParameter = 500;
    // SEE PAGE 18 OF https://www.cs.utexas.edu/~pstone/Courses/394Rspring13/resources/mcrave.pdf for info on
    // equivalence parameter

    float killRaveGameCount = 2000;
    //sets rave to 0 if over this amount of totalGamesPlayed: get rid of rave

    public MCParamsRave(boolean raveOn){
        useRave = raveOn;
    }
    public float calculateRaveValue(MonteCarloTreeNode child, MonteCarloTreeNode parent, int moveID, RaveTracker rave){
        if(!useRave) return 0;
        if(parent.totalGamesPlayed >= killRaveGameCount) return 0;
        float raveValue = rave.getValue(parent.currentPlayer, moveID);
        //float variableWeight = (raveMaxWeight*raveDecayStart) / (raveDecayStart + parent.totalGamesPlayed);
        float variableWeight = (float)Math.sqrt(raveEquParameter/(3 * parent.totalGamesPlayed + raveEquParameter));

        return raveWeight*variableWeight*raveValue;
    }

}
