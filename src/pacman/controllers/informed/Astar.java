package pacman.controllers.informed;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.EnumMap;

import static pacman.game.Constants.*;

import java.util.ArrayList;
import java.util.PriorityQueue;

/*
 */
public class Astar extends Controller<MOVE> {
    private int GHOST_DIST_WT = 0;
    private int NEAREST_GHOST_WT = 1000; //300
    private int NUM_PILL_WT = 400; //400
    private int DIST_PILL_WT = 0;
    private int GHOST_EDIBLE_WT = 5; //5 --> 2359
    private int PILLS_TO_TRACK = 0;
    private int numIters = 2;
    private int GHOST_PANIC_DIST = 5;
    private int GHOST_PANIC_VAL = 100;
    private Game game;

    Controller<EnumMap<GHOST, MOVE>> spookies;

    public Astar(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }

    // constructor for evolutionary computation so we can mutate and recombine weights
    public Astar(Controller<EnumMap<GHOST, MOVE>> spookies, int[] weights, Game game){
        this.spookies = spookies;
        this.GHOST_DIST_WT = weights[0];
        this.NEAREST_GHOST_WT = weights[1];
        this.NUM_PILL_WT = weights[2];
        this.GHOST_EDIBLE_WT = weights[3];
        this.DIST_PILL_WT = weights[4];
        this.PILLS_TO_TRACK = weights[5];
        this.GHOST_PANIC_DIST = weights[6];
        this.GHOST_PANIC_VAL = weights[7];
        this.game = game;


    }

    public MOVE getMove(Game game, long timeDue) {
        int iters = 0;
        ArrayList<Node> explored = new ArrayList<>();
        PriorityQueue<Node> evaluatedMoves = new PriorityQueue<>();


        Node root = new Node(game.copy(), 0, null, null, 0);
        evaluatedMoves.add(root);

        while (iters < numIters) {
            iters++;
            Node best = evaluatedMoves.remove();
            Node parent = null;
            for (MOVE move : MOVE.values()) {

                Game tempGame = game.copy();
                for(int j = 0; j < 4; j++) {
                    tempGame.advanceGame(move, spookies.getMove(tempGame, -1));
                }
                int tempCost = evaluateState(tempGame);
                if(iters != 0) {
                    parent = best;
                }

                Node temp = new Node(tempGame, best.getCost() + tempCost, move, parent, iters);
                evaluatedMoves.add(temp);
            }
            //numIters--;
        }

        Node bestOverall = evaluatedMoves.remove();
        while(bestOverall.getDepth() != numIters){
            bestOverall = evaluatedMoves.remove();
        }

        for(int i = 0; i < numIters -1 ; i++){
            bestOverall = bestOverall.getParent();
        }
        return bestOverall.getMove();


    }

    private int evaluateState(Game game) {
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

        if(minDistToGhost < GHOST_PANIC_DIST){
            minDistToGhost = GHOST_PANIC_VAL;
        }
        else{
            minDistToGhost = 0;
        }


        int distToClosestPills = 0;
        for(int i = 0; i < PILLS_TO_TRACK; i++){
            distToClosestPills += distToPills.remove();
        }

        int eval = (NUM_PILL_WT * totalPills) + (DIST_PILL_WT * distToClosestPills)
                + (NEAREST_GHOST_WT * minDistToGhost) + (GHOST_DIST_WT * totalDistToGhost)
                + (GHOST_EDIBLE_WT * minDistToEdible);
       // if(eval < 0) eval = 0;
        return eval;
    }
}


class Node implements Comparable<Node> {
    private Game game;
    private int cost;
    private MOVE move;
    private Node parent;
    private int depth;

    public Node(Game game, int cost, MOVE move, Node parent, int depth) {
        this.game = game;
        this.cost = cost;
        this.move = move;
        this.parent = parent;
        this.depth = depth;
    }

    public int compareTo(Node other) {
        return Integer.compare(this.cost, other.cost);
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMove(MOVE move) {
        this.move = move;
    }

    public void setParent(Node node) {
        this.parent = node;
    }

    public int getCost() {
        return cost;
    }

    public Game getGame() {
        return game;
    }

    public MOVE getMove() {
        return move;
    }

    public Node getParent() {
        return parent;
    }

    public int getDepth() {
        return this.depth;
    }

}





