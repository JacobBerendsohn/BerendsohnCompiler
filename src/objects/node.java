package objects;

import java.util.ArrayList;

public class node {
    String name = "";
    node parent = null;
    ArrayList<node> children = new ArrayList<>();
    token leafToken = null;
    Boolean isRoot = false;

    public node(String name) {
        this.name = name;
    }

    public node() {

    }

    // Function for adding children to a node
    public void addChild(node child) {
        this.children.add(child);
    }

    public ArrayList<node> getChildren() {
        return this.children;
    }

    public void setParent(node parent) {
        this.parent = parent;
    }

    public node getParent() {
        return this.parent;
    }

    public void setRoot(Boolean root) {
        this.isRoot = root;
    }

    public String getName() {
        return this.name;
    }

    public void addLeafToken(token leafT) {
        this.leafToken = leafT;
    }

    public String listChildren() {
        String children = "Children:\n";

        for (node n : this.children) {
            children += (n.getName() + "\n");
        }

        return children;
    }

    // Check for root nodes
    public Boolean isRoot() {
        return this.isRoot;
    }
    /*
     * // For creating leaf Nodes
     * public node(String name, node parent, node child, token leafToken) {
     * this.name = name;
     * this.parent = parent;
     * this.children.add(child);
     * this.leafToken = leafToken;
     * }
     * 
     * // Adding a non leaf node
     * public node(String name, node parent, node child) {
     * this.name = name;
     * this.parent = parent;
     * this.children.add(child);
     * }
     * 
     * // Adding a root node
     * public node(String name, node parent, node child, Boolean isRoot) {
     * this.name = name;
     * this.parent = parent;
     * this.children.add(child);
     * this.isRoot = isRoot;
     * }
     */

    // Basic constructor

}
