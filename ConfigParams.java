import java.util.Random;

public class ConfigParams {

    public static void setBaseGame(MonteCarloParameters p, IGame game, Random r){
        AIGameTracker base1 = new AIGameTracker(game);
        base1.setMoveSupplier(new TixTaxMoveSupplier());
        base1.setSimulationInput(new AISimulationInput3(r, new TixTaxMoveConvert()));
        base1.setHasher(null);
        base1.setHeuristics();
        p.setBaseGame(base1);
    }
    public static void setTestBaseGame(MonteCarloParameters p, IGame game, Random r){
        AIGameTracker base1 = new AIGameTracker(game);
        base1.setMoveSupplier(new TixTaxMoveSupplier());
        base1.setSimulationInput(new AISimulationInput2(r, new TixTaxMoveConvert()));
        base1.setHasher(null);
        base1.setHeuristics();
        p.setBaseGame(base1);
    }

    public static void setBaseUseTable(MonteCarloParameters p, IGame game, boolean usetable){
        p.useTranspositionTable = usetable;
        p.gameParams.baseGame.hasher = null;

    }

    /**
    //confReqSims: 400 - 536 ; 150 - 563 ; 100 - 536; 200 - 529; 170 - 512; 130 - 541; 140 = 516
    public static void setConfCalc300rds(MonteCarloParameters p){
        p.confCalculatorKey = 1;
        p.maxB = 0.6f;
        p.confReqSims = 600;
        p.reqScore = 40.5f;
        p.depthWeight = 1f;
        p.confDiffWeight = 0f;
    }
    
    public static void setConfCalc100ms(MonteCarloParameters p){
        p.confCalculatorKey = 1;
        p.maxB = 0.8f;
        p.confReqSims = 150;
        p.reqScore = 40.5f;
        p.depthWeight = 1f;
        p.confDiffWeight = 0f;
    }

    public static void setBestMove120000rds(MonteCarloParameters p){
        p.confCalculatorKey = 1;
        p.maxB = 0.8f;
        p.confReqSims = 32000;
        p.reqScore = 40.5f;
        p.depthWeight = 1f;
        p.confDiffWeight = 0f;
    }
     **/

    public static MonteCarloParameters defaultStart(){
        MonteCarloParameters p = new MonteCarloParameters();
        p.gameParams.moveConverter = new TixTaxMoveConvert();
        p.tiePoints = 0.5f;
        p.useTranspositionTable = false;
        p.recalcUCBCooldown = 5;
        p.moveSupplyFilter = EMoveSupplyFilter.filterPseudoEquivalents;
        p.resignThreshold = 0.25f;

        return p;
    }

    public static MonteCarloParameters setRave(MonteCarloParameters p, int estTurns, float equPoint, float killPoint){
        p.rave = new MCParamsRave(true);
        p.rave.raveAmplify = 0.1f;
        p.rave.raveEquParameter = Math.round(equPoint * estTurns);
        p.rave.killRaveGameCount = Math.round(killPoint * estTurns);

        return p;
    }

    public static MonteCarloParameters setUCBBonus(MonteCarloParameters p, float strength, int turns, float halfPoint, float maxPoint){
        p.useUCBBonuses = true;
        p.ucbBonusStrength = strength;
        p.ucbBonusEquParameter = turns * halfPoint;
        p.ucbBonusMaxPoint = turns * maxPoint;

        return p;
    }

    public static MonteCarloParameters setDomainPriors(MonteCarloParameters p, float baseWeight, float bonusWeight){
        p.domain.usePriors = true;
        p.domain.priorWeightBase = baseWeight;
        p.domain.priorWeightBonus = bonusWeight;

        return p;
    }

}
