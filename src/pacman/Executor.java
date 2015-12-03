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
import java.util.PriorityQueue;
import pacman.controllers.Controller;
import pacman.controllers.informed.Astar;
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
public class Executor
{	
	/**
	 * The main method. Several options are listed - simply remove comments to use the option you want.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		Executor exec=new Executor();

		/*
		//run multiple games in batch mode - good for testing.
		int numTrials=10;
		exec.runExperiment(new RandomPacMan(),new RandomGhosts(),numTrials);
		 */
		
		/*
		//run a game in synchronous mode: game waits until controllers respond.
		int delay=5;
		boolean visual=true;
		exec.runGame(new RandomPacMan(),new RandomGhosts(),visual,delay);
  		 */
		
		///*
		//run the game in asynchronous mode.
		boolean visual=true;
		int numGen = 5;
		int popSize = 10;
		double[] maxes = new double[8];
		double[] avgs = new double[8];
		for(int i = 1; i < 5; i ++){
			double[] ret = exec.evolutionaryStrategy(numGen, popSize, (i * -250 ), (i * 250));
			maxes[i-1] = ret[0];
			avgs[i-1] = ret[1];
		}
		for(int i = 1; i < 5; i ++){
			double[] ret = exec.evolutionaryStrategy(numGen, popSize, (i * -25), (i * 25));
			maxes[i - 1 + 4] = ret[0];
			avgs [i - 1 + 4] = ret[1];
		}

		for(int i = 0; i < 5; i++){
			System.out.println("Range:[" + (i*-250) + "," + (i*250) + "] Max: " + maxes[i] + " Avg: " + avgs[i]);
		}
		for(int i = 0; i < 5; i++){
			System.out.println("Range:[" + (i*-25) + "," + (i*25) + "] Max: " + maxes[i-1+4] + " Avg: " + avgs[i-1+4]);
		}

		//exec.evolutionaryStrategy(5,10,5,visual);
//		exec.runGameTimed(new NearestPillPacMan(),new AggressiveGhosts(),visual);
	//	exec.runGameTimed(new DFSPacMan(new StarterGhosts()),new StarterGhosts(),visual);
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
     * @param ghostController The Ghosts controller
     * @param trials The number of trials to be executed
     */
    public void runExperiment(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,int trials)
    {
    	double avgScore=0;
    	
    	Random rnd=new Random(0);
		Game game;
		
		for(int i=0;i<trials;i++)
		{
			game=new Game(rnd.nextLong());
			
			while(!game.gameOver())
			{
		        game.advanceGame(pacManController.getMove(game.copy(),System.currentTimeMillis()+DELAY),
		        		ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
			}
			
			avgScore+=game.getScore();
			System.out.println(i+"\t"+game.getScore());
		}
		
		System.out.println(avgScore/trials);
    }
	
	/**
	 * Run a game in asynchronous mode: the game waits until a move is returned. In order to slow thing down in case
	 * the controllers return very quickly, a time limit can be used. If fasted gameplay is required, this delay
	 * should be put as 0.
	 *
	 * @param pacManController The Pac-Man controller
	 * @param ghostController The Ghosts controller
	 * @param visual Indicates whether or not to use visuals
	 * @param delay The delay between time-steps
	 */
	public void runGame(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean visual,int delay)
	{
		Game game=new Game(0);

		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		while(!game.gameOver())
		{
	        game.advanceGame(pacManController.getMove(game.copy(),-1),ghostController.getMove(game.copy(),-1));
	        
	        try{Thread.sleep(delay);}catch(Exception e){}
	        
	        if(visual)
	        	gv.repaint();
		}
	}
	
	/**
     * Run the game with time limit (asynchronous mode). This is how it will be done in the competition. 
     * Can be played with and without visual display of game states.
     *
     * @param pacManController The Pac-Man controller
     * @param ghostController The Ghosts controller
	 * @param visual Indicates whether or not to use visuals
     */
    public void runGameTimed(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean visual)
	{
		Game game=new Game(0);
		
		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		if(pacManController instanceof HumanController)
			gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
				
		new Thread(pacManController).start();
		new Thread(ghostController).start();
		
		while(!game.gameOver())
		{
			pacManController.update(game.copy(),System.currentTimeMillis()+DELAY);
			ghostController.update(game.copy(),System.currentTimeMillis()+DELAY);

			try
			{
				Thread.sleep(DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

	        game.advanceGame(pacManController.getMove(),ghostController.getMove());	   
	        
	        if(visual)
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
     * @param ghostController The Ghosts controller
     * @param fixedTime Whether or not to wait until 40ms are up even if both controllers already responded
	 * @param visual Indicates whether or not to use visuals
     */
    public void runGameTimedSpeedOptimised(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean fixedTime,boolean visual)
 	{
 		Game game=new Game(0);
 		
 		GameView gv=null;
 		
 		if(visual)
 			gv=new GameView(game).showGame();
 		
 		if(pacManController instanceof HumanController)
 			gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
 				
 		new Thread(pacManController).start();
 		new Thread(ghostController).start();
 		
 		while(!game.gameOver())
 		{
 			pacManController.update(game.copy(),System.currentTimeMillis()+DELAY);
 			ghostController.update(game.copy(),System.currentTimeMillis()+DELAY);

 			try
			{
				int waited=DELAY/INTERVAL_WAIT;
				
				for(int j=0;j<DELAY/INTERVAL_WAIT;j++)
				{
					Thread.sleep(INTERVAL_WAIT);
					
					if(pacManController.hasComputed() && ghostController.hasComputed())
					{
						waited=j;
						break;
					}
				}
				
				if(fixedTime)
					Thread.sleep(((DELAY/INTERVAL_WAIT)-waited)*INTERVAL_WAIT);
				
				game.advanceGame(pacManController.getMove(),ghostController.getMove());	
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
 	        
 	        if(visual)
 	        	gv.repaint();
 		}
 		
 		pacManController.terminate();
 		ghostController.terminate();
 	}
    
	/**
	 * Run a game in asynchronous mode and recorded.
	 *
     * @param pacManController The Pac-Man controller
     * @param ghostController The Ghosts controller
     * @param visual Whether to run the game with visuals
	 * @param fileName The file name of the file that saves the replay
	 */
	public void runGameTimedRecorded(Controller<MOVE> pacManController,Controller<EnumMap<GHOST,MOVE>> ghostController,boolean visual,String fileName)
	{
		StringBuilder replay=new StringBuilder();
		
		Game game=new Game(0);
		
		GameView gv=null;
		
		if(visual)
		{
			gv=new GameView(game).showGame();
			
			if(pacManController instanceof HumanController)
				gv.getFrame().addKeyListener(((HumanController)pacManController).getKeyboardInput());
		}		
		
		new Thread(pacManController).start();
		new Thread(ghostController).start();
		
		while(!game.gameOver())
		{
			pacManController.update(game.copy(),System.currentTimeMillis()+DELAY);
			ghostController.update(game.copy(),System.currentTimeMillis()+DELAY);

			try
			{
				Thread.sleep(DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

	        game.advanceGame(pacManController.getMove(),ghostController.getMove());	        
	        
	        if(visual)
	        	gv.repaint();
	        
	        replay.append(game.getGameState()+"\n");
		}
		
		pacManController.terminate();
		ghostController.terminate();
		
		saveToFile(replay.toString(),fileName,false);
	}
	
	/**
	 * Replay a previously saved game.
	 *
	 * @param fileName The file name of the game to be played
	 * @param visual Indicates whether or not to use visuals
	 */
	public void replayGame(String fileName,boolean visual)
	{
		ArrayList<String> timeSteps=loadReplay(fileName);
		
		Game game=new Game(0);
		
		GameView gv=null;
		
		if(visual)
			gv=new GameView(game).showGame();
		
		for(int j=0;j<timeSteps.size();j++)
		{			
			game.setGameState(timeSteps.get(j));

			try
			{
				Thread.sleep(DELAY);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
	        if(visual)
	        	gv.repaint();
		}
	}
	
	//save file for replays
    public static void saveToFile(String data,String name,boolean append)
    {
        try 
        {
            FileOutputStream outS=new FileOutputStream(name,append);
            PrintWriter pw=new PrintWriter(outS);

            pw.println(data);
            pw.flush();
            outS.close();

        } 
        catch (IOException e)
        {
            System.out.println("Could not save data!");	
        }
    }  

    //load a replay
    private static ArrayList<String> loadReplay(String fileName)
	{
    	ArrayList<String> replay=new ArrayList<String>();
		
        try
        {         	
        	BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));	 
            String input=br.readLine();		
            
            while(input!=null)
            {
            	if(!input.equals(""))
            		replay.add(input);

            	input=br.readLine();	
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        
        return replay;
	}

    private double[] evolutionaryComputation(int generations, int numGames, int min, int max, int genOrEvol){
        int rnmin = min;
        int rnmax = max;
        Game game = new Game(0);
		Random rn = new Random();
		int[] genMaxScore = new int[generations];
		double[] genAvgScore = new double[generations];



        Executor exec = new Executor();
        PriorityQueue<EvolObj> population = new PriorityQueue<>();
		ArrayList<EvolObj> popToMutate = new ArrayList<>();
		// Large initial population to add some variance and then smaller mutations
        for(int i = 0; i < 100; i++){
            EvolObj temp = new EvolObj(game.copy(),initialSeed(1000, -1000));
            temp.setScore(0);
            population.add(temp);
        }

        for(int j = 0; j < generations; j++){ // number of generations
			int maxScore = 0;
			double avgScore = 0;
			System.out.println("********Generation*******: " + j);
			int i = 0;
            for(EvolObj candidate: population){ // for each member of
				System.out.println("Candidate number: " + i);
				candidate.setGame(game.copy());
				Game curGame = candidate.getGame();
				int[] curWeights = candidate.getWeights();
                Astar tempController = new Astar(new StarterGhosts(), curWeights, curGame);
                while(!curGame.gameOver())
                {
                    curGame.advanceGame(tempController.getMove(curGame.copy(), -1), new StarterGhosts().getMove(game.copy(), -1));

                    //try{Thread.sleep(5);}catch(Exception e){}

                }
				candidate.setScore(curGame.getScore());
				avgScore += candidate.getScore();
				maxScore = (maxScore < candidate.getScore()) ? candidate.getScore() : maxScore;
				i++;
				System.out.println(candidate.getScore());
            }
			genMaxScore[j] = maxScore;
			genAvgScore[j] = avgScore/numGames;

			// choose the 10 fittest based off score
			for(int k = 0; k < numGames/2; k++){
				popToMutate.add(population.remove());
			}
			population.clear();
			for(int k = 0; k < numGames/2; k++){
				population.add(popToMutate.get(k));
			}

			for(int k = 0; k < numGames/2; k++){
				int indexOne = rn.nextInt(numGames/2);
				// evolutionary strategy
				if(genOrEvol == 0){
					EvolObj mutatee = popToMutate.get(indexOne);
					mutate(mutatee, rnmax, rnmin);
					population.add(mutatee);
				}
				//genetic programming
				if(genOrEvol == 1){
					int indexTwo = rn.nextInt(numGames/2);
					while(indexTwo == indexOne){
						indexTwo = rn.nextInt(numGames/2);
					}
					EvolObj p1 = popToMutate.get(indexOne);
					EvolObj p2 = popToMutate.get(indexTwo);
					EvolObj child = new EvolObj(game.copy(), reproduce(p1,p2));

				}

			}

        }
		double allGenMax = 0;
		double allGenAvg = 0;
		for(int i = 0; i < generations; i++){
			System.out.println("Generation " + i + " max score: " + genMaxScore[i] + " avg score: " + genAvgScore[i]);
			allGenMax = (allGenMax < genMaxScore[i]) ? genMaxScore[i] : allGenMax;
			allGenAvg += genAvgScore[i];
		}
		allGenAvg = allGenAvg/generations;

		double[] retVals = new double[]{allGenMax, allGenAvg};
		return retVals;

    }

	private double[] simmulatedAnnealing(int generations, int numGames, int min, int max){
		int rnmin = min;
		int rnmax = max;
		Game game = new Game(0);
		Random rn = new Random();
		int[] genMaxScore = new int[generations];
		double[] genAvgScore = new double[generations];



		Executor exec = new Executor();
		PriorityQueue<EvolObj> population = new PriorityQueue<>();
		ArrayList<EvolObj> popToMutate = new ArrayList<>();
		// Large initial population to add some variance and then smaller mutations
		for(int i = 0; i < 100; i++){
			EvolObj temp = new EvolObj(game.copy(),initialSeed(1000, -1000));
			temp.setScore(0);
			population.add(temp);
		}

		for(int j = 0; j < generations; j++){ // number of generations
			int maxScore = 0;
			double avgScore = 0;
			System.out.println("********Generation*******: " + j);
			int i = 0;
			for(EvolObj candidate: population){ // for each member of
				System.out.println("Candidate number: " + i);
				candidate.setGame(game.copy());
				Game curGame = candidate.getGame();
				int[] curWeights = candidate.getWeights();
				Astar tempController = new Astar(new StarterGhosts(), curWeights, curGame);
				while(!curGame.gameOver())
				{
					curGame.advanceGame(tempController.getMove(curGame.copy(), -1), new StarterGhosts().getMove(game.copy(), -1));

					//try{Thread.sleep(5);}catch(Exception e){}

				}
				candidate.setScore(curGame.getScore());
				avgScore += candidate.getScore();
				maxScore = (maxScore < candidate.getScore()) ? candidate.getScore() : maxScore;
				i++;
				System.out.println(candidate.getScore());
			}
			genMaxScore[j] = maxScore;
			genAvgScore[j] = (j == 0) ? avgScore/100 : avgScore/numGames;

			// choose the 10 fittest based off score
			for(int k = 0; k < numGames/2; k++){
				popToMutate.add(population.remove());
			}
			population.clear();
			for(int k = 0; k < numGames/2; k++){
				population.add(popToMutate.get(k));
			}
			for(int k = 0; k < numGames/2; k++){
				int index = rn.nextInt(numGames/2);
				EvolObj mutatee = popToMutate.get(index);
				mutate(mutatee, rnmax, rnmin);
				population.add(mutatee);
			}

		}
		double allGenMax = 0;
		double allGenAvg = 0;
		for(int i = 0; i < generations; i++){
			System.out.println("Generation " + i + " max score: " + genMaxScore[i] + " avg score: " + genAvgScore[i]);
			allGenMax = (allGenMax < genMaxScore[i]) ? genMaxScore[i] : allGenMax;
			allGenAvg += genAvgScore[i];
		}
		allGenAvg = allGenAvg/generations;

		double[] retVals = new double[]{allGenMax, allGenAvg};
		return retVals;

	}

    public void mutate(EvolObj state, int rnmax, int rnmin){
        Random rn = new Random();
        int[] newWeights = new int[8];
        int[] oldWeights = state.getWeights();
        for(int i = 0; i < 8; i++){
            newWeights[i] = oldWeights[i] + (rn.nextInt(rnmax - rnmin + 1) + rnmin);
        }
        state.setWeights(newWeights);

    }

    public int[] reproduce(EvolObj p1, EvolObj p2) {
        int[] p1Weights = p1.getWeights();
        int[] p2Weights = p2.getWeights();
        int[] weights = new int[8];
        for(int i = 0; i < 8; i++){
            weights[i] = (p1Weights[i] + p2Weights[i])/2;
        }
        return weights;
    }

    public int[] initialSeed(int rnmax, int rnmin){
        Random rn = new Random();
        int[] weights = new int[8];
        for(int i = 0; i < 8; i++){
            weights[i] = rn.nextInt(rnmax - rnmin + 1) + rnmin;
        }
        return weights;
    }

    class EvolObj implements Comparable<EvolObj>{
        private Game game;
        private int score;
        private int[] weights;
        private MOVE move;

        public EvolObj(Game game, int[] weights){
            this.game = game;
            this.weights = weights;
        }

        public void setGame(Game game){
            this.game = game;
        }

        public void setWeights(int[] weights){
            this.weights = weights;
        }
        public void setScore(int score){
            this.score = score;
        }

        public void setMove(MOVE move){
            this.move = move;
        }

        public MOVE getMove(){
            return move;
        }

        public int getScore(){
            return score;
        }

        public int[] getWeights(){
            return weights;
        }

        public Game getGame(){
            return game;
        }

        public int compareTo(EvolObj other) {
            return Integer.compare(this.score, other.getScore());
        }
    }
}
