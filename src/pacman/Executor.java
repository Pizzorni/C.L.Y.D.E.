package pacman;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;
import java.util.Arrays;
import java.util.PriorityQueue;

import pacman.controllers.Controller;
import pacman.controllers.evolution.EvolutionaryStrategy;
import pacman.controllers.evolution.GeneticProgramming;
import pacman.controllers.informed.Astar;
import pacman.controllers.informed.HillClimber;
import pacman.controllers.uninformed.DFSPacMan;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.NearestPillPacMan;
import pacman.controllers.examples.NearestPillPacManVS;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.RandomNonRevPacMan;
import pacman.controllers.examples.RandomPacMan;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Game;
import pacman.game.GameView;

import static pacman.game.Constants.*;

/**
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
@SuppressWarnings("unused")
public class Executor {
    /**
     * The main method. Several options are listed - simply remove comments to use the option you want.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Executor exec = new Executor();


        //run multiple games in batch mode - good for testing.
        //int numTrials=10;
        //exec.runExperiment(new HillClimber(new StarterGhosts()), new StarterGhosts(),numTrials);

		
		/*
        //run a game in synchronous mode: game waits until controllers respond.
		int delay=5;
		boolean visual=true;
		exec.runGame(new RandomPacMan(),new RandomGhosts(),visual,delay);
  		 */

        ///*
        //run the game in asynchronous mode.
        // attemping to fine tune evolutionary computation parameters
        boolean visual = true;
        int numGen = 5;
        int popSize = 10;
        double[] maxes = new double[8];
        double[] avgs = new double[8];
//		for(int i = 1; i < 5; i ++){
//			double[] ret = exec.evolutionaryStrategy(numGen, popSize, (i * -250 ), (i * 250));
//			maxes[i-1] = ret[0];
//			avgs[i-1] = ret[1];
//		}
//		for(int i = 1; i < 5; i ++){
//			double[] ret = exec.evolutionaryStrategy(numGen, popSize, (i * -25), (i * 25));
//			maxes[i - 1 + 4] = ret[0];
//			avgs [i - 1 + 4] = ret[1];
//		}
//
//		for(int i = 0; i < 5; i++){
//			System.out.println("Range:[" + (i*-250) + "," + (i*250) + "] Max: " + maxes[i] + " Avg: " + avgs[i]);
//		}
//		for(int i = 0; i < 5; i++){
//			System.out.println("Range:[" + (i*-25) + "," + (i*25) + "] Max: " + maxes[i-1+4] + " Avg: " + avgs[i-1+4]);
//		}
//		double[] ret = exec.evolutionaryComputation(numGen, popSize, -50, 50, 1);
//		//System.out.println("Max: " + ret[0] + "Avgs: " + ret[1]);
//		maxes[0] = ret[0];
//		avgs[0] = ret[1];
//		ret = exec.evolutionaryComputation(numGen, popSize, -750, 750, 1);
//		maxes[1] = ret[0];
//		avgs[1] = ret[1];
//		ret = exec.evolutionaryComputation(numGen, popSize, -50, 50, 0);
//		maxes[2] = ret[0];
//		avgs[2] = ret[1];
//		exec.evolutionaryComputation(numGen, popSize, -750, 750, 0);
//		maxes[3] = ret[0];
//		avgs[3] = ret[1];
//		for(int i = 0; i < 4; i ++){
//			if(i >= 2){
//				System.out.println("*** EVOLUTIONARY STRATEGY ***");
//			}
//			if(i < 2){
//				System.out.println("*** GENETIC PROGRAMMING ***");
//			}
//			System.out.println("Max: " + maxes[i] + " Avg: " + avgs[i]);
//		}


        //exec.evolutionaryStrategy(5,10,5,visual);
//
        int[] scores = new int[100];
        int max = 0;
        double avg = 0;
        for (int i = 0; i < 100; i++) {
            scores[i] = exec.simulatedAnnealing(100000, 0.003);
            avg += scores[i];
            max = (max < scores[i]) ? scores[i] : max;
            System.out.println("Trial: " + i + " Score: " + scores[i]);
        }
        avg = avg / 100;
        System.out.println("Max: " + max + " Avg: " + avg);

        //exec.runGameTimed(new HillClimber(new StarterGhosts()),new StarterGhosts(),visual);
//		exec.runGameTimed(new HumanController(new KeyBoardInput()),new StarterGhosts(),visual);	
        //*/
		
		/*
		//run the game in asynchronous mode but advance as soon as both controllers are ready  - this is the mode of the competition.
		//time limit of DELAY ms still applies.
		boolean visual=true;
		boolean fixedTime=false;
		exec.runGameTimedSpeedOptimised(new RandomPacMan(),new RandomGhosts(),fixedTime,visual);
		*/
		
		/*
		//run game in asynchronous mode and record it to file for replay at a later stage.
		boolean visual=true;
		String fileName="replay.txt";
		exec.runGameTimedRecorded(new HumanController(new KeyBoardInput()),new RandomGhosts(),visual,fileName);
		//exec.replayGame(fileName,visual);
		 */
    }

    /**
     * For running multiple games without visuals. This is useful to get a good idea of how well a controller plays
     * against a chosen opponent: the random nature of the game means that performance can vary from game to game.
     * Running many games and looking at the average score (and standard deviation/error) helps to get a better
     * idea of how well the controller is likely to do in the competition.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param trials           The number of trials to be executed
     */
    public void runExperiment(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, int trials) {
        double avgScore = 0;

        Random rnd = new Random(0);
        Game game;

        for (int i = 0; i < trials; i++) {
            game = new Game(rnd.nextLong());

            while (!game.gameOver()) {
                game.advanceGame(pacManController.getMove(game.copy(), System.currentTimeMillis() + DELAY),
                        ghostController.getMove(game.copy(), System.currentTimeMillis() + DELAY));
            }

            avgScore += game.getScore();
            System.out.println(i + "\t" + game.getScore());
        }

        System.out.println(avgScore / trials);
    }

    /**
     * Run a game in asynchronous mode: the game waits until a move is returned. In order to slow thing down in case
     * the controllers return very quickly, a time limit can be used. If fasted gameplay is required, this delay
     * should be put as 0.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param visual           Indicates whether or not to use visuals
     * @param delay            The delay between time-steps
     */
    public void runGame(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual, int delay) {
        Game game = new Game(0);

        GameView gv = null;

        if (visual)
            gv = new GameView(game).showGame();

        while (!game.gameOver()) {
            game.advanceGame(pacManController.getMove(game.copy(), -1), ghostController.getMove(game.copy(), -1));

            try {
                Thread.sleep(delay);
            } catch (Exception e) {
            }

            if (visual)
                gv.repaint();
        }
    }

    /**
     * Run the game with time limit (asynchronous mode). This is how it will be done in the competition.
     * Can be played with and without visual display of game states.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param visual           Indicates whether or not to use visuals
     */
    public void runGameTimed(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual) {
        Game game = new Game(0);

        GameView gv = null;

        if (visual)
            gv = new GameView(game).showGame();

        if (pacManController instanceof HumanController)
            gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());

        new Thread(pacManController).start();
        new Thread(ghostController).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
            ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            game.advanceGame(pacManController.getMove(), ghostController.getMove());

            if (visual)
                gv.repaint();
        }

        pacManController.terminate();
        ghostController.terminate();
    }

    /**
     * Run the game in asynchronous mode but proceed as soon as both controllers replied. The time limit still applies so
     * so the game will proceed after 40ms regardless of whether the controllers managed to calculate a turn.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param fixedTime        Whether or not to wait until 40ms are up even if both controllers already responded
     * @param visual           Indicates whether or not to use visuals
     */
    public void runGameTimedSpeedOptimised(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean fixedTime, boolean visual) {
        Game game = new Game(0);

        GameView gv = null;

        if (visual)
            gv = new GameView(game).showGame();

        if (pacManController instanceof HumanController)
            gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());

        new Thread(pacManController).start();
        new Thread(ghostController).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
            ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                int waited = DELAY / INTERVAL_WAIT;

                for (int j = 0; j < DELAY / INTERVAL_WAIT; j++) {
                    Thread.sleep(INTERVAL_WAIT);

                    if (pacManController.hasComputed() && ghostController.hasComputed()) {
                        waited = j;
                        break;
                    }
                }

                if (fixedTime)
                    Thread.sleep(((DELAY / INTERVAL_WAIT) - waited) * INTERVAL_WAIT);

                game.advanceGame(pacManController.getMove(), ghostController.getMove());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (visual)
                gv.repaint();
        }

        pacManController.terminate();
        ghostController.terminate();
    }

    /**
     * Run a game in asynchronous mode and recorded.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController  The Ghosts controller
     * @param visual           Whether to run the game with visuals
     * @param fileName         The file name of the file that saves the replay
     */
    public void runGameTimedRecorded(Controller<MOVE> pacManController, Controller<EnumMap<GHOST, MOVE>> ghostController, boolean visual, String fileName) {
        StringBuilder replay = new StringBuilder();

        Game game = new Game(0);

        GameView gv = null;

        if (visual) {
            gv = new GameView(game).showGame();

            if (pacManController instanceof HumanController)
                gv.getFrame().addKeyListener(((HumanController) pacManController).getKeyboardInput());
        }

        new Thread(pacManController).start();
        new Thread(ghostController).start();

        while (!game.gameOver()) {
            pacManController.update(game.copy(), System.currentTimeMillis() + DELAY);
            ghostController.update(game.copy(), System.currentTimeMillis() + DELAY);

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            game.advanceGame(pacManController.getMove(), ghostController.getMove());

            if (visual)
                gv.repaint();

            replay.append(game.getGameState() + "\n");
        }

        pacManController.terminate();
        ghostController.terminate();

        saveToFile(replay.toString(), fileName, false);
    }

    /**
     * Replay a previously saved game.
     *
     * @param fileName The file name of the game to be played
     * @param visual   Indicates whether or not to use visuals
     */
    public void replayGame(String fileName, boolean visual) {
        ArrayList<String> timeSteps = loadReplay(fileName);

        Game game = new Game(0);

        GameView gv = null;

        if (visual)
            gv = new GameView(game).showGame();

        for (int j = 0; j < timeSteps.size(); j++) {
            game.setGameState(timeSteps.get(j));

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (visual)
                gv.repaint();
        }
    }

    //save file for replays
    public static void saveToFile(String data, String name, boolean append) {
        try {
            FileOutputStream outS = new FileOutputStream(name, append);
            PrintWriter pw = new PrintWriter(outS);

            pw.println(data);
            pw.flush();
            outS.close();

        } catch (IOException e) {
            System.out.println("Could not save data!");
        }
    }

    //load a replay
    private static ArrayList<String> loadReplay(String fileName) {
        ArrayList<String> replay = new ArrayList<String>();

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            String input = br.readLine();

            while (input != null) {
                if (!input.equals(""))
                    replay.add(input);

                input = br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return replay;
    }

    /**
     * A simple simulated annealing implementation. Simulates a game with a hill climber. If the score is
     * better, we accept the move. If the score is worse, we accept it with some probability.
     *
     * @param temp Initial temperature used to compute probability of accepting worse moves.
     *             Recommended range: [10000, 100000]
     * @param coolingRate Rate at which temperature decreases, percentage wise.
     *             Recommended range: [0.0001, 0.005]
     * @return Returns final score of game for statistical analysis.
     */
    private int simulatedAnnealing(double temp, double coolingRate) {
        Game game = new Game(0);
        Executor exec = new Executor();
        StarterGhosts spookies = new StarterGhosts();
        HillClimber climber = new HillClimber(spookies);
        int score = game.getScore();
        int tempScore;
        // While its hot and the game is on, iterate
        while (temp > 1 && !game.gameOver()) {
            Game tempGame = game.copy();
            MOVE climberMove = climber.getMove(tempGame.copy(), -1);
            EnumMap<GHOST, MOVE> spookieMove = spookies.getMove(tempGame.copy(), -1);

            // Simulate a move using hill climber
            for (int i = 0; i < 4; i++) {
                tempGame.advanceGame(climberMove, spookieMove);
            }

            // compute score of move according to simple heuristic
            tempScore = annealingHeuristic(tempGame);
            // If temp score is better, accept the move as valid
            if (tempScore > score) {
                game.advanceGame(climberMove, spookieMove);
            }
            // If temp score is worse, accept with a certain probability
            else {
                double probability = Math.exp((score - tempScore) / temp);
                if (probability > Math.random()) {
                    game.advanceGame(climberMove, spookieMove);
                }
            }
            // cool down a bit
            temp = temp * (1 - coolingRate);
        }
        return game.getScore();
    }


    /**
     * A simple heuristic for annealing. Takes into account the score, number of pills left, and
     * whether its game over to discourage suicide by ghost.
     *
     * @param game The game whose state will be evaluated
     * @return Returns evaluation of current game state according to our simple heuristic
     */
    private int annealingHeuristic(Game game) {
        int score = game.getScore();
        int numPills = game.getActivePillsIndices().length + game.getActivePowerPillsIndices().length;
        int overPenalty = 0;
        Boolean over = game.gameOver();
        if (over) {
            overPenalty = 100;
        }

        return score - numPills - overPenalty;

    }

    /**
     * Implementation of Evolutionary Strategy and Genetic Programming in one. Evolves A* heuristic through either
     * mutation or reproduction as documented below.
     * Implemented here due to performance issues when implemented as a controller. Visual not added due to
     * complications arising from running several hundreds of games. Also a huge performance hit to do so.
     *
     * @param generations Number of generations to run. Recommended range: [5,30]
     * @param popSize     Size of population to be kept each generation. Recommended range: [5,20]
     * @param min         Minimum random value to be used in mutation step. Used mostly in simulated annealing.
     * @param max         Maximum random value to be used in mutation step. Used mostly in simulated annealing.
     * @param genOrEvol   Dictates whether we will use an Evolutionary Strategy or Genetic Programming.
     *                    If 0, Evol. Strat. If 1, Genetic Programming.
     * @return Returns an array containing the Maximum score across all generations and the average scores of
     * all generations. Provided for fine tuning of parameters and statistical analysis in part 2.
     */
    private double[] evolutionaryComputation(int generations, int popSize, int min, int max, int genOrEvol) {
        int rnmin = min;
        int rnmax = max;
        Game game = new Game(0);
        Random rn = new Random();
        int[] genMaxScore = new int[generations];
        double[] genAvgScore = new double[generations];
        int[][] bestWeightsPerGen = new int[generations][8];

        // Slight optimization. Generation 0 will contain a considerably larger population so as to have more variance
        // We will then take the popSize/2 best, and then have a working set of size popSize.
        int initialPop = 5 * popSize;


        Executor exec = new Executor();
        // Priority Queue will sort our population by score. EvolObj compare sorts in reverse order to compensate
        // for the priority queue's "natural ordering"
        PriorityQueue<EvolObj> population = new PriorityQueue<>();
        // Used to resort population in intermediary steps since Queue will not resort itself
        PriorityQueue<EvolObj> resortedPop = new PriorityQueue<>();
        // Used to extract the popSize/2 fittest members of population
        ArrayList<EvolObj> fitPop = new ArrayList<>();
        // Large initial population to add some variance and then smaller mutations
        for (int i = 0; i < initialPop; i++) {
            // Randomize weights of initial population. We use [-1000, 1000] so as to have more variance and prevent
            // getting stuck on a local optimum.
            EvolObj temp = new EvolObj(game.copy(), initialSeed(1000, -1000));
            temp.setScore(0);
            population.add(temp);
        }

        // Iterate over number of generations
        for (int j = 0; j < generations; j++) {
            // maxScore and avgScore computed on a generation by generation basis for statistical anlysis
            int maxScore = 0;
            double avgScore = 0;
            // Clear the previous fitPopulation
            fitPop.clear();
            //System.out.println(j + " ******** GENERATION ******* " + j);
            int i = 0; // count population member number
            // Iterate over the entire candidate population
            for (EvolObj candidate : population) {
                //System.out.print("citizen: " + i + " ");
                // Initialize blank game
                candidate.setGame(game.copy());
                Game curGame = candidate.getGame();
                int[] curWeights = candidate.getWeights();
                // The algorithm we are evolving is A*. We evolve the weights using the alternate
                // constructor provided.
                Astar tempController = new Astar(new StarterGhosts(), curWeights);
                // Advance game until it is complete using Astar's getMove
                while (!curGame.gameOver()) {
                    curGame.advanceGame(tempController.getMove(curGame.copy(), -1), new StarterGhosts().getMove(game.copy(), -1));

                    //try{Thread.sleep(5);}catch(Exception e){}

                }
                // Update score for fitness evaluation
                candidate.setScore(curGame.getScore());
                // Use second priority queue to resort population
                resortedPop.add(candidate);
                avgScore += candidate.getScore();
                // Update max score. If new max score is found, keep track of which weights were used to obtain it too
                if (maxScore < candidate.getScore()) {
                    maxScore = candidate.getScore();
                    bestWeightsPerGen[j] = candidate.getWeights();
                }
                i++; //increment to keep track of candidate number for logging purposes
                // Log population performance. Commented out by default.
                //System.out.println(" score: " + candidate.getScore() + " weights: " + Arrays.toString(candidate.getWeights()));
            }
            // Store statistics for current generation performance
            genMaxScore[j] = maxScore;
            // Take into account that generation 0 is considerably larger
            genAvgScore[j] = (j == 0) ? avgScore / initialPop : avgScore / popSize;

            // choose the fittest half of the population based off game score
            for (int k = 0; k < popSize / 2; k++) {
                EvolObj temp = resortedPop.remove();
                fitPop.add(temp);
            }

            // Clear priority queues in preparation for next generation
            population.clear();
            resortedPop.clear();
            for (EvolObj citizen : fitPop) {
                population.add(citizen);
            }

            // Crossover and Mutation step, dependent on Evol Strat/Genetic Programming flag
            // Since we chose the popSize/2 best, we need to generate another popSize/2 candidates
            for (int k = 0; k < popSize / 2; k++) {
                // Randomly choose who to mutate/reproduce from the best
                int indexOne = rn.nextInt(popSize / 2);
                // Evolutionary Strategy
                if (genOrEvol == 0) {
                    EvolObj mutatee = fitPop.get(indexOne);
                    // M-m-m-m-m-mutate
                    int[] newWeights = mutate(mutatee, rnmax, rnmin);
                    // Create new member of population given new mutated weights
                    EvolObj newCitizen = new EvolObj(game.copy(), newWeights);
                    newCitizen.setScore(0);
                    population.add(newCitizen);
                }
                // Genetic Programming
                if (genOrEvol == 1) {
                    // Randomly choose another member of the population with which to reproduce
                    int indexTwo = rn.nextInt(popSize / 2);
                    // Make sure that we chose a different member of the population. Asexual reproduction does nothing
                    // given our reproduction function revolves around averaging weights
                    while (indexTwo == indexOne) {
                        indexTwo = rn.nextInt(popSize / 2);
                    }
                    EvolObj p1 = fitPop.get(indexOne);
                    EvolObj p2 = fitPop.get(indexTwo);
                    EvolObj child = new EvolObj(game.copy(), reproduce(p1, p2));
                    child.setScore(0);
                    population.add(child);
                }
            }

//			System.out.println("GENERATION " + j + " RESULTS");
//			System.out.println("Max: " + genMaxScore[j] + " Avg: " + genAvgScore[j]);
//			System.out.println("Weights used: " + Arrays.toString(bestWeightsPerGen[j]));

        }
        // Statistical analysis over all generations
        double allGenMax = 0;
        double allGenAvg = 0;
        for (int i = 0; i < generations; i++) {
            //System.out.println("Generation " + i + " max score: " + genMaxScore[i] + " avg score: " + genAvgScore[i]);
            allGenMax = (allGenMax < genMaxScore[i]) ? genMaxScore[i] : allGenMax;
            allGenAvg += genAvgScore[i];
        }
        allGenAvg = allGenAvg / generations;

        double[] retVals = new double[]{allGenMax, allGenAvg};
        return retVals;

    }

    /**
     * Mutation function. Generates random numbers in the range [rnmin, rnmax] and adds them to
     * to the weight vector of the current EvolObj. Predominantly used for Evolution Strategy.
     *
     * @param state The evolutionary object that will be mutated to produce a new member of the pop
     * @param rnmax Maximum random value to be used in mutation step. Fixed unless using simulated annealing.
     * @param rnmin Minimum random value to be used in mutation step. Fixed unless using simulated annealing.
     * @return Returns new mutated weights generated from current weights
     */

    public int[] mutate(EvolObj state, int rnmax, int rnmin) {
        Random rn = new Random();
        int[] newWeights = new int[8];
        int[] oldWeights = state.getWeights();
        for (int i = 0; i < 8; i++) {
            newWeights[i] = oldWeights[i] + (rn.nextInt(rnmax - rnmin + 1) + rnmin);
        }
        return newWeights;

    }

    /**
     * Crossover/reproduction function. Generates new weights by averaging the weights of two parents chosen
     * from the fit population.
     *
     * @param p1 Parent one
     * @param p2 Parent two
     * @return Returns new weights generated from crossover of two parents
     */
    public int[] reproduce(EvolObj p1, EvolObj p2) {
        int[] p1Weights = p1.getWeights();
        int[] p2Weights = p2.getWeights();
        int[] newWeights = new int[8];
        for (int i = 0; i < 8; i++) {
            newWeights[i] = (p1Weights[i] + p2Weights[i]) / 2;
        }
        return newWeights;
    }

    /**
     * Initial random seeding function used to generate weights for initial population in evolutionary algos.
     * Numbers are generated from the range [-1000, 1000] which is considerably more random than the mutation step
     * in order to ensure a varied initial population. Hopefully will prevent getting stuck on local
     * optimums. Will potentially be modified by simulated annealing in part 2.
     *
     * @param rnmax Maximum random value to be used in initial weight generation step.
     * @param rnmin Minimum random value to be used in initial weight generation step.
     * @return Returns weight vector for initial population
     */
    public int[] initialSeed(int rnmax, int rnmin) {
        Random rn = new Random();
        int[] weights = new int[8];
        for (int i = 0; i < 8; i++) {
            weights[i] = rn.nextInt(rnmax - rnmin + 1) + rnmin;
        }
        return weights;
    }


    /**
     * This class is a wrapper object used to keep track of relevant evolutionary information.
     * Maintains information including game being simulated and its score/weights.
     */
    class EvolObj implements Comparable<EvolObj> {
        private Game game;
        private int score;
        private int[] weights;

        /**
         * Constructor to build tree nodes
         *
         * @param game    The game being simulated
         * @param weights Vector of weights used in A* heuristic function
         */

        public EvolObj(Game game, int[] weights) {
            this.game = game;
            this.weights = weights;
        }

        // Setters and getters
        public void setGame(Game game) {
            this.game = game;
        }

        public void setWeights(int[] weights) {
            this.weights = weights;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getScore() {
            return score;
        }

        public int[] getWeights() {
            return weights;
        }

        public Game getGame() {
            return game;
        }


        // Implement comparable so we can use priority queue to sort population for us based on
        // fitness, which is score/performance in this case.
        // Reverses the default comparison since a higher score is better, and natural integer ordering
        // would give the worst performing members of the population the worst score.

        // TL;DR: If score of this member of the population is higher, return -1 so that the
        // priority queue thinks it is "smaller" based off natural ordering
        public int compareTo(EvolObj other) {
            int thisScore = this.getGame().getScore();
            int otherScore = other.getGame().getScore();
            if (thisScore > otherScore) {
                return -1;
            } else if (thisScore == otherScore) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
