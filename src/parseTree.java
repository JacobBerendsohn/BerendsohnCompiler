// This class was adapted from the psuedocode on page 154 of the parse PDF
public class parseTree {
    node rootNode = null;
    node currentNode = null;

    public void addNode(String kind, String label, Boolean isLeaf) {
        // Creating the node to be added to the tree and naming it
        node curParseNode = new node();
        curParseNode.name = label;

        // Setting the root node and the parent node for a current tree
        if (this.rootNode != null) {
            curParseNode.parent = currentNode;
        } else {
            this.rootNode = curParseNode;
            curParseNode.parent = null;
        }

        // Check to see if the node is a leaf node
        if (isLeaf) {
            this.currentNode = curParseNode;
        }

    }
}
