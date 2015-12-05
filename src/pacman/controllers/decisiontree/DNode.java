package pacman.controllers.decisiontree;

import java.util.ArrayList;

/**
 * Created by giorgio on 12/4/15.
 */
public class DNode {

    private DNode parent;
    private ArrayList<DNode> children;
    private ArrayList<int[]> Instances;
    private int attribute;
    private int decision;


    public DNode(DNode parent, ArrayList<int[]> instances, int decision, int attribute) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.Instances = instances;
        this.attribute = attribute;
        this.decision = decision;
    }

    public DNode getParent() {
        return parent;
    }

    public void setParent(DNode parent) {
        this.parent = parent;
    }

    public ArrayList<DNode> getChildren() {
        return children;
    }

    public int getDecision() {
        return decision;
    }

    public void setDecision(int decision) {
        this.decision = decision;
    }

    public void setChildren(ArrayList<DNode> children) {
        this.children = children;
    }

    public void setChild(DNode child) {
        this.children.add(child);
    }

    public ArrayList<int[]> getInstances() {
        return Instances;
    }

    public void setInstances(ArrayList<int[]> instances) {
        Instances = instances;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }


}
