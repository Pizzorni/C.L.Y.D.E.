package pacman.controllers.uninformed;

import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Game;
import java.util.EnumMap;
import static pacman.game.Constants.*;

/*
 */
public class DFSPacMan extends Controller<MOVE> {	
	
	Controller<EnumMap<GHOST, MOVE>> spookies;
	public DFSPacMan(Controller<EnumMap<GHOST, MOVE>> spookies){
		this.spookies = spookies;
	}
	public MOVE getMove(Game game,long timeDue) {
		Game[] games = new Game[5];
		MOVE[] moves = MOVE.values();
		int maxScore = 0;//game.getScore();
		int bestMove = 0;
		int i = 0;
		for (MOVE move : MOVE.values()){
			moves[i] = move;
			i++;
		}
		int[] scores = new int[5];
		for(i = 0; i < 5; i++) {
			games[i] = game.copy();
			for (int j = 0; j < 4; j++) {
				games[i].advanceGame(moves[i], spookies.getMove(games[i], -1));
			}
			scores[i] = games[i].getScore();
		}

		System.out.println();
		for (int j = 0; j < 5; j++){
			if (scores[j] > maxScore){
				maxScore = scores[j];
				bestMove = j;
			}
		}
		return moves[bestMove];
	}
}


