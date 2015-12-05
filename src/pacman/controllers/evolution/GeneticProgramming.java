package pacman.controllers.evolution;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.PriorityQueue;
import java.util.Random;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * This controller implements a simple genetic programming scheme. The strategy revolves around generating initially
 * random action sequences, evaluating their fitness (score), and then performing a reproductive step by randomly
 * choosing two parents from the surviving population. In an effort to maintain genetic variety, we will
 * randomly select 25% of the population from the unfit population to continue on.
 * Members of the population are Candidate objects defined externally.
 */
public class GeneticProgramming extends Controller<MOVE> {
    // Default weights chosen somewhat at random empirically
    Controller<EnumMap<GHOST, MOVE>> spookies;
    private int numGenerations = 10;
    private int populationSize = 20;
    MOVE[] allMoves = MOVE.values();

    /**
     * Genetic Programming Constructor.
     *
     * @param spookies The ghost controller to be played, and therefore simulated, against.
     */
    public GeneticProgramming(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }


    /**
     * The Genetic Programming schema that will seed an initial population with random action sequences and then
     * attempt to perform crossovers to optimize performance. Will also keep a small subset of the unfit population
     * for the sake of genetic diversity.
     *
     * @param game game to simulate on
     * @return Returns the first move of the best action sequence found after 10 generations
     */

    public MOVE getMove(Game game, long timeDue) {
        // Will automatically sort population for us based on fitness because
        // we implemented the comparable interface in the Candidate object
        PriorityQueue<Candidate> population = new PriorityQueue<>();
        // Used to resort the population after simulation
        PriorityQueue<Candidate> resortedPop = new PriorityQueue<>();
        // Used to select which members of the fit population will be reproduce
        ArrayList<Candidate> reproducers = new ArrayList<>();
        // Used to select which members of the unfit population will be given eugenic clemency
        ArrayList<Candidate> unfitPop = new ArrayList<>();
        Random rn = new Random();

        // Seed the initial population with completely randomized move sequences.
        for (int i = 0; i < populationSize; i++) {
            Candidate temp = new Candidate(game.copy(), generateMoveSequence(20), 0);
            population.add(temp);
        }

        // Iterate number of generation times
        for (int i = 0; i < numGenerations; i++) {
            // Iterate over current population
            for (Candidate candidate : population) {
                Game curGame = candidate.getGame();
                ArrayList<MOVE> actionSequence = candidate.getSequence();
                // Execute action sequence
                for (MOVE move : actionSequence) {
                    // Advance more than once so move has time to execute
                    for (int j = 0; j < 4; j++) {
                        curGame.advanceGame(move, spookies.getMove(curGame.copy(), -1));
                    }
                }
                // Update candidate fitness
                candidate.setFitness(curGame.getScore());
                // Add to second priority queue once fitness is updated for resorting
                resortedPop.add(candidate);
                unfitPop.add(candidate);
            }
            population.clear();
            int newPopSize = 0;
            // Pick the 25% most fit of the population and reinsert them into the pool.
            for (int j = 0; j < populationSize / 4; j++) {
                Candidate temp = resortedPop.remove();
                population.add(temp);
                reproducers.add(temp);
                unfitPop.remove(temp);
                newPopSize++;
            }
            // Now pick another 25% randomly from the non-elite of the population to preserve
            // genetic diversity
            for (int j = 0; j < populationSize / 4; j++) {
                int index = rn.nextInt(unfitPop.size());
                Candidate temp = unfitPop.get(index);
                population.add(temp);
                reproducers.add(temp);
                newPopSize++;
            }
            // Refill population with crossovers from remaining population
            while (newPopSize < populationSize) {
                // Randomly select two distinct members to reproduce
                int indexOne = rn.nextInt(reproducers.size());
                int indexTwo = rn.nextInt(reproducers.size());
                // ensure distinctness amongst parents
                while (indexTwo == indexOne) {
                    indexTwo = rn.nextInt(reproducers.size());
                }
                Candidate parentOne = reproducers.get(indexOne);
                Candidate parentTwo = reproducers.get(indexTwo);
                ArrayList<MOVE> parSeqOne = parentOne.getSequence();
                ArrayList<MOVE> parSeqTwo = parentTwo.getSequence();
                // Reproduce!
                ArrayList<MOVE> newSequence = crossoverActionSequence(parSeqOne, parSeqTwo);
                Candidate child = new Candidate(game.copy(), newSequence, 0);
                newPopSize++;
            }
            reproducers.clear();
            unfitPop.clear();
        }
        // Return the first move of the fittest member of the population
        return population.remove().getSequence().remove(0);
    }

    /**
     * Generates a randomized move sequence in order to seed initial population
     *
     * @param length The length of the random move sequence to be generated
     * @return Returns a randomized action sequence
     */

    public ArrayList<MOVE> generateMoveSequence(int length) {
        ArrayList<MOVE> actionSeq = new ArrayList<>();
        Random rn = new Random();
        for (int i = 0; i < length; i++) {
            int index = rn.nextInt(5);
            actionSeq.add(allMoves[index]);
        }
        return actionSeq;
    }

    /**
     * Randomly chooses an action from either parents action sequence to create a crossover of their
     * action sequences.
     *
     * @param parentSeq1 The first parent sequence
     * @param parentSeq2 The second parent sequence
     * @return Returns a crossed over action sequence generated from parents
     */
    public ArrayList<MOVE> crossoverActionSequence(ArrayList<MOVE> parentSeq1, ArrayList<MOVE> parentSeq2) {
        ArrayList<MOVE> actionSeq = new ArrayList<>();
        Random rn = new Random();
        for (int i = 0; i < parentSeq1.size(); i++) {
            // Flip a coin to see who gets to pass on their genese
            int parent = rn.nextInt(1);
            if (parent == 0) {
                actionSeq.add(parentSeq1.get(i));
            } else {
                actionSeq.add(parentSeq2.get(i));
            }
        }
        return actionSeq;
    }

}


