package pacman.controllers.uninformed;

import pacman.game.Game;

import static pacman.game.Constants.MOVE;

/**
 * A simple tree node implementation for use in uninformed search algorithms. Keeps track of game, move and depth.
 */
public class TreeNode implements Comparable<TreeNode> {
    private Game game;
    private MOVE move;
    private TreeNode parent;
    private int depth;

    /**
     * Tree Node constructor
     *
     * @param game  Game to be simulated
     * @param move  Move played in game
     * @param depth Depth of node in tree
     */

    public TreeNode(Game game, MOVE move, TreeNode parent, int depth) {
        this.game = game;
        this.move = move;
        this.parent = parent;
        this.depth = depth;
    }

    // Setters and getters

    public int compareTo(TreeNode other) {
        return Integer.compare(this.depth, other.depth);
    }

    public Game getGame() {
        return game;
    }

    public MOVE getMove() {
        return move;
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getDepth() {
        return depth;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMove(MOVE move) {
        this.move = move;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
