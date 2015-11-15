package pacman.controllers.uninformed;

import pacman.controllers.Controller;
import pacman.game.Game;
import java.util.EnumMap;
import static pacman.game.Constants.*;

/*
 */
public class IterDeepPacMan extends Controller<MOVE> {
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
    public IterDeepPacMan(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }
    public MOVE getMove(Game game,long timeDue) {


        moves = MOVE.values();
        int i = 0;
        //store moves in a move array
        for (MOVE move : MOVE.values()){
            moves[i] = move;
            i++;
        }

        found = false;

        int max_iter = 5;
        int iter_count= 0;

        games = new Game[(int)Math.pow(5.0, LEVELS*max_iter)];
        games[0] = game;
        last_score = game.getScore();


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

        int best_move;
        if (found) {
            //System.out.println("Found");
            //calculate best move
            int game_div = (int)Math.pow(5, LEVELS*iter_count+found_level-2);
            //System.out.printf("game num: %d -- iter_count: %d -- found_level: %d -- game_div: %d\n",found_game_num, iter_count, found_level, game_div);
            double game_thing = found_game_num/game_div;
            //System.out.printf("gamething: %f\n", game_thing);
            best_move = (int)game_thing;
            //best_move = 1;
        }
        else {
            System.out.println("Not found");
            //choose random move
            best_move = (int)(Math.random()*10)%5;
        }
        //System.out.printf("choosing move %d\n", best_move);
        return moves[best_move];
    }

    private void iterativeDeep(Game game, MOVE move, int level_count){
        if (!found) {
            advanceGame(game, move);
            int score = game.getScore();

            if (score - last_score == SCORE) {
                found = true;
                //System.out.printf("FOUND: with game num:%d, score: %d, last score:%d\n", game_num, score, last_score);
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

    private void advanceGame(Game game, MOVE move){
        for (int j = 0; j < 4; j++) {
            game.advanceGame(move, spookies.getMove(game, -1));
        }
    }
}


