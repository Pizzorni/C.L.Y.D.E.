package pacman.controllers.uninformed;

import pacman.controllers.Controller;
import pacman.game.Game;
import java.util.EnumMap;
import java.util.Stack;
import java.util.ArrayList;
import static pacman.game.Constants.*;

/**
 * Implements a controller using iterative deepening
 */
public class DFS extends Controller<MOVE> {
    private static int ITERATIONS = 3;
    private Stack<TreeNode> tree = new Stack<>();


    Controller<EnumMap<GHOST, MOVE>> spookies;
    public DFS(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }

    /**
     * Evaluates current game state, then uses perceptron weights to decide what to do
     * @param game A copy of the current game
     * @param timeDue The time the next move is due
     * @return move decision
     */
    public MOVE getMove(Game game,long timeDue) {
        int iter = ITERATIONS;

        //root node
        tree.push(new TreeNode(game.copy(), null, null));

        while(iter > 0){
            TreeNode tmp = tree.pop();
            for (MOVE move : MOVE.values()){
                Game tmpGame = game.copy();
                for(int i = 0; i < 4; i++){
                    tmpGame.advanceGame(move, spookies.getMove());
                }

                TreeNode child = new TreeNode(tmpGame, move, tmp);
                tree.push(child);
            }
            iter--;
        }

        TreeNode foundMove = tree.pop();
        while(foundMove.getParent().getMove() != null){
            foundMove = foundMove.getParent();
        }

        return foundMove.getMove();

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


