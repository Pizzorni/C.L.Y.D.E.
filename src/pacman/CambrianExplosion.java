package pacman;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.ServiceConfigurationError;
import java.util.concurrent.*;

import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.informed.Astar;
import pacman.game.Game;
import java.util.Set;

/**
 * Created by giorgio on 12/4/15.
 */


public class CambrianExplosion {
    static final int MUTATION = 0;
    static final int CROSSOVER = 1;
    static final int COMBINATION = 2;
    private int NUM_GEN;
    private int POP_SIZE;
    private int INIT_XFACTOR;
    private int INIT_POP_SIZE;
    private double TEMPERATURE;
    private double DELTA_TEMP;
    private PriorityQueue<Organism> mutatePop;
    private PriorityQueue<Organism> crossoverPop;
    private PriorityQueue<Organism> combinationPop;


    public CambrianExplosion(int numGen, int popSize, int xfactor, double temp, double deltaTemp) {
        this.NUM_GEN = numGen;
        this.POP_SIZE = popSize;
        this.INIT_XFACTOR = xfactor;
        this.TEMPERATURE = temp;
        this.DELTA_TEMP = deltaTemp;
        this.INIT_POP_SIZE = INIT_XFACTOR * POP_SIZE;

        mutatePop = new PriorityQueue<>();
        crossoverPop = new PriorityQueue<>();
        combinationPop = new PriorityQueue<>();
    }

    public void explode() {
        // Initialize populations
        ArrayList<Organism> initialPopulation = new ArrayList<>();

        int initPopSize = POP_SIZE * INIT_XFACTOR;

        Game game = new Game(0);

        // Seed initial population
        for (int i = 0; i < initPopSize; i++) {
            Organism temp = new Organism(game.copy(), initialSeed(1000, -1000), 0);
            initialPopulation.add(temp);
        }

        // do a deep copy of population to generate the populations we will use
        for (Organism orgo : initialPopulation) {
            mutatePop.add(new Organism(orgo));
            crossoverPop.add(new Organism(orgo));
            combinationPop.add(new Organism(orgo));
        }

        ArrayList<Future> threads = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        EvolThread mutateThread = new EvolThread(mutatePop, MUTATION, 50, -50, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, TEMPERATURE, DELTA_TEMP);
        EvolThread crossoverThread = new EvolThread(crossoverPop, CROSSOVER, 50, -50, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, TEMPERATURE, DELTA_TEMP);
        EvolThread combinationThread = new EvolThread(combinationPop, COMBINATION, 50, -50, NUM_GEN, POP_SIZE,
                INIT_POP_SIZE, TEMPERATURE, DELTA_TEMP);

        threads.add(executor.submit(mutateThread));
        threads.add(executor.submit(crossoverThread));
        threads.add(executor.submit(combinationThread));

        executor.shutdown();
        try{
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        }
        catch(java.lang.InterruptedException e){

        }


        ArrayList<EvolResult> results = new ArrayList<>();

        for(Future f : threads){
            try{
                results.add((EvolResult)f.get());
            }
            catch (java.util.concurrent.ExecutionException e){

            }
            catch(java.lang.InterruptedException e){

            }
        }

        String out = "";

        for(EvolResult r: results){
            String weights = "[ ";
            for(int weight : r.getBestWeights()){
                weights += weight + " ";
            }
            weights+="]";
            out = String.format("%-11s Max: %-4d Avg: %-6.2f Weights: %-20s ", r.getAlgotype(), r.getMaxScore(),
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
     * from the fit population.
     *
     * @param p1 Parent one
     * @param p2 Parent two
     * @return Returns new weights generated from crossover of two parents
     */
    public int[] reproduce(Organism p1, Organism p2) {
        int[] p1Weights = p1.getWeights();
        int[] p2Weights = p2.getWeights();
        int[] newWeights = new int[8];
        for (int i = 0; i < 8; i++) {
            newWeights[i] = (p1Weights[i] + p2Weights[i]) / 2;
        }
        return newWeights;
    }


    class EvolThread implements Callable {

        private PriorityQueue<Organism> population;
        private int numGen;
        private int popSize;
        private int initPopSize;
        private double temp;
        private double coolingRate;
        private int algoType;
        private int rnmax;
        private int rnmin;
        private double temp;
        private double deltaTemp;

        public EvolThread(PriorityQueue<Organism> pop, int algoType, int max, int min, int numGen, int popSize,
                          int initPopSize, double temp, double deltaTemp){

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

            Random rn = new Random();

            for (int genIter = 0; genIter < numGen; genIter++) {
                sortedPopulation.clear();
                selectedPopulation.clear();
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
                    avgScore += curScore;
                    if(maxScore < curScore){
                        maxScore = curScore;
                        bestWeights = orgo.getWeights();
                    }
                    sortedPopulation.add(orgo);
                }
                genMaxScores[genIter] = maxScore;
                genAvgScores[genIter] = (genIter == 0) ? avgScore / initPopSize : avgScore / popSize;

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
                        int[] mutatedWeights = mutate(mutatee, rnmax, rnmin);
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
                        int[] reproducedWeights = reproduce(parentOne, parentTwo);
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
                        Organism mutatee = new Organism(selectedPopulation.get(randIndex));
                        int[] mutatedWeights = mutate(mutatee, rnmax, rnmin);
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
                        int[] reproducedWeights = reproduce(parentOne, parentTwo);
                        Organism child = new Organism(game.copy(), reproducedWeights, 0);
                        population.add(child);
                        tempPopSize++;
                    }
                }

                switch(algoType){
                    case MUTATION:  algoName = "Mutation";
                        break;
                    case CROSSOVER: algoName = "Crossover";
                        break;
                    case COMBINATION: algoName = "Combination";
                        break;
                }

                System.out.println(algoName + " --- Max: " + genMaxScores[genIter] + " Avg: "
                        + genAvgScores[genIter]);
            }

            // Generations done, return some statistics in a wrapper obj
            int maxAllGen = 0;
            double avgAllGen = 0;
            for(int i = 0; i < numGen; i ++){
                maxAllGen = (maxAllGen < genMaxScores[i]) ? genMaxScores[i] : maxAllGen;
                avgAllGen += genAvgScores[i];
            }
            avgAllGen = avgAllGen/numGen;
            Organism best = population.remove();

            EvolResult ret = new EvolResult(algoName,best.getWeights(),maxAllGen,avgAllGen);
            return ret;

        }

    }

    class EvolResult{
        public EvolResult(String algoType, int[] bestWeights, int maxScore, double avgScore) {
            this.algoType = algoType;
            this.bestWeights = bestWeights;
            this.maxScore = maxScore;
            this.avgScore = avgScore;
        }

        public String getAlgotype() {
            return algoType;
        }

        public void setAlgotype(String algoType) {
            this.algoType = algoType;
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

        private String algoType;
        private int[] bestWeights;
        private int maxScore;
        private double avgScore;
    }
}



