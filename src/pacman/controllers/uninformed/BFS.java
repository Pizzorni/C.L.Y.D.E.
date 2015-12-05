package pacman.controllers.uninformed;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.PriorityQueue;

import static pacman.game.Constants.GHOST;
import static pacman.game.Constants.MOVE;

/**
 * A simple breadth first search for PacMan
 */
public class BFS extends Controller<MOVE> {
    private static int ITERATIONS = 3;
    private PriorityQueue<TreeNode> tree = new PriorityQueue<>();
    private ArrayList<TreeNode> treeIndexed = new ArrayList<>();


    Controller<EnumMap<GHOST, MOVE>> spookies;

    public BFS(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }

    /**
     * Performs breadth first search to a depth of 3, before timing out. Returns the parent of the deepest explored
     * move.
     *
     * @param game    A copy of the current game
     * @param timeDue The time the next move is due
     * @return move decision
     */

    public MOVE getMove(Game game, long timeDue) {
        int iter = ITERATIONS;

        //root node
        TreeNode root = new TreeNode(game.copy(), null, null, 0);
        tree.add(root);
        treeIndexed.add(root);
        for (int i = 0; i < iter; i++) {
            TreeNode tmp = treeIndexed.get(i);
            for (MOVE move : MOVE.values()) {
                Game tmpGame = tmp.getGame();
                for (int j = 0; j < 4; j++) {
                    tmpGame.advanceGame(move, spookies.getMove(tmpGame.copy(), -1));
                }
                TreeNode child = new TreeNode(tmpGame, move, tmp, tmp.getDepth() + 1);
                tree.add(child);
                treeIndexed.add(child);
            }
        }

        tree.remove(); // get rid of root node
        TreeNode foundMove = tree.remove();
        while (foundMove.getDepth() != 1) {
            foundMove = foundMove.getParent();
        }

        return foundMove.getMove();

    }

}


