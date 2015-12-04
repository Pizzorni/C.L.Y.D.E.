package pacman.controllers.supervised;

import java.util.ArrayList;

import pacman.controllers.Controller;
import pacman.game.Game;

import static pacman.game.Constants.*;

/*
 * Perceptron woo!
 */
public class Perceptron extends Controller<MOVE> {
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
	//private int[][] INSTANCES;
	private double[] WEIGHTS;

	//private int[] instCount;


	public Perceptron(double[] weights) {
		//this.INSTANCES = instances;
		this.WEIGHTS = weights;
		//this.instCount = new int[INSTANCES.length];
	}


	public MOVE getMove(Game game, long timeDue) {

		int current = game.getPacmanCurrentNodeIndex();

		gatherData(game);
		int decision = (int) Math.signum(WEIGHTS[0] * closestGhostDist + WEIGHTS[1] * closestPillDist);

		if (decision == -1)
			//run from nearest ghost
			return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(closestGhost), DM.PATH);
		else
			//run to nearest pill
			return game.getNextMoveTowardsTarget(current, closestPill, DM.PATH);

	}

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



