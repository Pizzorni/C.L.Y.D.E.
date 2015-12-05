package pacman.controllers.uninformed;

import pacman.controllers.Controller;
import pacman.game.Game;
import java.util.EnumMap;
import static pacman.game.Constants.*;

/**
 * Implements a controller using iterative deepening
 */
public class IterDeep extends Controller<MOVE> {
    private static int SCORE = 40;
    private static int LEVELS = 2;
    private static MOVE[] moves;
    private boolean found;
    private int found_level;
    private int found_game_num;
    private Game[] games;
    private int game_num;
    private int last_score;

    Controller<EnumMap<GHOST, MOVE>> spookies;
    public IterDeep(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }

    /**
     * Evaluates current game state, then uses perceptron weights to decide what to do
     * @param game A copy of the current game
     * @param timeDue The time the next move is due
     * @return move decision
     */
    public MOVE getMove(Game game,long timeDue) {

        //store moves in a move array
        moves = MOVE.values();
        int i = 0;
        for (MOVE move : MOVE.values()){
            moves[i] = move;
            i++;
        }

        found = false;
        int max_iter = 5;
        int iter_count= 0;

        //create an array of the max number of games possibly needed
        games = new Game[(int)Math.pow(5.0, LEVELS*max_iter)];
        games[0] = game;
        last_score = game.getScore();

        //iterate through games in the game array and advance them with each of the 5 moves
        // call iterative deepening on each
        int j;
        while(!found && iter_count < max_iter) {
            for (j = 0; j <(int)Math.pow(5.0, LEVELS*iter_count); j++){
                for (i = 0; i < 5; i++) {
                    iterativeDeep(game, moves[i], 1);
                }
            }
            iter_count++;
            game_num = 0;
        }

        //calculate the best move based on the index into the game array
        int best_move;
        if (found) {
            //calculate best move
            int game_div = (int)Math.pow(5, LEVELS*iter_count+found_level-2);
            double game_thing = found_game_num/game_div;
            best_move = (int)game_thing;
        }
        else {
            //choose random move if reach max iters without finding a best move
            best_move = (int)(Math.random()*10)%5;
        }
        return moves[best_move];
    }

    /**
     * for the game passed in, advance each game by each move
     *      and check if the found condition is met or we have reached
     *      the level max
     *      In this case the found condition is that the score has
     *      advanced by SCORE
     * @param game
     * @param move
     * @param level_count
     */
    private void iterativeDeep(Game game, MOVE move, int level_count){
        if (!found) {
            advanceGame(game, move);
            int score = game.getScore();

            if (score - last_score == SCORE) {
                found = true;
                found_level = level_count;
                found_game_num = game_num;
            } else if (level_count == LEVELS) {
                games[game_num] = game;
                game_num++;
            } else {
                for (int i = 0; i < 5; i++) {
                    iterativeDeep(game, moves[i], level_count+1);
                }
            }
        }
    }

    /**
     * Advances the game 4 times using the same move
     *  4 advances moves PacMan one grid block
     * @param game
     * @param move
     */
    private void advanceGame(Game game, MOVE move){
        for (int j = 0; j < 4; j++) {
            game.advanceGame(move, spookies.getMove(game, -1));
        }
    }
}


