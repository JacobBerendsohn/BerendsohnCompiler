// This class was adapted from the psuedocode on page 154 of the parse PDF
public class parseTree {
    node rootNode = null;
    node currentNode = null;

    public void addNode(String label, Boolean isLeaf) {
        // Creating the node to be added to the tree and naming it
        node curParseNode = new node(label);
        curParseNode.name = label;

        // Check to see if the node is a leaf node
        if (isLeaf) {
            this.currentNode = curParseNode;
        }

    }
}
