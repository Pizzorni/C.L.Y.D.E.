package pacman.controllers.uninformed;

import java.util.ArrayList;
import pacman.controllers.Controller;
import pacman.game.Game;

import static pacman.game.Constants.*;

/*
 */
public class DFSPacMan extends Controller<MOVE> {	
	
	Controller<EnumMap<Constants.GHOST, MOVE>> spookies;
	public DFSPacMan(Controller<EnumMap<Constants.GHOST, MOVE>> spookies){
		this.spookies = spookies;
	}
	public MOVE getMove(Game game,long timeDue) {			
		Game[] games = new Game[5];
		MOVE[] moves = MOVES.values();
		int maxScore = 0;
		int bestMove = 0;
		for (MOVE move : MOVE.values()){
			moves[i] = move;
		}
		for(int i = 0; i < 5; i++){
			games[i] = game.copy();
			games[i].advanceGame(moves[i], spookies.getMove(games[i], -1));
			if (games[i].getScore() > maxScore){
				maxScore = games[i].getScore();
				bestMove = i;
			}
		}
		return moves[i];
	}
}


