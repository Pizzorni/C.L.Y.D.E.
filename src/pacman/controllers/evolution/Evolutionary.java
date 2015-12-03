package pacman.controllers.evolution;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.Executor;
import java.util.Random;
import java.util.PriorityQueue;
import java.util.EnumMap;

import static pacman.game.Constants.*;


/*
 */
public class Evolutionary extends Controller<MOVE>{
    private int numIters = 2;
    private int rnmin = -1000;
    private int rnmax = -1000;
    private int NUMGAMES = 5;
    private int NUMTRIALS = 10;

    Controller<EnumMap<GHOST, MOVE>> spookies;

    public Evolutionary(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }


    public MOVE getMove(Game game, long timeDue) {
        int iters = 0;
        int numTrials = 10;
        PriorityQueue<EvolObj> sortedPopulation = new PriorityQueue<>();
        EvolObj[] population = new EvolObj[NUMGAMES];
        for(int i = 0; i < NUMGAMES; i++){
            population[i] = new EvolObj(game.copy(), initialSeed());
        }


        for (int i = 0; i < NUMGAMES; i++) {
            int avgScore = 0;
            int turncount = 0;
            int curScore = 0;
            int maxScore = -1;
            int tempCost = 0;
            EvolObj curState = population[i];
            Game curGame = curState.getGame();
            while (!curGame.gameOver()) {
                for(MOVE move: MOVE.values()){
                    for(int j = 0; j < 4; j++){
                        curGame.advanceGame(move, spookies.getMove(curGame, -1));
                    }
                    tempCost = evaluateState(curState);
                    curScore = curGame.getScore();
                    maxScore = (maxScore > curScore) ? maxScore : curScore;
                }
            }
            population[i].setScore(curGame.getScore());
            sortedPopulation.add(population[i]);
        }



        return sortedPopulation.remove().getMove();
    }

    public void mutate(EvolObj state){
        Random rn = new Random();
        int[] newWeights = new int[8];
        int[] oldWeights = state.getWeights();
        for(int i = 0; i < 8; i++){
            newWeights[i] = oldWeights[i] + rn.nextInt(rnmax - rnmin + 1) + rnmin;
        }
        state.setWeights(newWeights);

    }

    public int[] reproduce(EvolObj p1, EvolObj p2) {
        int[] p1Weights = p1.getWeights();
        int[] p2Weights = p2.getWeights();
        int[] weights = new int[8];
        for(int i = 0; i < 8; i++){
            weights[i] = (p1Weights[i] + p2Weights[i])/2;
        }
        return weights;
    }

    public int[] initialSeed(){
        Random rn = new Random();
        int[] weights = new int[8];
        for(int i = 0; i < 8; i++){
            weights[i] = rn.nextInt(rnmax - rnmin + 1) + rnmin;
        }
        return weights;
    }


    private int evaluateState(EvolObj state) {
        Game game = state.getGame();
        int[] weights = state.getWeights();

        int[] activePills = game.getActivePillsIndices();
        int[] activePowerPills = game.getActivePowerPillsIndices();
        int numPills = activePills.length;
        int numPowerPills = activePowerPills.length;
        int totalPills = numPills + numPowerPills;
        int[] allPills = new int[totalPills];
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        PriorityQueue<Integer> distToPills = new PriorityQueue<>();

        for (int i = 0; i < numPills; i++) {
            allPills[i] = activePills[i];
        }

        for (int i = 0; i < numPowerPills; i++) {
            allPills[numPills + i] = activePowerPills[i];
        }

        // compute distance to all pills
        for (int i = 0; i < numPills; i++) {
            int currDist = game.getManhattanDistance(pacmanPos, activePills[i]);
            distToPills.add(currDist);
        }

        // compute distance to all non edible ghosts and compute distance to closest ghost
        int minDistToGhost = Integer.MAX_VALUE;
        int totalDistToGhost = 1;
        int inactiveGhostCount = 0;
        int minDistToEdible = Integer.MAX_VALUE;
        for (GHOST spookie : GHOST.values()) {
            int currDist = game.getManhattanDistance(pacmanPos, game.getGhostCurrentNodeIndex(spookie));
            if(game.getGhostEdibleTime(spookie) > 0){
                inactiveGhostCount++;
                minDistToEdible = (minDistToGhost > currDist) ? currDist : minDistToGhost;
                break;
            }

            totalDistToGhost += currDist;
            minDistToGhost = (minDistToGhost > currDist) ? currDist : minDistToGhost;
        }


        if(inactiveGhostCount == 0){
            minDistToEdible = 0;
        }
        if(inactiveGhostCount == 4){
            minDistToGhost = 0;
        }

        if(minDistToGhost < weights[6]){
            minDistToGhost = weights[7];
        }
        else{
            minDistToGhost = 0;
        }


        int distToClosestPills = 0;
        for(int i = 0; i < weights[5]; i++){
            distToClosestPills += distToPills.remove();
        }

        int eval = (weights[2] * totalPills) + (weights[4] * distToClosestPills)
                + (weights[1] * minDistToGhost) + (weights[0] * totalDistToGhost)
                + (weights[3] * minDistToEdible);
        // if(eval < 0) eval = 0;
        return eval;
    }

}

class EvolObj implements Comparable<EvolObj>{
    private Game game;
    private int score;
    private int[] weights;
    private MOVE move;

    public EvolObj(Game game, int[] weights){
        this.game = game;
        this.score = game.getScore();
        this.weights = weights;
    }

    public void setGame(Game game){
        this.game = game;
    }

    public void setWeights(int[] weights){
        this.weights = weights;
    }
    public void setScore(int score){
        this.score = score;
    }

    public void setMove(MOVE move){
        this.move = move;
    }

    public MOVE getMove(){
        return move;
    }

    public int getScore(){
        return score;
    }

    public int[] getWeights(){
        return weights;
    }

    public Game getGame(){
        return game;
    }

    public int compareTo(EvolObj other) {
        return Integer.compare(this.score, other.getScore());
    }
}





