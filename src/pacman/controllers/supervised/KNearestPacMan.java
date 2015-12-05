package pacman.controllers.supervised;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.ArrayList;

import static pacman.game.Constants.*;

/**
 * This implements a controller with K Nearest Neighbor algorithm
 */
public class KNearestPacMan extends Controller<MOVE> {
    private GHOST closestGhost;
    private int closestPill;
    private int closestGhostDist;
    private int closestPillDist;
    private static int K = 3;

    /* This is the training data
     *
     * It is an array of arrays
     *
     * 	The first index is the distance of the nearest edible ghost
     * 	The second index is the distance of the nearest pill (regular or power)
     * 	The third index is the decision
     * 		0 means run from the nearest ghost
     * 		1 means eat the nearest pill
     */
    private static int[][] INSTANCES = {
            {5, 2, 0},
            {5, 5, 0},
            {5, 10, 0},
            {5, 15, 0},
            {5, 20, 0},
            {10, 2, 1},
            {10, 5, 1},
            {10, 10, 0},
            {10, 15, 0},
            {10, 20, 0},
            {15, 2, 1},
            {15, 5, 1},
            {15, 10, 1},
            {15, 15, 1},
            {15, 20, 0},
            {20, 2, 1},
            {20, 5, 1},
            {20, 10, 1},
            {20, 15, 1},
            {20, 20, 1},
            {Integer.MAX_VALUE, 5, 1},
            {Integer.MAX_VALUE, 10, 1},
            {Integer.MAX_VALUE, 15, 1},
            {Integer.MAX_VALUE, 20, 1}};


    /**
     * Evaluates current game state, then uses training data to decide what to do
     *
     * @param game    A copy of the current game
     * @param timeDue The time the next move is due
     * @return move decision
     */
    public MOVE getMove(Game game, long timeDue) {

        int current = game.getPacmanCurrentNodeIndex();

        gatherData(game);
        int decision = evaluateDistances();

        if (decision == 0)
            //run from nearest ghost
            return game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(closestGhost), DM.PATH);
        else
            //run to nearest pill
            return game.getNextMoveTowardsTarget(current, closestPill, DM.PATH);

    }

    /**
     * Evaluate which K instances are nearest the current game state
     *
     * @return array with K nearest instances
     */
    private int evaluateDistances() {
        //given closest ghost and pill evaluate which instances are closest K
        int[] distances = new int[K];
        int[] indexes = new int[K];
        int i;

        //initialize distances
        for (i = 0; i < K; i++)
            distances[i] = Integer.MAX_VALUE;

        //calculate distances from current game to each instance
        //distance is:
        // 		sqrt( (difference of the ghost distance)^2 + (difference of pill distance)^2) )
        int dist;
        for (i = 0; i < INSTANCES.length; i++) {
            dist = (int) Math.sqrt(Math.pow(INSTANCES[i][0] - closestGhostDist, 2) + Math.pow(INSTANCES[i][1] - closestPillDist, 2));
            if (dist <= distances[0]) {
                distances[2] = distances[1];
                distances[1] = distances[0];
                distances[0] = dist;
                indexes[2] = indexes[1];
                indexes[1] = indexes[0];
                indexes[0] = i;
            } else if (dist <= distances[1]) {
                distances[2] = distances[1];
                distances[1] = dist;
                indexes[2] = indexes[1];
                indexes[1] = i;
            } else if (dist <= distances[2]) {
                distances[2] = dist;
                indexes[2] = i;
            }
        }

        //add decisions of each of the K nearest instances
        // return that sum divided by K
        int sum = 0;
        for (i = 0; i < K; i++) {
            sum += INSTANCES[indexes[i]][2];
        }

        //if 0, then run from nearest ghost
        //if 1, then run to nearest pill
        return sum / K;
    }

    /**
     * Evaluates game state
     * Gets distance of the closest ghost and closest pill
     *
     * @param game
     */
    private void gatherData(Game game) {
        //get distance of nearest non-edible ghost and distance to nearest pill

        int current = game.getPacmanCurrentNodeIndex();


        //Get distance from closest ghost
        closestGhostDist = Integer.MAX_VALUE;
        int tmp;
        //NOTE: this for loop is adapted from the StarterPacMan controller
        for (GHOST ghost : GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                tmp = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
                if (tmp < closestGhostDist)
                    closestGhostDist = tmp;
                closestGhost = ghost;
            }
        }


        //Get distance of nearest pill (normal or power)
        //NOTE: this section is also adapted from the StarterPacMan controller
        int[] pills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();

        ArrayList<Integer> targets = new ArrayList<Integer>();

        for (int i = 0; i < pills.length; i++)                    //check which pills are available
            if (game.isPillStillAvailable(i))
                targets.add(pills[i]);

        for (int i = 0; i < powerPills.length; i++)            //check with power pills are available
            if (game.isPowerPillStillAvailable(i))
                targets.add(powerPills[i]);

        int[] targetsArray = new int[targets.size()];        //convert from ArrayList to array

        for (int i = 0; i < targetsArray.length; i++)
            targetsArray[i] = targets.get(i);

        //return the next direction once the closest target has been identified
        closestPill = game.getClosestNodeIndexFromNodeIndex(current, targetsArray, DM.PATH);
        closestPillDist = game.getShortestPathDistance(current, closestPill);
    }
}























