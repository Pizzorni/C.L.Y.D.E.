package pacman.controllers.uninformed;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.Stack;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * A simple depth first search for PacMan
 */
public class DFS extends Controller<MOVE> {
    private static int ITERATIONS = 3;
    private Stack<TreeNode> tree = new Stack<>();


    Controller<EnumMap<GHOST, MOVE>> spookies;

    public DFS(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }

    /**
     * Performs depth first search to a depth of 3, before timing out. Returns the parent of the deepest explored
     * move.
     *
     * @param game    A copy of the current game
     * @param timeDue The time the next move is due
     * @return move decision
     */
    public MOVE getMove(Game game, long timeDue) {
        int iter = ITERATIONS;

        //root node
        tree.push(new TreeNode(game.copy(), null, null, 0));

        while (iter > 0) {
            TreeNode tmp = tree.peek();
            for (MOVE move : MOVE.values()) {
                Game tmpGame = tmp.getGame().copy();
                for (int i = 0; i < 4; i++) {
                    tmpGame.advanceGame(move, spookies.getMove(tmpGame.copy(), -1));
                }

                TreeNode child = new TreeNode(tmpGame, move, tmp, tmp.getDepth() + 1);
                tree.push(child);
            }
            iter--;
        }

        TreeNode foundMove = tree.pop();
        while (foundMove.getDepth() != 1) {
            foundMove = foundMove.getParent();
        }

        return foundMove.getMove();

    }

}


