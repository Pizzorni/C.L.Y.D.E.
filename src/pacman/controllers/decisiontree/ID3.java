package pacman.controllers.decisiontree;


import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.EnumMap;

import static pacman.game.Constants.*;

/**
 * This controller implements a very simple Hill Climber. He evaluates every neighbor (move) around him
 * and picks the best one. Returns the move that is locally best. Very short sighted.
 */

public class ID3 extends Controller<MOVE> {
    Controller<EnumMap<GHOST, MOVE>> spookies;
    private GHOST closestGhost;
    private int closestPill;
    private int closestPillDist;
    private int ghostPANICRUNAWAY;
    private int NUMATTRIBUTES = 3;
    private int[] GHOST_CUTOFFS = {10,25};
    private int[] PILL_CUTOFFS = {25};
    private int[] POWER_DIST = {15};
    private final int ATTR_GHOST = 0;
    private final int ATTR_PILL = 1;
    private final int ATTR_POWER = 2;


    // ghost, then pill
    // 0 run, 1 target pill
    private static int[][] INSTANCES= {
            {5, 2, 0},
            {5, 5, 0},
            {5, 10, 0},
            {5, 15, 0},
            {5, 20, 0},
            {10, 2, 1},
            {10, 5, 1},
            {10, 10, 0},
            {10, 15, 0},
            {10, 20, 0},
            {15, 2, 1},
            {15, 5, 1},
            {15, 10, 1},
            {15, 15, 1},
            {15, 20, 0},
            {20, 2, 1},
            {20, 5, 1},
            {20, 10, 1},
            {20, 15, 1},
            {20, 20, 1},
            {Integer.MAX_VALUE, 5, 1},
            {Integer.MAX_VALUE, 10, 1},
            {Integer.MAX_VALUE, 15, 1},
            {Integer.MAX_VALUE, 20, 1}};


    /**
     * Hill Climber constructor.
     *
     * @param spookies The ghost controller to be played, and therefore simulated, against.
     */
    public ID3(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies; // SPOOKIES
    }

    /**
     *
     * @param game game to simulate on
     * @return Returns the move that gets it the highest score in its immediate vicinity
     */
    public MOVE getMove(Game game, long timeDue) {

        DNode rootNode = new DNode(null, null, NUMATTRIBUTES, null);
        ArrayList<int[]> rootInstances = new ArrayList<>();
        for(int i = 0; i < 24; i++){
            rootInstances.add(INSTANCES[i]);
        }
        rootNode.setInstances(rootInstances);






        return MOVE.LEFT;

    }

    public double[][] getProportion(ArrayList<int[]> instance, int attribute){
        // first index
        // 0 --> negative
        // 1 --> positive

        // second index corresponds to attribute specific cutoffs
        double count[][] = new double[2][3];
        int value;
        int classification;
        for(int[] set : instance){
            if(attribute == ATTR_GHOST){
                value = set[attribute];
                classification = set[3];
                if(value <= 10){
                    if(classification == 0){
                        count[0][0]++;
                    }
                    else{
                        count[1][0]++;
                    }
                }
                else if (10 < value && value < 25){
                    if(classification == 0){
                        count[0][1]++;
                    }
                    else{
                        count[1][1]++;
                    }
                }
                else{
                    if(classification == 0){
                        count[0][2]++;
                    }
                    else{
                        count[1][2]++;
                    }
                }
            }
            if(attribute == ATTR_PILL){
                value = set[attribute];
                classification = set[3];
                if( value < 25){
                    if(classification == 0){
                        count[0][0]++;
                    }
                    else{
                        count[1][0]++;
                    }
                }
                else{
                    if(classification == 0){
                        count[0][1]++;
                    }
                    else{
                        count[1][1]++;
                    }
                }
            }
            if(attribute == ATTR_POWER){
                value = set[attribute];
                classification = set[3];
                if(value < 15){
                    if(classification == 0){
                        count[0][0]++;
                    }
                    else{
                        count[1][0]++;
                    }
                }
                else{
                    if(classification == 0){
                        count[0][1]++;
                    }
                    else{
                        count[1][1]++;
                    }
                }
            }
        }
        return count;
    }

    public double entropy(double posProb, double negProb){
        double entropy;
        entropy = -posProb * (Math.log(posProb)/Math.log(2)) - negProb * (Math.log(negProb)/Math.log(2));
        return entropy;
    }
}


