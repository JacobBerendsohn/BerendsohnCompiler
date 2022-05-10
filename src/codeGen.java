import objects.parseTree;
import objects.scope;
import objects.token;
import objects.node;

public class codeGen {
    // Creation of 256 bytes of data, held in an array of strings
    String[] data = new String[0xFF];
    int memPointer = 0x00;

    public String[] generateCode(parseTree AST) {
        return data;
    }

    public void iterateTree(parseTree AST) {

    }

    public void iterateTreeRecur(node curNode, int depth) {

        // Checking for leaf nodes
        if (curNode.getChildren().isEmpty()) {

            if (curNode.getParent().getName().equals("VarDecl")) {

            } else if (curNode.getParent().getName().equals("AssignmentStatement")) {

            } else if (curNode.getParent().getName().equals("PrintStatement")) {

            } else if (curNode.getParent().getName().equals("IfStatement")) {

            } else if (curNode.getParent().getName().equals("WhileStatement")) {

            }

        } else {
            // Children present so show interior branches

            // Recursion loop [FUN] :)
            for (int i = 0; i < curNode.getChildren().size(); i++) {
                iterateTreeRecur(curNode.getChildren().get(i), depth + 1);
            }
        }
    }
}
