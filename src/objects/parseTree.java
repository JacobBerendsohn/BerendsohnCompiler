package objects;

// This class was adapted from the psuedocode on page 154 of the parse PDF
// as well as treeDemo.js from labouseur.com
public class parseTree {
    node rootNode = null;
    node currentNode = null;
    String treeString = "";
    boolean error = false;

    public void clearTree() {
        rootNode = null;
        currentNode = null;
        treeString = "";
        error = false;
    }

    public void addNode(String label, Boolean isLeaf) {
        // Creating the node to be added to the tree and naming it
        node curParseNode = new node(label);

        // Checking if we have a root node yet, if not make this one the root
        if (this.rootNode == null) {
            curParseNode.setRoot(true);
            this.rootNode = curParseNode;
            curParseNode.setParent(null);
        } else {
            // Setting this nodes parent to the last node parsed
            curParseNode.setParent(currentNode);

            // Making this current node a child of the last node parsed
            currentNode.addChild(curParseNode);
        }

        // Check to see if the node is a leaf node if not, set this node to currNode
        if (!isLeaf) {
            this.currentNode = curParseNode;
        }

    }

    // Function for ending this lineage of children, or this branch
    // Taken from "endChildren" in parseTree example
    public void executeOrder66() {
        // Checking that there is a node above this current one in the parse tree
        if (!currentNode.isRoot()) {
            // Changing current node to its parent node
            currentNode = currentNode.getParent();
        } else {
            // Add error handling
        }
    }

    // Function to visualize our current parse tree

    public String toString() {
        treeString = "";

        // Starting recursive drawing function
        expand(rootNode, 0);
        return treeString;
    }

    // Expands the tree to show depth in a 2D way
    // Taken from treeDemo.js
    public void expand(node curNode, int depth) {
        // Adding space for visuals
        for (int i = 0; i < depth; i++) {
            treeString += "-";
        }

        // Checking for leaf nodes
        if (curNode.getChildren().isEmpty()) {
            treeString += "[" + curNode.getName() + "]\n";
        } else {
            // Children present so show interior branches
            treeString += "<" + curNode.getName() + ">\n";

            // Recursion loop [FUN] :)
            for (int i = 0; i < curNode.getChildren().size(); i++) {
                expand(curNode.getChildren().get(i), depth + 1);
            }
        }
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

}