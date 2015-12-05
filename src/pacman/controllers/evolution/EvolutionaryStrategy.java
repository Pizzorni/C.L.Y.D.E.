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
 * This controller implements a simple evolutionary strategy. The strategy revolves around generating initially
 * random action sequences, evaluating their fitness (score), and then mutating the top 25% to repopulate.
 * Members of the population are Candidate objects defined externally.
 */
public class EvolutionaryStrategy extends Controller<MOVE> {
    // Default weights chosen somewhat at random empirically
    Controller<EnumMap<GHOST, MOVE>> spookies;
    private int numGenerations = 10;
    private int populationSize = 20;
    MOVE[] allMoves = MOVE.values();

    /**
     * Evolutionary Strategy Constructor.
     *
     * @param spookies The ghost controller to be played, and therefore simulated, against.
     */
    public EvolutionaryStrategy(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }


    /**
     * The Evolutionary Strategy that will seed an initial population with random action sequences and then
     * mutate the sequence of actions in an effort to optimize performance. Will retain the top 25% of the
     * population and randomly mutate them.
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
        // Used to select which members of the fit population will be mutated
        ArrayList<Candidate> mutatees = new ArrayList<>();
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
                Game curGame = game.copy();
                candidate.setGame(curGame);
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
            }
            population.clear();
            int newPopSize = 0;
            // Pick the 25% most fit of the population and reinsert them into the pool
            // also add them to list of mutation candidates
            for (int j = 0; j < populationSize / 4; j++) {
                Candidate temp = resortedPop.remove();
                population.add(temp);
                mutatees.add(temp);
                newPopSize++;
            }
            // Refill population with mutated copies of original population
            while (newPopSize < populationSize) {
                // Randomly select one of the top 25% to mutate
                int index = rn.nextInt(mutatees.size());
                Candidate willMutate = mutatees.get(index);
                ArrayList<MOVE> oldSeq = willMutate.getSequence();
                ArrayList<MOVE> mutatedSeq = mutateActionSequence(oldSeq);
                // Create new mutated candidate and add to population
                Candidate mutated = new Candidate(game.copy(), mutatedSeq, 0);
                population.add(mutated);
                newPopSize++;
            }
            mutatees.clear();
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
     * Randomly mutates an action sequence to repopulate
     *
     * @param oldSeq The sequence to be randomly mutated
     * @return Returns a randomly mutated copy of the original action sequence
     */
    public ArrayList<MOVE> mutateActionSequence(ArrayList<MOVE> oldSeq) {
        ArrayList<MOVE> actionSeq = new ArrayList<>();
        Random rn = new Random();
        for (int i = 0; i < oldSeq.size(); i++) {
            // Flip a coin
            int mutate = rn.nextInt(1);
            // If heads, we mutate randomly
            if (mutate == 1) {
                int index = rn.nextInt(5);
                MOVE newMove = allMoves[index];
                actionSeq.add(newMove);
            }
            // If tails, we take the original move
            else if (mutate == 0) {
                actionSeq.add(oldSeq.get(i));
            }
        }
        return actionSeq;
    }

}






