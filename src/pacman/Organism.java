package pacman;

import pacman.game.Game;

/**
 * This class is a wrapper object used to keep track of relevant evolutionary information.
 * Maintains information including game being simulated and its fitness/weights.
 */
class Organism implements Comparable<Organism> {
    private Game game;
    private int fitness;
    private int[] weights;

    /**
     * Constructor to build tree nodes
     *
     * @param game    The game being simulated
     * @param weights Vector of weights used in A* heuristic function
     */

    public Organism(Game game, int[] weights, int fitness) {
        this.game = game;
        this.weights = weights;
        this.fitness = fitness;
    }

    // Copy constructor
    public Organism(Organism source) {
        this.game = source.getGame().copy();
        this.fitness = source.getFitness();
        this.weights = source.getWeights();
    }

    // Setters and getters
    public void setGame(Game game) {
        this.game = game;
    }

    public void setWeights(int[] weights) {
        this.weights = weights;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getFitness() {
        return fitness;
    }

    public int[] getWeights() {
        return weights;
    }

    public Game getGame() {
        return game;
    }


    // Implement comparable so we can use priority queue to sort population for us based on
    // fitness, which is fitness/performance in this case.
    // Reverses the default comparison since a higher fitness is better, and natural integer ordering
    // would give the worst performing members of the population the worst fitness.

    // TL;DR: If fitness of this member of the population is higher, return -1 so that the
    // priority queue thinks it is "smaller" based off natural ordering
    public int compareTo(Organism other) {
        return -1 * Integer.compare(this.fitness, other.fitness);
    }
}
