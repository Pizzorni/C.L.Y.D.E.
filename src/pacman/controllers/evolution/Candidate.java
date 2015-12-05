package pacman.controllers.evolution;

import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;

/**
 * This class is represents an element of the population. Used for both Evolutionary Strategy and Genetic Programming.
 * Stores information like game being simulated, fitness, and sequence of actions to be taken.
 */
class Candidate implements Comparable<Candidate> {
    private Game game;
    private ArrayList<Constants.MOVE> sequence;
    private int fitness;

    /**
     * Constructor to build candidates of population
     *
     * @param game The game being simulated
     */

    public Candidate(Game game, ArrayList<Constants.MOVE> sequence, int fitness) {
        this.game = game;
        this.sequence = sequence;
        this.fitness = fitness;
    }

    // Implement comparable so we can use priority queue to order nodes for us.
    // Uses default integer comparison since lower cost is better
    public int compareTo(Candidate other) {
        return (-1 * Integer.compare(this.fitness, other.fitness));
    }

    // Setters and Getters

    public void setGame(Game game) {
        this.game = game;
    }

    public void setSequence(ArrayList<Constants.MOVE> sequence) {
        this.sequence = sequence;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public Game getGame() {
        return game;
    }

    public ArrayList<Constants.MOVE> getSequence() {
        return sequence;
    }

    public int getFitness() {
        return fitness;
    }
}