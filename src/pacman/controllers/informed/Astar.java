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
    private int GHOST_DIST_WT = 100;
    private int NEAREST_GHOST_WT = 100;
    private int NUM_PILL_WT = 0;
    private int DIST_PILL_WT = 10;
    private int PILLS_TO_TRACK = 0;
    private int numIters = 2;

    Controller<EnumMap<GHOST, MOVE>> spookies;

    public Astar(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
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
        for(int i = 0; i < numIters - 1; i++){
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
        for (int i = 0; i < allPills.length; i++) {
            int currDist = game.getManhattanDistance(pacmanPos, allPills[i]);
            distToPills.add(currDist);
        }

        // compute distance to all non edible ghosts and compute distance to closest ghost
        int minDistToGhost = Integer.MAX_VALUE;
        int totalDistToGhost = 1;
        int inactiveGhostCount = 0;
        for (GHOST spookie : GHOST.values()) {
            if(game.getGhostEdibleTime(spookie) > 0){
                inactiveGhostCount++;
                break;
            }
            int currDist = game.getManhattanDistance(pacmanPos, game.getGhostCurrentNodeIndex(spookie));
            totalDistToGhost += currDist;
            minDistToGhost = (minDistToGhost > currDist) ? currDist : minDistToGhost;
        }


        if(inactiveGhostCount == 4){
            minDistToGhost = 1;
        }

        int distToClosestPills = 0;
        for(int i = 0; i < PILLS_TO_TRACK; i++){
            distToClosestPills += distToPills.remove();
        }
        if(totalDistToGhost == 0) totalDistToGhost = 1;
        if(minDistToGhost == 0) minDistToGhost = 1;
        return (NUM_PILL_WT * totalPills) + (DIST_PILL_WT * distToClosestPills)
                + (NEAREST_GHOST_WT * (1/minDistToGhost)) + (GHOST_DIST_WT * (1/(totalDistToGhost)));
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





