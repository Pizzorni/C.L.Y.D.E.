package pacman.controllers.informed;


import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.EnumMap;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * This controller implements a very simple Hill Climber. He evaluates every neighbor (move) around him
 * and picks the best one. Returns the move that is locally best. Very short sighted.
 */

public class HillClimber extends Controller<MOVE> {
    Controller<EnumMap<GHOST, MOVE>> spookies;


    /**
     * Hill Climber constructor.
     *
     * @param spookies The ghost controller to be played, and therefore simulated, against.
     */
    public HillClimber(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies; // SPOOKIES
    }

    /**
     * The Hill Climber will look at every move he can make this given time and pick the best one. Best one
     * is defined as the move that provides the highest score, and does not lead to game over. This simple
     * heuristic will make it hunt down pills and slightly avoid ghosts. Gets stuck in corners when no pills
     * nearby.
     *
     * @param game game to simulate on
     * @return Returns the move that gets it the highest score in its immediate vicinity
     */
    public MOVE getMove(Game game, long timeDue) {
        Game[] games = new Game[5];
        MOVE[] moves = new MOVE[5];
        int moveIter = 0;
        for (MOVE move : MOVE.values()) {
            moves[moveIter] = move;
            moveIter++;
        }
        for (int i = 0; i < 5; i++) {
            games[i] = game.copy();
        }
        int maxScore = Integer.MIN_VALUE;
        int maxIndex = 0;
        int tempScore;

        // Simulate all moves in current state
        for (int i = 0; i < 5; i++) {
            // Needs some extra time to properly compute all moves
            for (int j = 0; j < 8; j++) {
                games[i].advanceGame(moves[i], spookies.getMove(games[i].copy(), -1));
            }
            tempScore = games[i].getScore();
            // Find max score and which move was responsible for it
            if (maxScore < tempScore) {
                maxScore = tempScore;
                maxIndex = i;
            }
        }
        // return move that got highest score
        return moves[maxIndex];

    }
}


