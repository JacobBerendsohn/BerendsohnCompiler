import java.util.ArrayList;

import objects.parseTree;
import objects.scope;
import objects.token;
import objects.node;
import objects.genTable;

public class codeGen {
    // Creation of 256 bytes of data, held in an array of strings
    String[] data = new String[0xFF];
    int memPointer = 0x00;

    // Creating arraylists for holding var and jump info
    ArrayList<genTable> variableTable = new ArrayList<>();
    ArrayList<genTable> jumpTable = new ArrayList<>();

    public String[] generateCode(parseTree AST) {

        iterateTree(AST);

        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                data[i] = "00";
            }
        }

        return data;
    }

    public void iterateTree(parseTree AST) {
        iterateTreeRecur(AST.getRootNode(), 0);
    }

    // Varibable to see which child of a parent we are on
    int numChild = 0;

    int curTempNum = 0;

    public void iterateTreeRecur(node curNode, int depth) {

        // Checking for leaf nodes
        if (curNode.getChildren().isEmpty()) {

            // Getting the number of children of the current parent
            int numParentsChildren = curNode.getParent().getChildren().size() - 1;

            if (curNode.getParent().getName().equals("VarDecl")) {

                int varDeclAddresses = 5;

                if (numChild == 0) {
                    numChild++;
                } else {

                    genTable curVar = new genTable("T" + Integer.toString(curTempNum), curNode.getName(), 0x00,
                            curNode.getToken());
                    variableTable.add(curVar);

                    for (int i = 0; i < varDeclAddresses; i++) {
                        if (i == 0) {
                            data[memPointer] = "A9";
                            memPointer++;
                        } else if (i == 1) {
                            data[memPointer] = "00";
                            memPointer++;
                        } else if (i == 2) {
                            data[memPointer] = "8D";
                            memPointer++;
                        } else if (i == 3) {
                            data[memPointer] = variableTable.get(curTempNum).getTempName();
                            memPointer++;
                        } else if (i == 4) {
                            data[memPointer] = "XX";
                        }
                    }

                    numChild = 0;
                    curTempNum++;

                }

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

    public void createDebug(String message) {
        System.out.println("DEBUG Semantic - " + message);
    }

    public void createInfo(String message) {
        System.out.println("INFO Semantic - " + message);
    }

    public void createError(String message) {
        System.out.println("ERROR Semantic - " + message);
    }
}
