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
    private GHOST closestSpooky;
    private int closestPill;
    private int closestPillDist;
    private int closestPower;
    private int closestPowerDist;
    private int spookiestSpookyDist;
    private int NUMATTRIBUTES = 3;
    private int[] GHOST_CUTOFFS = {10, 25};
    private int[] PILL_CUTOFFS = {25};
    private int[] POWER_DIST = {15};
    private final int ATTR_GHOST = 0;
    private final int ATTR_PILL = 1;
    private final int ATTR_POWER = 2;
    private DNode rootNode;


    // ghost, then pill, then power pill
    // 0 run, 1 target pill
    private static int[][] INSTANCES = {
            {5, 2, 5, 0},
            {5, 5, 5, 0},
            {5, 10, 5, 0},
            {5, 15, 5, 0},
            {5, 20, 5, 0},
            {5, 2, 20, 0},
            {5, 5, 20, 0},
            {5, 10, 20, 0},
            {15, 15, 20, 0},
            {25, 20, 20, 0},
            {10, 2, 5, 0},
            {10, 5, 5, 0},
            {10, 10, 5, 0},
            {10, 15, 5, 0},
            {10, 20, 5, 0},
            {10, 2, 20, 0},
            {10, 5, 20, 0},
            {10, 10, 20, 0},
            {10, 15, 20, 0},
            {10, 20, 20, 0},
            {15, 2, 5, 1},
            {15, 5, 5, 1},
            {15, 10, 5, 1},
            {15, 15, 5, 1},
            {15, 20, 5, 1},
            {15, 2, 10, 1},
            {15, 5, 10, 1},
            {15, 10, 10, 1},
            {15, 15, 10, 1},
            {15, 20, 10, 1},
            {15, 2, 20, 0},
            {15, 5, 20, 0},
            {15, 10, 20, 0},
            {15, 15, 20, 0},
            {15, 20, 20, 0},
            {20, 2, 5, 1},
            {20, 5, 5, 1},
            {20, 10, 5, 1},
            {20, 15, 5, 1},
            {20, 20, 5, 1},
            {20, 2, 10, 1},
            {20, 5, 10, 1},
            {20, 10, 10, 1},
            {20, 15, 10, 1},
            {20, 20, 10, 1},
            {20, 2, 20, 0},
            {20, 5, 20, 0},
            {20, 10, 20, 0},
            {20, 15, 20, 0},
            {20, 20, 20, 0},
            {25, 2, 5, 1},
            {25, 5, 5, 1},
            {25, 10, 5, 1},
            {25, 15, 5, 1},
            {25, 20, 5, 1},
            {25, 2, 10, 1},
            {25, 5, 10, 1},
            {25, 10, 10, 1},
            {25, 15, 10, 1},
            {25, 20, 10, 1},
            {25, 2, 20, 1},
            {25, 5, 20, 1},
            {25, 10, 20, 1},
            {25, 15, 20, 1},
            {25, 20, 20, 1},
            {25, 30, 5, 0},
            {25, 30, 5, 0},
            {25, 30, 5, 0},
            {25, 30, 5, 0},
            {25, 30, 5, 0},
            {25, 30, 10, 0},
            {25, 30, 10, 0},
            {25, 30, 10, 0},
            {25, 30, 10, 0},
            {25, 30, 10, 0},
            {25, 30, 20, 0},
            {25, 30, 20, 0},
            {25, 30, 20, 0},
            {25, 30, 20, 0},
            {25, 30, 20, 0},

//            {Integer.MAX_VALUE, 5, 1},
//            {Integer.MAX_VALUE, 10, 1},
//            {Integer.MAX_VALUE, 15, 1},
//            {Integer.MAX_VALUE, 20, 1}
    };


    /**
     * Hill Climber constructor.
     *
     * @param spookies The ghost controller to be played, and therefore simulated, against.
     */
    public ID3(Controller<EnumMap<GHOST, MOVE>> spookies) {
        this.spookies = spookies; // SPOOKIES
        this.rootNode = new DNode(null, null, 0, 0);
        ArrayList<int[]> rootInstances = new ArrayList<>();
        for (int i = 0; i < INSTANCES.length; i++) {
            rootInstances.add(INSTANCES[i]);
        }
        rootNode.setInstances(rootInstances);
        buildTree(rootNode);
    }

    /**
     * @param game game to simulate on
     * @return Returns the move that gets it the highest score in its immediate vicinity
     */
    public MOVE getMove(Game game, long timeDue) {

        // RECON
        gatherData(game);
        int current = game.getPacmanCurrentNodeIndex();
        DNode decisionNode = traverseTree(rootNode);
        int decision = decisionNode.getDecision();
        if (decision == 0) {
            return game.getNextMoveAwayFromTarget(current, game.getGhostCurrentNodeIndex(closestSpooky), DM.PATH);
        } else {
            return game.getNextMoveTowardsTarget(current, closestPill, DM.PATH);
        }
        // just in case

    }

    public DNode traverseTree(DNode node) {
        int attribute = node.getAttribute();
        boolean baseCase = checkLeaf(node);
        if (baseCase) {
            return node;
        }

        if (attribute == ATTR_GHOST) {
            if (spookiestSpookyDist <= 10) {
                return traverseTree(node.getChildren().get(0));
            } else if (10 < spookiestSpookyDist && spookiestSpookyDist < 25) {
                return traverseTree(node.getChildren().get(1));
            } else {
                return traverseTree(node.getChildren().get(2));
            }
        }

        if (attribute == ATTR_PILL) {
            if (closestPillDist <= 25) {
                return traverseTree(node.getChildren().get(0));
            } else {
                return traverseTree(node.getChildren().get(1));
            }
        } else {
            if (closestPowerDist <= 15) {
                return traverseTree(node.getChildren().get(0));
            } else {
                return traverseTree(node.getChildren().get(1));
            }
        }
    }

    /**
     * Evaluates game state
     * Gets distance of the closest ghost and closest pill
     *
     * @param game
     */
    private void gatherData(Game game) {
        //get distance of nearest non-edible ghost and distance to nearest pill

        int current = game.getPacmanCurrentNodeIndex();

        //Get distance from closest ghost
        spookiestSpookyDist = Integer.MAX_VALUE;
        int tmp;
        //NOTE: this for loop is adapted from the StarterPacMan controller
        for (GHOST ghost : GHOST.values()) {
            if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0) {
                tmp = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
                if (tmp < spookiestSpookyDist)
                    spookiestSpookyDist = tmp;
                closestSpooky = ghost;
            }
        }


        //Get distance of nearest pill (normal or power)
        //NOTE: this section is also adapted from the StarterPacMan controller
        int[] pills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();

        ArrayList<Integer> targetPills = new ArrayList<Integer>();
        ArrayList<Integer> targetPower = new ArrayList<Integer>();

        for (int i = 0; i < pills.length; i++)                    //check which pills are available
            if (game.isPillStillAvailable(i))
                targetPills.add(pills[i]);

        for (int i = 0; i < powerPills.length; i++)            //check with power pills are available
            if (game.isPowerPillStillAvailable(i))
                targetPower.add(powerPills[i]);

        int[] targetsArrayPills = new int[targetPills.size()];        //convert from ArrayList to array
        int[] targetsArrayPower = new int[targetPower.size()];        //convert from ArrayList to array

        for (int i = 0; i < targetsArrayPills.length; i++)
            targetsArrayPills[i] = targetPills.get(i);

        for (int i = 0; i < targetsArrayPower.length; i++)
            targetsArrayPower[i] = targetPower.get(i);

        //return the next direction once the closest target has been identified
        closestPill = game.getClosestNodeIndexFromNodeIndex(current, targetsArrayPills, DM.PATH);
        closestPillDist = game.getShortestPathDistance(current, closestPill);

        closestPower = game.getClosestNodeIndexFromNodeIndex(current, targetsArrayPower, DM.PATH);
        closestPowerDist = game.getShortestPathDistance(current, closestPower);

        //info. gathered.
    }


    public void buildTree(DNode node) {
        int numBranches;
        boolean baseCase = checkLeaf(node);
        if (baseCase) {
            node.setDecision(node.getInstances().get(0)[3]);
        } else {
            int attributeToPick = chooseAttribute(node.getInstances());
            numBranches = (attributeToPick == 0) ? 3 : 2;
            node.setAttribute(attributeToPick);
            //  System.out.println("Attribute " + attributeToPick);
            for (int i = 0; i < numBranches; i++) {
                ArrayList<int[]> subInstance = partitionInstance(node.getInstances(), attributeToPick, i);
                DNode child = new DNode(node, subInstance, -1, -1);
                node.setChild(child);
                buildTree(child);
            }
        }
    }

    public ArrayList<int[]> partitionInstance(ArrayList<int[]> instance, int attribute, int branch) {
        ArrayList<int[]> subInstance;
        if (attribute == ATTR_GHOST) {
            subInstance = partitionGhostInstance(instance, branch);
        } else if (attribute == ATTR_PILL) {
            subInstance = partitionPillInstance(instance, branch);
        } else {
            subInstance = partitionPowerInstance(instance, branch);
        }
        return subInstance;
    }

    public ArrayList<int[]> partitionGhostInstance(ArrayList<int[]> instance, int branch) {
        ArrayList<int[]> subInstance = new ArrayList<>();
        if (branch == 0) {
            for (int[] set : instance) {
                if (set[0] <= 10) {
                    subInstance.add(set);
                }
            }
        }
        if (branch == 1) {
            for (int[] set : instance) {
                if (10 < set[0] && set[0] < 25) {
                    subInstance.add(set);
                }
            }
        } else {
            for (int[] set : instance) {
                if (set[0] >= 25) {
                    subInstance.add(set);
                }
            }
        }

        return subInstance;
    }

    public ArrayList<int[]> partitionPillInstance(ArrayList<int[]> instance, int branch) {
        ArrayList<int[]> subInstance = new ArrayList<>();
        if (branch == 0) {
            for (int[] set : instance) {
                if (set[1] <= 25) {
                    subInstance.add(set);
                }
            }
        } else {
            for (int[] set : instance) {
                if (set[1] > 25) {
                    subInstance.add(set);
                }
            }
        }
        return subInstance;
    }

    public ArrayList<int[]> partitionPowerInstance(ArrayList<int[]> instance, int branch) {
        ArrayList<int[]> subInstance = new ArrayList<>();
        if (branch == 0) {
            for (int[] set : instance) {
                if (set[2] <= 15) {
                    subInstance.add(set);
                }
            }
        } else {
            for (int[] set : instance) {
                if (set[2] > 15) {
                    subInstance.add(set);
                }
            }
        }
        return subInstance;
    }

    // computes information gain for all attributes and returns the best attribute
    public int chooseAttribute(ArrayList<int[]> instance) {
        int bestAttribute = 0;
        double bestInfoGain = -1;
        for (int i = 0; i < NUMATTRIBUTES; i++) {
            double infoGain;
            infoGain = informationGain(instance, i);
            //  System.out.println(infoGain);
            if (bestInfoGain < infoGain) {
                bestInfoGain = infoGain;
                bestAttribute = i;
            }
        }
        return bestAttribute;
    }

    // iterates through instance to check if all target values are identical
    public boolean checkLeaf(DNode node) {
        // base case
        ArrayList<int[]> nodeInstance = node.getInstances();
        int targetVal = nodeInstance.get(0)[3];
        int count = 0;
        for (int[] set : nodeInstance) {
            count++;
            //  System.out.println(Arrays.toString(set) + " count: " + count);
            if (targetVal != set[3]) {
                return false;
            }
        }
//        double entropy = entropyOnAttribute(nodeInstance, node.getAttribute());
//        if(entropy == 0){
//            return true;
//        }
        return true;
    }

    public double[][] getCount(ArrayList<int[]> instance, int attribute) {
        // first index
        // 0 --> negative
        // 1 --> positive

        // second index corresponds to attribute specific cutoffs
        int value;
        int classification;
        double count[][] = new double[2][3];
        for (int i = 0; i < instance.size(); i++) {
            //  System.out.println(instance.get(i)[attribute]);
            if (attribute == ATTR_GHOST) {
                value = instance.get(i)[attribute];
//                System.out.println("instance " + Arrays.toString(instance.get(i)) + " i: " + i);
                classification = instance.get(i)[3];
                //  System.out.println("Value: " + value + " Classification: " + classification);
                if (value <= 10) {
                    if (classification == 0) {
                        count[0][0]++;
                    } else if (classification == 1) {
                        count[0][1]++;
                    }
                } else if (10 < value && value < 25) {
                    if (classification == 0) {
                        count[0][1]++;
                    } else if (classification == 1) {
                        count[1][1]++;
                    }
                } else {
                    if (classification == 0) {
                        count[0][2]++;
                    } else if (classification == 1) {
                        count[1][2]++;
                    }
                }
                //  System.out.println(Arrays.toString(count[0]));
                // return count;
            }
            if (attribute == ATTR_PILL) {
                value = instance.get(i)[attribute];
                classification = instance.get(i)[3];
                if (value == 25) {
                    if (classification == 0) {
                        count[0][0]++;
                    } else if (classification == 1) {
                        count[1][0]++;
                    }
                } else {
                    if (classification == 0) {
                        count[0][1]++;
                    } else if (classification == 1) {
                        count[1][1]++;
                    }
                }
                //   return count;
            }
            if (attribute == ATTR_POWER) {
                value = instance.get(i)[attribute];
                classification = instance.get(i)[3];
                if (value <= 15) {
                    if (classification == 0) {
                        count[0][0]++;
                    } else if (classification == 1) {
                        count[1][0]++;
                    }
                } else {
                    if (classification == 0) {
                        count[0][1]++;
                    } else if (classification == 1) {
                        count[1][1]++;
                    }
                }
                //   return count;
            }
        }
        //System.out.println(Arrays.toString(count[0]));
        return count;
    }

    public double entropy(double posCount, double negCount) {
        double size = posCount + negCount;
        if (size == 0) {
            return 0;
        }
        System.out.println(negCount);
        double posProb = posCount / size;
        double negProb = negCount / size;
        double entropy = (-1 * posProb * (Math.log(posProb) / Math.log(2))) - (negProb * (Math.log(negProb) / Math.log(2)));
        //  System.out.println("retentropy: " + entropy);
        return entropy;

    }

    /*
     * computes entropy for a specific attribute given an instance
     */
    public double entropyOnAttribute(ArrayList<int[]> instance, int attribute) {
        double[][] attributeCount = getCount(instance, attribute);
        double entropy;
        double totalPos = 0;
        double totalNeg = 0;
        for (int i = 0; i < attributeCount[0].length; i++) {
            totalPos += attributeCount[1][i];
            totalNeg += attributeCount[0][i];
            System.out.println("totPos: " + totalPos + "totNeg: " + totalNeg);
        }

        entropy = entropy(totalPos, totalNeg);
        //System.out.println(entropy);
        return entropy;
    }

    /*
     *   computes entropy after partitioning instance based of attribute
     */
    public double entropyOnPartition(ArrayList<int[]> instance, int attribute) {
        double[][] count = getCount(instance, attribute);
        //  System.out.println(Arrays.toString(count[0]));
        double entropy = 0;
        double temptropy;
        double countSum;
        for (int i = 0; i < count[0].length; i++) {
            //  System.out.println(count[1][i]);
            temptropy = entropy(count[0][i], count[1][i]);
            //   System.out.println(temptropy);
            countSum = count[0][i] + count[1][i];
            entropy += temptropy * (countSum / instance.size());
        }
        return entropy;
    }

    public double informationGain(ArrayList<int[]> instance, int attribute) {
        double entropy = entropyOnAttribute(instance, attribute);
        double entropyPartition = entropyOnPartition(instance, attribute);
        return entropy - entropyPartition;
    }

}


