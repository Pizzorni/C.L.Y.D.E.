package pacman;

import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.informed.Astar;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.*;

/**
 * This class run six different evolutionary algorithms concurrently to find out who will reign supreme. Currently
 * supports a Mutation algorithm, a Crossover algorithm, and a Mutation and Crossover algorithm, all with and without
 * simulated annealing
 */


public class CambrianExplosion {
    static final int MUTATION = 0;
    static final int CROSSOVER = 1;
    static final int COMBINATION = 2;
    static final int RNMAX = 50;
    static final int RNMIN = -50;
    private int NUM_GEN;
    private int POP_SIZE;
    private int INIT_XFACTOR; // used to add some oomp to the initial population optionally
    private int INIT_POP_SIZE;
    private double TEMPERATURE;
    private double REV_TEMPERATURE;
    private double DELTA_TEMP;
    private PriorityQueue<Organism> mutatePop;
    private PriorityQueue<Organism> crossoverPop;
    private PriorityQueue<Organism> combinationPop;
    private PriorityQueue<Organism> mutatePopSim;
    private PriorityQueue<Organism> crossoverPopSim;
    private PriorityQueue<Organism> combinationPopSim;


    public CambrianExplosion(int numGen, int popSize, int xfactor) {
        this.NUM_GEN = numGen;
        this.POP_SIZE = popSize;
        this.INIT_XFACTOR = xfactor;
        this.TEMPERATURE = 30;
        this.DELTA_TEMP = 0.005;
        this.REV_TEMPERATURE = 5;
        this.INIT_POP_SIZE = INIT_XFACTOR * POP_SIZE;

        mutatePop = new PriorityQueue<>();
        crossoverPop = new PriorityQueue<>();
        combinationPop = new PriorityQueue<>();
        mutatePopSim = new PriorityQueue<>();
        crossoverPopSim = new PriorityQueue<>();
        combinationPopSim = new PriorityQueue<>();
    }

    public void explode() {
        // Initialize populations
        ArrayList<Organism> initialPopulation = new ArrayList<>();

        int initPopSize = POP_SIZE * INIT_XFACTOR;

        Game game = new Game(0);

        // Seed initial population
        for (int i = 0; i < initPopSize; i++) {
            Organism temp = new Organism(game.copy(), initialSeed(250, -250), 0);
            initialPopulation.add(temp);
        }

        // do a deep copy of population to generate the populations we will use
        for (Organism orgo : initialPopulation) {
            mutatePop.add(new Organism(orgo));
            crossoverPop.add(new Organism(orgo));
            combinationPop.add(new Organism(orgo));
            mutatePopSim.add(new Organism(orgo));
            crossoverPopSim.add(new Organism(orgo));
            combinationPopSim.add(new Organism(orgo));
        }

        ArrayList<Future> threads = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(6);
        // Spawn threads to make things fast
        EvolThread mutateThread = new EvolThread(mutatePop, MUTATION, RNMAX, RNMIN, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, 0, 0);
        EvolThread crossoverThread = new EvolThread(crossoverPop, CROSSOVER, RNMAX, RNMIN, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, 0, 0);
        EvolThread combinationThread = new EvolThread(combinationPop, COMBINATION, RNMAX, RNMIN, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, 0, 0);
        EvolThread mutateThreadSim = new EvolThread(mutatePopSim, MUTATION, RNMAX, RNMIN, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, TEMPERATURE, DELTA_TEMP);
        EvolThread crossoverThreadSim = new EvolThread(crossoverPopSim, CROSSOVER, RNMAX, RNMIN, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, TEMPERATURE, DELTA_TEMP);
        EvolThread combinationThreadSim = new EvolThread(combinationPopSim, COMBINATION, RNMAX, RNMIN, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, TEMPERATURE, DELTA_TEMP);

        threads.add(executor.submit(mutateThread));
        threads.add(executor.submit(crossoverThread));
        threads.add(executor.submit(combinationThread));
        threads.add(executor.submit(mutateThreadSim));
        threads.add(executor.submit(crossoverThreadSim));
        threads.add(executor.submit(combinationThreadSim));

        executor.shutdown();

        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (java.lang.InterruptedException e) {
        }

        ArrayList<EvolResult> results = new ArrayList<>();
        for (Future f : threads) {
            try {
                results.add((EvolResult) f.get());
            } catch (java.util.concurrent.ExecutionException e) {
            } catch (java.lang.InterruptedException e) {
            }
        }

        String out = "";

        for (EvolResult r : results) {
            String weights = "[ ";
            for (int weight : r.getBestWeights()) {
                weights += weight + " ";
            }
            weights += "]";
            out = String.format("%-17s Max: %-4d Avg: %-6.2f Weights: %-20s ", r.getAlgotype(), r.getMaxScore(),
                    r.getAvgScore(), weights);

            System.out.println(out);

        }

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
     * Mutation function. Generates random numbers in the range [rnmin, rnmax] and adds them to
     * to the weight vector of the current Organism. Predominantly used for Evolution Strategy.
     *
     * @param orgo  The evolutionary object that will be mutated to produce a new member of the pop
     * @param rnmax Maximum random value to be used in mutation step. Fixed unless using simulated annealing.
     * @param rnmin Minimum random value to be used in mutation step. Fixed unless using simulated annealing.
     * @return Returns new mutated weights generated from current weights
     */

    public int[] mutate(Organism orgo, int rnmax, int rnmin) {
        Random rn = new Random();
        int[] newWeights = new int[8];
        int[] oldWeights = orgo.getWeights();
        for (int i = 0; i < 8; i++) {
            newWeights[i] = oldWeights[i] + (rn.nextInt(rnmax - rnmin + 1) + rnmin);
        }
        return newWeights;

    }

    /**
     * Crossover/reproduction function. Generates new weights by averaging the weights of two parents chosen
     * from the fit population with a small random weight added to each parent's genotypes.
     *
     * @param p1 Parent one
     * @param p2 Parent two
     * @return Returns new weights generated from crossover of two parents
     */
    public int[] reproduce(Organism p1, Organism p2, int rnmax, int rnmin) {
        int[] p1Weights = p1.getWeights();
        int[] p2Weights = p2.getWeights();
        int[] newWeights = new int[8];
        int[] p1Seed = new int[8];
        int[] p2Seed = new int[8];
        int coinFlip;
        Random rn = new Random();
        for (int i = 0; i < 8; i++) {
            coinFlip = rn.nextInt(2);
            if (coinFlip == 1) {
                p1Seed[i] = (rn.nextInt(rnmax - rnmin + 1) + rnmin) % rnmax;
            }
            else{
                p1Seed[i] = 1;
            }
            coinFlip = rn.nextInt();
            if (coinFlip == 1) {
                p2Seed[i] = (rn.nextInt(rnmax - rnmin + 1) + rnmin) % rnmax;
            }
            else{
                p2Seed[i] = 1;
            }
            newWeights[i] = (((p1Weights[i] + p1Seed[i])) + ((p2Weights[i] - p2Seed[i])) / 2);
        }
        return newWeights;
    }


    class EvolThread implements Callable {

        private PriorityQueue<Organism> population;
        private int numGen;
        private int popSize;
        private int initPopSize;
        private double temp;
        private double deltaTemp;
        private int algoType;
        private int rnmax;
        private int rnmin;


        public EvolThread(PriorityQueue<Organism> pop, int algoType, int max, int min, int numGen, int popSize,
                          int initPopSize, double temp, double deltaTemp) {

            this.population = pop;
            this.algoType = algoType;
            this.rnmax = max;
            this.rnmin = min;
            this.numGen = numGen;
            this.popSize = popSize;
            this.initPopSize = initPopSize;
            this.temp = temp;
            this.deltaTemp = deltaTemp;
        }

        public EvolResult call() {
            PriorityQueue<Organism> sortedPopulation = new PriorityQueue<>();
            ArrayList<Organism> selectedPopulation = new ArrayList<>();
            Executor exec = new Executor();
            Game game = new Game(0);
            Game curGame;
            Astar astar;
            int[] genMaxScores = new int[numGen];
            double[] genAvgScores = new double[numGen];
            StarterGhosts spookies = new StarterGhosts();
            int[] curWeights;
            int curScore;
            int maxScore = Integer.MIN_VALUE;
            double avgScore = 0;
            int tempPopSize;
            int randIndex;
            int randIndexTwo;
            int[] bestWeights;
            String algoName = "";
            String rev = "";
            int bestHalf = popSize / 2;
            int bestQuarter = popSize / 2;
            Random rn = new Random();
            Random rn2 = new Random();
            int tempmax = rnmax;
            int tempmin = rnmin;


            for (int genIter = 0; genIter < numGen; genIter++) {
                sortedPopulation.clear();
                selectedPopulation.clear();
                if(temp > 0){
                    temp *= (1 - deltaTemp);
                }
                for (Organism orgo : population) {
                    curGame = game.copy();
                    orgo.setGame(curGame);
                    curWeights = orgo.getWeights();
                    astar = new Astar(new StarterGhosts(), curWeights);
                    while (!curGame.gameOver()) {
                        curGame.advanceGame(astar.getMove(curGame.copy(), -1), spookies.getMove(curGame.copy(), -1));
                    }
                    curScore = curGame.getScore();
                    orgo.setFitness(curScore);
                    avgScore += curGame.getScore();
                    if (maxScore < curScore) {
                        maxScore = curScore;
                    }
                    sortedPopulation.add(orgo);
                }
                genMaxScores[genIter] = maxScore;
                if (genIter == 0) {
                    avgScore = avgScore / initPopSize;
                } else {
                    avgScore = avgScore / popSize;
                }
                genAvgScores[genIter] = avgScore;

                population.clear();

                if (algoType == MUTATION) {
                    // Pick the top 50% to survive
                    tempPopSize = 0;
                    for (int i = 0; i < popSize / 2; i++) {
                        Organism temp = sortedPopulation.remove();
                        population.add(new Organism(temp));
                        selectedPopulation.add(new Organism(temp));
                        tempPopSize++;
                    }
                    // while the population hasn't been replenished, pick from the surviving population at random
                    // and mutate
                    while (tempPopSize < popSize) {
                        randIndex = rn.nextInt(popSize / 2);
                        Organism mutatee = new Organism(selectedPopulation.get(randIndex));
                        // A little pseudo simulated annealing. The more we mutate, the more/less effective it
                        // will be depending on whether temperature is increasing or decreasing.
                        if (temp > 0) {
                            tempmax = rnmax + rn.nextInt((int) temp);
                            tempmin = rnmin - rn.nextInt((int) temp);
                        }
                        int[] mutatedWeights = mutate(mutatee, tempmax, tempmin);
                        mutatee.setWeights(mutatedWeights);
                        mutatee.setFitness(0);
                        population.add(mutatee);
                        tempPopSize++;
                    }
                }

                if (algoType == CROSSOVER) {
                    // Pick the top 25% to survive
                    tempPopSize = 0;
                    for (int i = 0; i < popSize / 4; i++) {
                        Organism temp = sortedPopulation.remove();
                        population.add(new Organism(temp));
                        tempPopSize++;
                    }
                    // From the remaining 75% of the less fit population, we will draw an additional 25%
                    // in order to preserve genetic diversity
                    while (!sortedPopulation.isEmpty()) {
                        Organism temp = sortedPopulation.remove();
                        selectedPopulation.add(temp);
                    }

                    for (int i = 0; i < popSize / 4; i++) {
                        randIndex = rn.nextInt(selectedPopulation.size());
                        Organism temp = new Organism(selectedPopulation.get(randIndex));
                        population.add(new Organism(temp));
                        tempPopSize++;
                    }
                    // create array list so we can index in randomly
                    selectedPopulation.clear();
                    for (Organism orgo : population) {
                        selectedPopulation.add(new Organism(orgo));
                    }

                    while (tempPopSize < popSize) {
                        randIndex = rn.nextInt(selectedPopulation.size());
                        randIndexTwo = rn.nextInt(selectedPopulation.size());
                        // ensure we are choosing two different parents
                        // asexual reproduction does not make sense given our crossover function
                        while (randIndexTwo == randIndex) {
                            randIndexTwo = rn.nextInt(selectedPopulation.size());
                        }
                        Organism parentOne = selectedPopulation.get(randIndex);
                        Organism parentTwo = selectedPopulation.get(randIndexTwo);
                        // A little pseudo smmulated annealing. The more we reproduce, the more/less random it
                        // will be depending on whether temperature is increasing or decreasing.
                        if (temp > 0) {
                            tempmax = rnmax + rn.nextInt((int) temp);
                            tempmin = rnmin - rn.nextInt((int) temp);
                        }
                        int[] reproducedWeights = reproduce(parentOne, parentTwo, tempmax, tempmin);
                        Organism child = new Organism(game.copy(), reproducedWeights, 0);
                        population.add(child);
                        tempPopSize++;
                    }

                }

                if (algoType == COMBINATION) {
                    // Strategy: allow top 50% to survive. From these survivors, mutate to produce another 25%
                    // and reproduce randomly to produce another 25%

                    // Pick the top 50%
                    tempPopSize = 0;
                    for (int i = 0; i < popSize / 2; i++) {
                        Organism temp = sortedPopulation.remove();
                        population.add(new Organism(temp));
                        selectedPopulation.add(new Organism(temp));
                        tempPopSize++;
                    }
                    // Mutate randomly from the chosen 50% to produce popSize/4 more candidates
                    for (int i = 0; i < popSize / 4; i++) {
                        randIndex = rn.nextInt(popSize / 2);
                        // A little pseudo simulated annealing. The more we mutate, the more/less effective it
                        // will be depending on whether temperature is increasing or decreasing.
                        Organism mutatee = new Organism(selectedPopulation.get(randIndex));
                        if (temp > 0) {
                            tempmax = rnmax + rn.nextInt((int) temp);
                            tempmin = rnmin - rn.nextInt((int) temp);
                        }
                        int[] mutatedWeights = mutate(mutatee, tempmax, tempmin);
                        mutatee.setWeights(mutatedWeights);
                        mutatee.setFitness(0);
                        population.add(mutatee);
                        tempPopSize++;
                    }

                    // Now, until population replenished, reproduce from chosen 50% randomly
                    // Approximately popSize/4 should be generated, depending on parity of popSize.
                    while (tempPopSize < popSize) {
                        randIndex = rn.nextInt(selectedPopulation.size());
                        randIndexTwo = rn.nextInt(selectedPopulation.size());
                        // ensure we are choosing two different parents
                        // asexual reproduction does not make sense given our crossover function
                        while (randIndexTwo == randIndex) {
                            randIndexTwo = rn.nextInt(selectedPopulation.size());
                        }
                        Organism parentOne = selectedPopulation.get(randIndex);
                        Organism parentTwo = selectedPopulation.get(randIndexTwo);
                        // A little pseudo smmulated annealing. The more we reproduce, the more/less random it
                        // will be depending on whether temperature is increasing or decreasing.
                        if (temp > 0) {
                            tempmax = rnmax + rn.nextInt((int) temp);
                            tempmin = rnmin -  rn.nextInt((int) temp);
                        }
                        int[] reproducedWeights = reproduce(parentOne, parentTwo, tempmax, tempmin);
                        Organism child = new Organism(game.copy(), reproducedWeights, 0);
                        population.add(child);
                        tempPopSize++;
                    }
                }

                switch (algoType) {
                    case MUTATION:
                        algoName = "Mutation";
                        break;
                    case CROSSOVER:
                        algoName = "Crossover";
                        break;
                    case COMBINATION:
                        algoName = "Combination";
                        break;
                }

//                rev = (temp > 0) ? " SIM" : "";
//
//
//                System.out.println(algoName + rev + " --- Max: " + genMaxScores[genIter] + " Avg: "
//                        + genAvgScores[genIter]);
            }

            // Generations done, return some statistics in a wrapper obj
            int maxAllGen = 0;
            double avgAllGen = 0;
            for (int i = 0; i < numGen; i++) {
                maxAllGen = (maxAllGen < genMaxScores[i]) ? genMaxScores[i] : maxAllGen;
                avgAllGen += genAvgScores[i];
            }
            avgAllGen = avgAllGen / numGen;
            Organism best = population.remove();

            EvolResult ret = new EvolResult(algoName, best.getWeights(), maxAllGen, avgAllGen, temp);
            return ret;

        }

    }

    class EvolResult {
        public EvolResult(String algoName, int[] bestWeights, int maxScore, double avgScore, double temp) {
            this.algoName = algoName;
            this.bestWeights = bestWeights;
            this.maxScore = maxScore;
            this.avgScore = avgScore;
            this.temp = temp;
            if (temp > 0) {
                this.rev = " SIM";
            } else {
                this.rev = "";
            }
            this.algoType = this.algoName + this.rev;
        }

        public String getAlgotype() {
            return algoType;
        }

        public String getAlgoName() {
            return algoName;
        }

        public void setAlgoName(String algoNype) {
            this.algoName = algoName;
        }

        public int[] getBestWeights() {
            return bestWeights;
        }

        public void setBestWeights(int[] bestWeights) {
            this.bestWeights = bestWeights;
        }

        public int getMaxScore() {
            return maxScore;
        }

        public void setMaxScore(int maxScore) {
            this.maxScore = maxScore;
        }

        public double getAvgScore() {
            return avgScore;
        }

        public void setAvgScore(double avgScore) {
            this.avgScore = avgScore;
        }

        private String algoName;
        private int[] bestWeights;
        private int maxScore;
        private double avgScore;
        private double temp;
        private String rev;
        private String algoType;
    }
}



