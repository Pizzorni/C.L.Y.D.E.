package pacman.controllers.informed;

import pacman.controllers.Controller;
import pacman.game.Game;
import java.util.EnumMap;
import static pacman.game.Constants.*;
import java.util.ArrayList;
import java.util.PriorityQueue;

/*
 */
public class Astar extends Controller<MOVE> {
    private static int DEPTH = 2;
    private static MOVE[] moves;
    private Game[] games;
    private int last_score;
    private int GHOST_DIST_WEIGHT = 1;
    private int PILL_DIST_WEIGHT = 1;
    private int numNodes = 20;

    Controller<EnumMap<GHOST, MOVE>> spookies;
    public Astar(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies;
    }
    
    public MOVE getMove(Game game,long timeDue) {
      int i = 0;
     // for(MOVE move: MOVE.values()){
      //  moves[i] = move;
      //  i++;
     // }
      moves = MOVE.values();
      ArrayList<Node> unevaluatedMoves = new ArrayList<Node>();
      PriorityQueue<Node> evaluatedMoves = new PriorityQueue<Node>(numNodes);
      for(MOVE move: MOVE.values()){
        Node temp = new Node(game.copy(), 0, move, null);
        unevaluatedMoves.add(temp);
      }

      return MOVE.LEFT;


    }  

    private int evaluateMove(Game game, MOVE move){
      int [] activePills = game.getActivePillsIndices();
      int [] activePowerPills = game.getActivePowerPillsIndices();
      int numPills = activePills.length;
      int numPowerPills = activePowerPills.length;
      int [] allPills = new int[numPills + numPowerPills];
      int pacmanPos = game.getPacmanCurrentNodeIndex();

      for(int i = 0; i < numPills; i++){
        allPills[i] = activePills[i];
      }

      for(int i = 0; i < numPowerPills; i++){
        allPills[numPills + i] = activePowerPills[i];
      }

      int minDistToPill = Integer.MAX_VALUE;
      for(int i = 0; i < allPills.length; i++){
        int currDist = game.getShortestPathDistance(pacmanPos, allPills[i]);
        minDistToPill = (minDistToPill < currDist) ? currDist : minDistToPill;
      }
      
      int minDistToGhost = Integer.MAX_VALUE;
      for(GHOST spookie : GHOST.values()){
        int currDist = game.getShortestPathDistance(pacmanPos, game.getGhostCurrentNodeIndex(spookie));
        minDistToGhost = (minDistToGhost < currDist) ? currDist : minDistToGhost;
      }

    //  int currScore = game.getScore();
      int moveEval = (GHOST_DIST_WEIGHT * minDistToGhost) - (PILL_DIST_WEIGHT * minDistToPill);
      return moveEval;
    }
}


class Node implements Comparable<Node> {
  private Game game;
  private int cost;
  private MOVE move;
  private MOVE parentMove;

  public Node(Game game, int cost, MOVE move, MOVE parent){
    this.game = game;
    this.cost = cost;
    this.move = move;
    this.parentMove = parent;
  }

  public int compareTo(Node other){
    return Integer.compare(this.cost, other.cost);
  }

  public void setCost(int cost){
    this.cost = cost;
  }

  public void setGame(Game game){
    this.game = game;
  }
  public void setMove(MOVE move){
    this.move = move;
  }
  public void setParent(MOVE move){
    this.parentMove = move;
  }

  public int getCost(){
    return cost;
  }
  
  public Game getGame(){
    return game;
  }
  
  public MOVE getMove(){
    return move;
  }
  public MOVE getParent(){
    return parentMove;
  }

}





