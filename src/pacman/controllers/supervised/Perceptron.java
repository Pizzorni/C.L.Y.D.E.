package pacman.controllers.supervised;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.ArrayList;

import static pacman.game.Constants.*;

/**
 * This implements a Perceptron based controller
 */
public class Perceptron extends Controller<MOVE> {
    private GHOST closestGhost;
    private int closestPill;
    private int closestGhostDist;
    private int closestPillDist;

    private static int training = 100;
    private double[] WEIGHTS = new double[2];

    /* This is the training data
     *		instances represents game states
     *			the first index is the distance to the nearest ghost
     *			the second index is the distance to the nearest pill
     *
     * 	 	expected is the decision
     * 	 		-1 is run away from ghost
     * 	 		1 is go towards pill
     */
    double[][] instances = {
            {5., 2.},
            {5., 5.},
            {5., 10.},
            {5., 15.},
            {5., 20.},
            {10., 2.},
            {10., 5.},
            {10., 10.},
            {10., 15.},
            {10., 20.},
            {15., 2.},
            {15., 5.},
            {15., 10.},
            {15., 15.},
            {15., 20.},
            {20., 2.},
            {20., 5.},
            {20., 10.},
            {20., 15.},
            {20., 20.},
            {200., 5.},
            {200., 10.},
            {200., 15.},
            {200., 20.}};

    int[] expected = {1, -1, -1, -1, -1, 1, 1, -1, -1, -1, 1, 1, 1, -1, -1, 1, 1, 1, 1, -1, 1, 1, 1, 1};


    /**
     * Constructor
     * This trains the perceptron on the training data above
     */
    public Perceptron() {
        int i, j;
        WEIGHTS[0] = Math.random();
        WEIGHTS[1] = Math.random();

        // TRAIN THE PERCEPTRON
        for (i = 0; i < training; i++) {
            for (j = 0; j < instances.length; j++) {
                double dot = WEIGHTS[0] * (instances[j][0]) + WEIGHTS[1] * instances[j][1];
                if (expected[j] != Math.signum(dot)) {
                    //adjust weight
                    WEIGHTS[0] += 1. / 5 * instances[j][0] * expected[j];
                    WEIGHTS[1] += 1. / 5 * instances[j][1] * expected[j];
                }
            }
        }
    }

    /**
     * Evaluates current game state, then uses perceptron weights to decide what to do
     *
     * @param game    A copy of the current game
     * @param timeDue The time the next move is due
     * @return move decision
     */
    public MOVE getMove(Game game, long timeDue) {

        int current = game.getPacmanCurrentNodeIndex();

        gatherData(game);
        int decision = (int) Math.signum(WEIGHTS[0] * closestGhostDist + WEIGHTS[1] * closestPillDist);

        //extra conditions to improve play
        if (closestGhostDist <= 5) {
            decision = -1;
        } else if (closestGhostDist > 30) {
            decision = 1;
        }

        if (decision == -1)
            //run from nearest ghost
            return game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(closestGhost), DM.PATH);
        else
            //run to nearest pill
            return game.getNextMoveTowardsTarget(current, closestPill, DM.PATH);

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
                if (tmp < closestGhostDist) {
                    closestGhostDist = tmp;
                    closestGhost = ghost;
                }
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



