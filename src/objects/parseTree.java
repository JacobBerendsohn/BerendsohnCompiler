package objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

// This class was adapted from the psuedocode on page 154 of the parse PDF
// as well as treeDemo.js from labouseur.com
public class parseTree {
    node rootNode = null;
    node currentNode = null;
    String treeString = "";
    boolean error = false;

    public node getCurrentNode() {
        return this.currentNode;
    }

    public void clearTree() {
        rootNode = null;
        currentNode = null;
        treeString = "";
        error = false;
    }

    public void setCurrentNode(node n) {
        this.currentNode = n;
    }

    public node getRootNode() {
        return this.rootNode;
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

    public String printSymbolTable() {
        String symbolTable = "";
        symbolTable += "Name    Type    isINIT    isUSED    SCOPE\n";

        ArrayList<Map<String, scope>> scopes = expandSymbolTable(rootNode, 0);
        ArrayList<Object[]> setKeys = new ArrayList<>();

        for (int i = 0; i < scopes.size(); i++) {
            setKeys.add(scopes.get(i).keySet().toArray());
        }

        for (int j = 0; j < setKeys.size(); j++) {

            for (int k = 0; k < setKeys.get(j).length; k++) {
                symbolTable += scopes.get(j).get(setKeys.get(j)[k]).getName() + "       "
                        + scopes.get(j).get(setKeys.get(j)[k]).getType() + "     "
                        + scopes.get(j).get(setKeys.get(j)[k]).isInit() + "      "
                        + scopes.get(j).get(setKeys.get(j)[k]).isUsed() + "     "
                        + scopes.get(j).get(setKeys.get(j)[k]).getScope() + "\n";
            }
        }

        return symbolTable;
    }

    ArrayList<Map<String, scope>> scopes = new ArrayList<>();

    public ArrayList<Map<String, scope>> expandSymbolTable(node curNode, int depth) {
        // Adding space for visuals

        // Checking for leaf nodes
        if (curNode.getChildren().isEmpty()) {

            addScopeToList(curNode, scopes);

        } else {
            // Children present so show interior branches

            addScopeToList(curNode, scopes);
            // scopes.add(curNode.getScopes());
            // Recursion loop [FUN] :)
            for (int i = 0; i < curNode.getChildren().size(); i++) {
                expandSymbolTable(curNode.getChildren().get(i), depth + 1);
            }
        }

        return scopes;

    }

    public void addScopeToList(node curNode, ArrayList<Map<String, scope>> scopes) {
        scopes.add(curNode.getScopes());
    }

}
