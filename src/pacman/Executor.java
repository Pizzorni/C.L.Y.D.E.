package pacman;

import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.informed.HillClimber;
import pacman.game.Game;
import pacman.game.GameView;

import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Random;

import static pacman.game.Constants.*;

/** our import statements ***********/
;
/**************************************/

/**
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
public class Executor {
    /**
     * The main method. Several options are listed - simply remove comments to use the option you want.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Executor exec = new Executor();
        boolean visual = true;


        //run multiple games in batch mode - good for testing.
        //int numTrials=10;
        //exec.runExperiment(new HillClimber(new StarterGhosts()), new StarterGhosts(),numTrials);


        //run a game in synchronous mode: game waits until controllers respond.
        int delay = 5;
        // exec.runGame(new RandomPacMan(), new RandomGhosts(), visual, delay);


        //run the game in asynchronous mode.
//		exec.runGameTimed(new IterDeepPacMan(new StarterGhosts()), new StarterGhosts(), visual);
//		exec.runGameTimed(new KNearestPacMan(), new StarterGhosts(), visual);
        // exec.runGameTimed(new BFS(new StarterGhosts()), new StarterGhosts(), visual);
//		exec.runGameTimed(new NearestPillPacMan(),new AggressiveGhosts(),visual);
//		exec.runGameTimed(new DFSPacMan(new StarterGhosts()),new StarterGhosts(),visual);
//		exec.runGameTimed(new HumanController(new KeyBoardInput()),new StarterGhosts(),visual);	

        //exec.runGame(new ID3(new StarterGhosts()), new StarterGhosts(), visual, delay);

        System.out.println("Trial: 1 Generations: 5 PopSize: 10");
        CambrianExplosion boom1 = new CambrianExplosion(5, 10, 1);
        boom1.explode();
        System.out.println("Trial: 2 Generations: 5 PopSize: 20");
        CambrianExplosion boom2 = new CambrianExplosion(5, 20, 1);
        boom2.explode();
        System.out.println("Trial: 3 Generations: 10 PopSize: 10");
        CambrianExplosion boom3 = new CambrianExplosion(10, 10, 1);
        boom3.explode();
        System.out.println("Trial: 4 Generations: 10 PopSize: 20");
        CambrianExplosion boom4 = new CambrianExplosion(10, 20, 1);
        boom4.explode();
        System.out.println("Trial: 5 Generations: 20 PopSize: 20");
        CambrianExplosion boom5 = new CambrianExplosion(20, 20, 1);
        boom5.explode();
        System.out.println("Trial: 6 Generations: 100 PopSize: 20");
        CambrianExplosion boom6 = new CambrianExplosion(100, 20, 1);
        boom6.explode();




        ///*
        //run the game in asynchronous mode.
        // attemping to fine tune evolutionary computation parameters
        //  boolean visual = true;
//        int numGen = 5;
//        int popSize = 10;
//        double[] maxes = new double[8];
//        double[] avgs = new double[8];
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
//        int[] scores = new int[100];
//        int max = 0;
//        double avg = 0;
//        for (int i = 0; i < 100; i++) {
//            scores[i] = exec.simulatedAnnealing(100000, 0.003);
//            avg += scores[i];
//            max = (max < scores[i]) ? scores[i] : max;
//            System.out.println("Trial: " + i + " Score: " + scores[i]);
//        }
//        avg = avg / 100;
//        System.out.println("Max: " + max + " Avg: " + avg);

        //  exec.runGameTimed(new ID3(new StarterGhosts()),new StarterGhosts(),visual);
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

    public void runGameTimedPerceptron(Controller<EnumMap<GHOST, MOVE>> ghostController, int training) {
        int[][] instances = {
                {5, 2}, {5, 5}, {5, 10}, {5, 15}, {5, 20},
                {10, 2}, {10, 5}, {10, 10}, {10, 15}, {10, 20},
                {15, 2}, {15, 5}, {15, 10}, {15, 15}, {15, 20},
                {20, 2}, {20, 5}, {20, 10}, {20, 15}, {20, 20},
                {Integer.MAX_VALUE, 5}, {Integer.MAX_VALUE, 10}, {Integer.MAX_VALUE, 15}, {Integer.MAX_VALUE, 20}};

        int[] expected = {-1, -1, 1, -1, -1, -1, 1, 1, -1, -1, -1, 1, 1, 1, 1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        double[] weights = new double[2];

        int i, j;
        weights[0] = Math.random();
        weights[1] = Math.random();
        System.out.printf("initial weights\n[%f, %f]\n", weights[0], weights[1]);
        // TRAIN THE PERCEPTRON
        for (i = 0; i < training; i++) {
            int non_match = 0;
            for (j = 0; j < instances.length; j++) {
                double dot = weights[0] * instances[j][0] + weights[1] * instances[j][1];
                //System.out.println(dot);
                if (expected[j] != Math.signum(dot)) {
                    non_match++;
                    //adjust weight
                    weights[0] += 1 / 3 * (double) instances[j][0];
                    weights[1] += 1 / 3 * (double) instances[j][1];
                }
            }
            System.out.printf("%d not a match\n", non_match);
        }
        System.out.printf("final weights\n[%f, %f]\n", weights[0], weights[1]);
        //USE THE PERCEPTRON
        //runGameTimed(new Perceptron(weights), ghostController, true);
    }


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
     * @param temp        Initial temperature used to compute probability of accepting worse moves.
     *                    Recommended range: [10000, 100000]
     * @param coolingRate Rate at which temperature decreases, percentage wise.
     *                    Recommended range: [0.0001, 0.005]
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

}


