package pacman.controllers.uninformed;
import apple.laf.JRSUIUtils;
import pacman.controllers.Controller;
import pacman.game.Game;
import java.util.EnumMap;
import static pacman.game.Constants.*;

/**
 * A simple tree node implementation for use in uninformed search algorithms. Keeps track of game and move.
 */
public class TreeNode {
    private Game game;
    private MOVE move;
    private TreeNode parent;

    /**
     * Tree Node constructor
     * @param game Game to be simulated
     * @param move Move played in game
     */

    public TreeNode(Game game, MOVE move, TreeNode parent){
        this.game = game;
        this.move = move;
        this.parent = parent;
    }

    // Setters and getters

    public Game getGame(){
        return game;
    }
    public MOVE getMove(){
        return move;
    }
    public TreeNode getParent(){
        return parent;
    }

    public void setGame(Game game){
        this.game = game;
    }
    public void setMove(MOVE move){
        this.move = move;
    }
    public void setParent(TreeNode parent){
        this.parent = parent;
    }

}
