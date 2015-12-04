package pacman.controllers.decisiontree;
import java.awt.dnd.DnDConstants;
import java.util.ArrayList;

/**
 * Created by giorgio on 12/4/15.
 */
public class DNode {

    private DNode parent;
    private DNode[] children;
    private int numAttributes;
    private ArrayList<int[]> Instances;

    public DNode(DNode parent, DNode[] children, int numAttributes, ArrayList<int[]> instances) {
        this.parent = parent;
        this.children = children;
        this.numAttributes = numAttributes;
        this.Instances = instances;
    }

    public DNode getParent() {
        return parent;
    }

    public void setParent(DNode parent) {
        this.parent = parent;
    }

    public DNode[] getChildren() {
        return children;
    }

    public void setChildren(DNode[] children) {
        this.children = children;
    }

    public int getNumAttributes() {
        return numAttributes;
    }

    public void setNumAttributes(int numAttributes) {
        this.numAttributes = numAttributes;
    }

    public ArrayList<int[]> getInstances() {
        return Instances;
    }

    public void setInstances(ArrayList<int[]> instances) {
        Instances = instances;
    }







}
