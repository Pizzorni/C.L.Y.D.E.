package pacman.controllers.informed;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.PriorityQueue;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * This class is the implementation of Astar search. Uses a priority queue sorted by cost to
 * choose which move to expand in the tree. Returns the original move that led to the
 * lowest and deepest cost move currently in the tree. Due to performance issues, can only evaluate
 * roughly 12 moves before timing out.
 */
public class Astar extends Controller<MOVE> {
    // Default weights chosen somewhat at random empirically
    private int GHOST_DIST_WT = -4;
    private int NEAREST_GHOST_WT = -569; //300
    private int NUM_PILL_WT = 309; //400
    private int DIST_PILL_WT = 58;
    private int GHOST_EDIBLE_WT = 894; //5 --> 2359
    private int PILLS_TO_TRACK = -637;
    private int numIters = 2;
    private int GHOST_PANIC_DIST = 603;
    private int GHOST_PANIC_VAL = -604;


    Controller<EnumMap<GHOST, MOVE>> spookies;

    /**
     * Default constructor used to run Astar on its own.
     *
     * @param spookies The ghost controller to be played, and therefore simulated, against.
     */
    public Astar(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }

    /**
     * Additional constructor for usage with evolutionary computation. Allows for modification
     * and evolution of weights used in heuristic.
     *
     * @param spookies The ghost controller to be played, and therefore simulated, against.
     * @param weights  array of weights to be used in heuristic
     */
    public Astar(Controller<EnumMap<GHOST, MOVE>> spookies, int[] weights) {
        this.spookies = spookies;
        this.GHOST_DIST_WT = weights[0];
        this.NEAREST_GHOST_WT = weights[1];
        this.NUM_PILL_WT = weights[2];
        this.GHOST_EDIBLE_WT = weights[3];
        this.DIST_PILL_WT = weights[4];
        this.PILLS_TO_TRACK = weights[5];
        this.GHOST_PANIC_DIST = weights[6];
        this.GHOST_PANIC_VAL = weights[7];


    }

    /**
     * The actual Astar search which will find the best move before timing out.
     *
     * @param game game to simulate on
     */

    public MOVE getMove(Game game, long timeDue) {
        int iters = 0;

        // Priority Queue will automatically sort moves based off cost for us
        PriorityQueue<Node> evaluatedMoves = new PriorityQueue<>();

        // Root node with no score, no move, no parent, and depth of 0
        Node root = new Node(game.copy(), 0, null, null, 0);
        evaluatedMoves.add(root);

        // num iters set to 2 else we timeout and cannot find a move fast enough
        while (iters < numIters) {
            iters++;
            // pop the current best move and evaluate its children
            Node best = evaluatedMoves.remove();
            Node parent = null;
            for (MOVE move : MOVE.values()) {

                Game tempGame = game.copy();
                // Need to advance a couple of times to let returned move actually occur
                for (int j = 0; j < 4; j++) {
                    tempGame.advanceGame(move, spookies.getMove(tempGame, -1));
                }
                int tempCost = evaluateState(tempGame);
                // if not at root, set parent
                if (iters != 0) {
                    parent = best;
                }
                // create new node with cost of parent + new cost, move taken, depth, and pointer to parent
                Node temp = new Node(tempGame, best.getCost() + tempCost, move, parent, iters);
                evaluatedMoves.add(temp);
            }
        }

        // once done searching, pop the best move
        Node bestOverall = evaluatedMoves.remove();
        // we want to find the lowest cost most explored move, so we iterate through the best moves until
        // we find a move at an acceptable depth
        while (bestOverall.getDepth() != numIters) {
            bestOverall = evaluatedMoves.remove();
        }

        // once we have found a suitable node, climb the tree to find the original move made (e.g. the first move)
        // and return that since that is what got us here
        for (int i = 0; i < numIters - 1; i++) {
            bestOverall = bestOverall.getParent();
        }
        return bestOverall.getMove();


    }

    /*
     * Evaluate state takes in a game after a move is played and computes the cost of having made that move
     */

    private int evaluateState(Game game) {
        int[] activePills = game.getActivePillsIndices();
        int[] activePowerPills = game.getActivePowerPillsIndices();
        int numPills = activePills.length;
        int numPowerPills = activePowerPills.length;
        int totalPills = numPills + numPowerPills;
        int[] allPills = new int[totalPills];
        int pacmanPos = game.getPacmanCurrentNodeIndex();
        PriorityQueue<Integer> distToPills = new PriorityQueue<>();

        // We consider all pills equally instead of discriminating between power and non power pills for the
        // sake of simplicity, and we create and array with all the pill indices
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
            // If ghost is edible
            if (game.getGhostEdibleTime(spookie) > 0) {
                inactiveGhostCount++;
                minDistToEdible = (minDistToGhost > currDist) ? currDist : minDistToGhost;
                break;
            }

            totalDistToGhost += currDist;
            minDistToGhost = (minDistToGhost > currDist) ? currDist : minDistToGhost;
        }

        // if no ghosts are edible, zero out value so as not to have Integer.MAX_VALUE dominate the evaluation
        if (inactiveGhostCount == 0) {
            minDistToEdible = 0;
        }
        // Similarily, if all ghosts edible, zero out value
        if (inactiveGhostCount == 4) {
            minDistToGhost = 0;
        }

        // If a ghost is too close, call an audible and change minDist to be higher so as to cause this move
        // to incur a higher cost
        if (minDistToGhost < GHOST_PANIC_DIST) {
            minDistToGhost = GHOST_PANIC_VAL;
        }
        // If all ghosts are reasonable far away, we don't care. Zero out.
        else {
            minDistToGhost = 0;
        }


        int distToClosestPills = 0;
        // Here to prevent a no such element exception when running evolutionary computation. Because of randomness,
        // number of Pills to track can exceed number of pills on board
        PILLS_TO_TRACK %= numPills;
        for (int i = 0; i < PILLS_TO_TRACK; i++) {
            distToClosestPills += distToPills.remove();
        }
        // Compute cost by multiplying weights by values
        int eval = (NUM_PILL_WT * totalPills) + (DIST_PILL_WT * distToClosestPills)
                + (NEAREST_GHOST_WT * minDistToGhost) + (GHOST_DIST_WT * totalDistToGhost)
                + (GHOST_EDIBLE_WT * minDistToEdible);
        return eval;
    }
}

/**
 * This class is a wrapper object used to build our tree. Keeps track of relevant information for each game state
 * like move made, cost, and score. Also keeps track of logistical tree information like parent and depth.
 */
class Node implements Comparable<Node> {
    private Game game;
    private int cost;
    private MOVE move;
    private Node parent;
    private int depth;

    /**
     * Constructor to build tree nodes
     *
     * @param game   The game being simulated
     * @param cost   The cost of making this move
     * @param move   The move made
     * @param parent Parent node
     * @param depth  "Depth" at which this node is in the tree
     */

    public Node(Game game, int cost, MOVE move, Node parent, int depth) {
        this.game = game;
        this.cost = cost;
        this.move = move;
        this.parent = parent;
        this.depth = depth;
    }

    // Implement comparable so we can use priority queue to order nodes for us.
    // Uses default integer comparison since lower cost is better
    public int compareTo(Node other) {
        return Integer.compare(this.cost, other.cost);
    }

    // Setters and getters
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





