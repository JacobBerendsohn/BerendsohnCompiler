// This class was adapted from the psuedocode on page 154 of the parse PDF
public class parseTree {
    node rootNode = null;
    node currentNode = null;

    public void addNode(String label, Boolean isLeaf) {
        // Creating the node to be added to the tree and naming it
        node curParseNode = new node(label);
        curParseNode.name = label;

        // Checking if we have a root node yet, if not make this one the root
        if (this.rootNode == null) {
            curParseNode.setRoot(true);
            this.rootNode = curParseNode;
        } else {
            // Setting this nodes parent to the last node parsed
            curParseNode.setParent(currentNode);

            // Making this current node a child of the last node parsed
            this.currentNode.addChild(currentNode);

        }

        // Check to see if the node is a leaf node if not, set this node to currNode
        if (!isLeaf) {
            this.currentNode = curParseNode;
        }

    }

    // Function for ending this lineage of children, or this branch
    // Taken from "endChildren" in parseTree example
    public void executeOrder66() {

    }
}
