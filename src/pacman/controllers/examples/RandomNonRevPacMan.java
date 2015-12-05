package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.Random;

import static pacman.game.Constants.MOVE;

/*
 * The Class RandomNonRevPacMan.
 */
public final class RandomNonRevPacMan extends Controller<MOVE> {
    Random rnd = new Random();

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {
        MOVE[] possibleMoves = game.getPossibleMoves(game.getPacmanCurrentNodeIndex(), game.getPacmanLastMoveMade());        //set flag as false to prevent reversals

        return possibleMoves[rnd.nextInt(possibleMoves.length)];
    }
}