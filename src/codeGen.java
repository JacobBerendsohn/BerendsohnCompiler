import java.util.ArrayList;
import java.util.Map;

import java.util.HashMap;

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
    Map<String, String> variableTypes = new HashMap<String, String>();

    int numJumps = 0;

    int curHeapStart = 0xFE;

    boolean inIfStatement = false;
    int ifDepth = 0;

    String truePointer = "FA";
    String falsePointer = "F4";

    String variableNameHolder = "";

    String variableTypeHolder = "";

    int numChild = 0;

    int curTempNum = 0;

    public String[] generateCode(parseTree AST, parseTree fullScope) {

        data[0xFE] = "00";

        for (int i = 0; i < data.length; i++) {
            if (data[i] == null) {
                data[i] = "00";
            }
        }

        curHeapStart -= "true".length();

        String[] trueVar = "true".split("");
        String[] falseVar = "false".split("");

        for (int i = 0; i < "true".length(); i++) {
            char a = trueVar[i].charAt(0);
            data[curHeapStart + i] = Integer.toHexString((int) a).toUpperCase();
        }

        curHeapStart--;

        curHeapStart -= "false".length();

        for (int i = 0; i < "false".length(); i++) {
            char a = falseVar[i].charAt(0);
            data[curHeapStart + i] = Integer.toHexString((int) a).toUpperCase();
        }

        curHeapStart--;

        iterateTree(AST, fullScope);
        backPatch();

        return data;
    }

    public void backPatch() {

        memPointer++;

        int startStaticVars = memPointer;
        // Setting the correct addresses for each variable after code is generated
        for (genTable vars : variableTable) {
            vars.setAddress(memPointer);
            for (int dataPointer = 0; dataPointer < data.length; dataPointer++) {
                if (data[dataPointer].equals(vars.getTempName())) {
                    data[dataPointer] = Integer.toHexString(vars.getAddress()).toUpperCase();
                    data[dataPointer + 1] = "00";
                }
            }
            memPointer++;
        }

        for (genTable vars : jumpTable) {
            for (int dataPointer = 0; dataPointer < data.length; dataPointer++) {
                if (data[dataPointer].equals(vars.getTempName())) {
                    data[dataPointer] = Integer.toHexString(vars.getJumpDist()).toUpperCase();
                }
            }
            memPointer++;
        }

        memPointer = startStaticVars;

        System.out.println("");
    }

    public void iterateTree(parseTree AST, parseTree fullScope) {
        iterateTreeRecur(AST.getRootNode(), 0, fullScope);
    }

    // Varibable to see which child of a parent we are on

    public void iterateTreeRecur(node curNode, int depth, parseTree fullScope) {

        if (depth <= ifDepth) {
            inIfStatement = false;
        }
        // Checking for leaf nodes
        if (curNode.getChildren().isEmpty()) {

            // Getting the number of children of the current parent
            int numParentsChildren = curNode.getParent().getChildren().size() - 1;

            if (curNode.getParent().getName().equals("VarDecl")) {

                int varDeclAddresses = 5;

                if (numChild == 0) {

                    numChild++;
                    variableTypeHolder = curNode.getName();

                } else {

                    variableTypes.put(curNode.getName(), variableTypeHolder);

                    genTable curVar = new genTable("T" + Integer.toString(curTempNum), curNode.getName(), 0x00,
                            curNode.getToken());
                    variableTable.add(curVar);

                    // Storing an int declaration
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
                            data[memPointer] = variableTable.get(curTempNum).getTempName().toUpperCase();
                            memPointer++;
                        } else if (i == 4) {
                            data[memPointer] = "XX";
                            memPointer++;
                        }
                    }

                    numChild = 0;
                    curTempNum++;

                    if (inIfStatement) {
                        if (!jumpTable.isEmpty()) {
                            jumpTable.get(numJumps - 1).addToJump(varDeclAddresses);
                        }
                    }

                }

            } else if (curNode.getParent().getName().equals("AssignmentStatement")) {

                int assignIntAddresses = 5;

                if (numChild == 0) {

                    variableNameHolder = curNode.getName();
                    numChild++;

                } else if (numChild == 1) {

                    if (curNode.getToken().getType().equals("ID")) {

                        String currentTempVarL = "";

                        String currentTempVarR = "";

                        for (genTable curVar : variableTable) {
                            if (curVar.getVarName().equals(variableNameHolder)) {
                                currentTempVarL = curVar.getTempName();
                            }
                        }

                        for (genTable curVar : variableTable) {
                            if (curVar.getVarName().equals(curNode.getName())) {
                                currentTempVarR = curVar.getTempName();
                            }
                        }

                        int assignVariable = 6;

                        for (int i = 0; i < assignVariable; i++) {
                            if (i == 0) {
                                data[memPointer] = "AD";
                                memPointer++;
                            } else if (i == 1) {
                                data[memPointer] = currentTempVarR;
                                memPointer++;
                            } else if (i == 2) {
                                data[memPointer] = "XX";
                                memPointer++;
                            } else if (i == 3) {
                                data[memPointer] = "8D";
                                memPointer++;
                            } else if (i == 4) {
                                data[memPointer] = currentTempVarL;
                                memPointer++;
                            } else if (i == 5) {
                                data[memPointer] = "XX";
                                memPointer++;
                            }
                        }

                        numChild = 0;

                        if (inIfStatement) {
                            if (!jumpTable.isEmpty()) {
                                jumpTable.get(numJumps - 1).addToJump(assignVariable);
                            }
                        }

                    } else {

                        String currentTempVar = "";

                        for (genTable curVar : variableTable) {
                            if (curVar.getVarName().equals(variableNameHolder)) {
                                currentTempVar = curVar.getTempName();
                            }
                        }

                        if (variableTypes.get(variableNameHolder).equals("int")) {
                            for (int i = 0; i < assignIntAddresses; i++) {
                                if (i == 0) {
                                    data[memPointer] = "A9";
                                    memPointer++;
                                } else if (i == 1) {
                                    data[memPointer] = "0" + curNode.getName().toUpperCase();
                                    memPointer++;
                                } else if (i == 2) {
                                    data[memPointer] = "8D";
                                    memPointer++;
                                } else if (i == 3) {
                                    data[memPointer] = currentTempVar;
                                    memPointer++;
                                } else if (i == 4) {
                                    data[memPointer] = "XX";
                                    memPointer++;
                                }
                            }

                            numChild = 0;
                            variableNameHolder = "";

                        } else if (variableTypes.get(variableNameHolder).equals("string")) {

                            curHeapStart -= curNode.getName().length();

                            for (int i = 0; i < assignIntAddresses; i++) {
                                if (i == 0) {
                                    data[memPointer] = "A9";
                                    memPointer++;
                                } else if (i == 1) {
                                    data[memPointer] = Integer.toHexString(curHeapStart).toUpperCase();
                                    memPointer++;
                                } else if (i == 2) {
                                    data[memPointer] = "8D";
                                    memPointer++;
                                } else if (i == 3) {
                                    data[memPointer] = currentTempVar;
                                    memPointer++;
                                } else if (i == 4) {
                                    data[memPointer] = "XX";
                                    memPointer++;
                                }
                            }

                            String[] addToHeap = curNode.getName().split("");

                            for (int i = 0; i < curNode.getName().length(); i++) {
                                char a = addToHeap[i].charAt(0);
                                data[curHeapStart + i] = Integer.toHexString((int) a).toUpperCase();
                            }

                            curHeapStart--;

                            numChild = 0;
                            variableNameHolder = "";
                        } else if (variableTypes.get(variableNameHolder).equals("boolean")) {

                            if (curNode.getName().equals("true")) {
                                for (int i = 0; i < assignIntAddresses; i++) {
                                    if (i == 0) {
                                        data[memPointer] = "A9";
                                        memPointer++;
                                    } else if (i == 1) {
                                        data[memPointer] = truePointer;
                                        memPointer++;
                                    } else if (i == 2) {
                                        data[memPointer] = "8D";
                                        memPointer++;
                                    } else if (i == 3) {
                                        data[memPointer] = currentTempVar;
                                        memPointer++;
                                    } else if (i == 4) {
                                        data[memPointer] = "XX";
                                        memPointer++;
                                    }
                                }
                            } else if (curNode.getName().equals("false")) {
                                for (int i = 0; i < assignIntAddresses; i++) {
                                    if (i == 0) {
                                        data[memPointer] = "A9";
                                        memPointer++;
                                    } else if (i == 1) {
                                        data[memPointer] = falsePointer;
                                        memPointer++;
                                    } else if (i == 2) {
                                        data[memPointer] = "8D";
                                        memPointer++;
                                    } else if (i == 3) {
                                        data[memPointer] = currentTempVar;
                                        memPointer++;
                                    } else if (i == 4) {
                                        data[memPointer] = "XX";
                                        memPointer++;
                                    }
                                }
                            }

                            numChild = 0;
                            variableNameHolder = "";

                        } else {

                        }

                        if (inIfStatement) {
                            if (!jumpTable.isEmpty()) {
                                jumpTable.get(numJumps - 1).addToJump(assignIntAddresses);
                            }
                        }
                    }

                }

            } else if (curNode.getParent().getName().equals("PrintStatement")) {

                int assignPrintAddresses = 6;

                String curVarTemp = "";

                for (genTable g : variableTable) {
                    if (g.getVarName().equals(curNode.getName())) {
                        curVarTemp = g.getTempName();
                    }
                }

                if (variableTypes.get(curNode.getName()) != null) {
                    if (variableTypes.get(curNode.getName()).equals("int")) {

                        for (int i = 0; i < assignPrintAddresses; i++) {
                            if (i == 0) {
                                data[memPointer] = "A2";
                                memPointer++;
                            } else if (i == 1) {
                                data[memPointer] = "01";
                                memPointer++;
                            } else if (i == 2) {
                                data[memPointer] = "AC";
                                memPointer++;
                            } else if (i == 3) {
                                data[memPointer] = curVarTemp;
                                memPointer++;
                            } else if (i == 4) {
                                data[memPointer] = "XX";
                                memPointer++;
                            } else if (i == 5) {
                                data[memPointer] = "FF";
                                memPointer++;
                            }
                        }

                    } else if (variableTypes.get(curNode.getName()).equals("string")) {

                        for (int i = 0; i < assignPrintAddresses; i++) {
                            if (i == 0) {
                                data[memPointer] = "A2";
                                memPointer++;
                            } else if (i == 1) {
                                data[memPointer] = "02";
                                memPointer++;
                            } else if (i == 2) {
                                data[memPointer] = "AC";
                                memPointer++;
                            } else if (i == 3) {
                                data[memPointer] = curVarTemp;
                                memPointer++;
                            } else if (i == 4) {
                                data[memPointer] = "XX";
                                memPointer++;
                            } else if (i == 5) {
                                data[memPointer] = "FF";
                                memPointer++;
                            }
                        }

                    } else if (variableTypes.get(curNode.getName()).equals("boolean")) {

                        for (int i = 0; i < assignPrintAddresses; i++) {
                            if (i == 0) {
                                data[memPointer] = "A2";
                                memPointer++;
                            } else if (i == 1) {
                                data[memPointer] = "02";
                                memPointer++;
                            } else if (i == 2) {
                                data[memPointer] = "AC";
                                memPointer++;
                            } else if (i == 3) {
                                data[memPointer] = curVarTemp;
                                memPointer++;
                            } else if (i == 4) {
                                data[memPointer] = "XX";
                                memPointer++;
                            } else if (i == 5) {
                                data[memPointer] = "FF";
                                memPointer++;
                            }
                        }
                    }

                    if (inIfStatement) {
                        if (!jumpTable.isEmpty()) {
                            jumpTable.get(numJumps - 1).addToJump(assignPrintAddresses);
                        }
                    }

                } else {

                    // Case where something is printed with no variable declared
                    if (curNode.getToken().getType().equals("DIGIT")) {

                        int digitPrint = 5;

                        for (int i = 0; i < digitPrint; i++) {
                            if (i == 0) {
                                data[memPointer] = "A2";
                                memPointer++;
                            } else if (i == 1) {
                                data[memPointer] = "01";
                                memPointer++;
                            } else if (i == 2) {
                                data[memPointer] = "A0";
                                memPointer++;
                            } else if (i == 3) {
                                data[memPointer] = Integer.toHexString(Integer.parseInt(curNode.getName()))
                                        .toUpperCase();
                                memPointer++;
                            } else if (i == 4) {
                                data[memPointer] = "FF";
                                memPointer++;
                            }
                        }

                        if (inIfStatement) {
                            if (!jumpTable.isEmpty()) {
                                jumpTable.get(numJumps - 1).addToJump(digitPrint);
                            }
                        }

                    } else if (curNode.getToken().getType().equals("STRING_EXPR")) {

                        int stringExprPrint = 11;

                        curHeapStart -= curNode.getName().length();

                        genTable curVar = new genTable("T" + Integer.toString(curTempNum), curNode.getName(), 0x00,
                                curNode.getToken());
                        variableTable.add(curVar);

                        curTempNum++;

                        for (int i = 0; i < stringExprPrint; i++) {
                            if (i == 0) {
                                data[memPointer] = "A9";
                                memPointer++;
                            } else if (i == 1) {
                                data[memPointer] = Integer.toHexString(curHeapStart).toUpperCase();
                                memPointer++;
                            } else if (i == 2) {
                                data[memPointer] = "8D";
                                memPointer++;
                            } else if (i == 3) {
                                data[memPointer] = curVar.getTempName(); ////
                                memPointer++;
                            } else if (i == 4) {
                                data[memPointer] = "XX";
                                memPointer++;
                            } else if (i == 5) {
                                data[memPointer] = "A2";
                                memPointer++;
                            } else if (i == 6) {
                                data[memPointer] = "02";
                                memPointer++;
                            } else if (i == 7) {
                                data[memPointer] = "AC";
                                memPointer++;
                            } else if (i == 8) {
                                data[memPointer] = curVar.getTempName(); ////
                                memPointer++;
                            } else if (i == 9) {
                                data[memPointer] = "XX";
                                memPointer++;
                            } else if (i == 10) {
                                data[memPointer] = "FF";
                                memPointer++;
                            }
                        }

                        String[] addToHeap = curNode.getName().split("");

                        for (int i = 0; i < curNode.getName().length(); i++) {
                            char a = addToHeap[i].charAt(0);
                            data[curHeapStart + i] = Integer.toHexString((int) a).toUpperCase();
                        }

                        if (inIfStatement) {
                            if (!jumpTable.isEmpty()) {
                                jumpTable.get(numJumps - 1).addToJump(stringExprPrint);
                            }
                        }

                        //////
                    } else if (curNode.getToken().getType().equals("BOOLEAN_VALUE")) {

                        int booleanExprPrint = 11;

                        genTable curVar = new genTable("T" + Integer.toString(curTempNum), curNode.getName(), 0x00,
                                curNode.getToken());
                        variableTable.add(curVar);

                        curTempNum++;

                        if (curNode.getName().equals("true")) {
                            for (int i = 0; i < booleanExprPrint; i++) {
                                if (i == 0) {
                                    data[memPointer] = "A9";
                                    memPointer++;
                                } else if (i == 1) {
                                    data[memPointer] = truePointer;
                                    memPointer++;
                                } else if (i == 2) {
                                    data[memPointer] = "8D";
                                    memPointer++;
                                } else if (i == 3) {
                                    data[memPointer] = curVar.getTempName(); ////
                                    memPointer++;
                                } else if (i == 4) {
                                    data[memPointer] = "XX";
                                    memPointer++;
                                } else if (i == 5) {
                                    data[memPointer] = "A2";
                                    memPointer++;
                                } else if (i == 6) {
                                    data[memPointer] = "02";
                                    memPointer++;
                                } else if (i == 7) {
                                    data[memPointer] = "AC";
                                    memPointer++;
                                } else if (i == 8) {
                                    data[memPointer] = curVar.getTempName(); ////
                                    memPointer++;
                                } else if (i == 9) {
                                    data[memPointer] = "XX";
                                    memPointer++;
                                } else if (i == 10) {
                                    data[memPointer] = "FF";
                                    memPointer++;
                                }
                            }

                        } else if (curNode.getName().equals("false")) {
                            for (int i = 0; i < booleanExprPrint; i++) {
                                if (i == 0) {
                                    data[memPointer] = "A9";
                                    memPointer++;
                                } else if (i == 1) {
                                    data[memPointer] = falsePointer;
                                    memPointer++;
                                } else if (i == 2) {
                                    data[memPointer] = "8D";
                                    memPointer++;
                                } else if (i == 3) {
                                    data[memPointer] = curVar.getTempName(); ////
                                    memPointer++;
                                } else if (i == 4) {
                                    data[memPointer] = "XX";
                                    memPointer++;
                                } else if (i == 5) {
                                    data[memPointer] = "A2";
                                    memPointer++;
                                } else if (i == 6) {
                                    data[memPointer] = "02";
                                    memPointer++;
                                } else if (i == 7) {
                                    data[memPointer] = "AC";
                                    memPointer++;
                                } else if (i == 8) {
                                    data[memPointer] = curVar.getTempName(); ////
                                    memPointer++;
                                } else if (i == 9) {
                                    data[memPointer] = "XX";
                                    memPointer++;
                                } else if (i == 10) {
                                    data[memPointer] = "FF";
                                    memPointer++;
                                }
                            }
                        }

                        if (inIfStatement) {
                            if (!jumpTable.isEmpty()) {
                                jumpTable.get(numJumps - 1).addToJump(booleanExprPrint);
                            }
                        }

                    }
                }

            } else if (curNode.getParent().getName().equals("IfStatement")) {

                ifDepth = depth - 1;

                inIfStatement = true;

                int ifIterOne = 3;

                int ifIterTwo = 5;

                String ifTempHolder = "";

                if (numChild == 0) {

                    for (genTable g : variableTable) {
                        if (g.getVarName().equals(curNode.getName())) {
                            ifTempHolder = g.getTempName();
                        }
                    }

                    for (int i = 0; i < ifIterOne; i++) {
                        if (i == 0) {
                            data[memPointer] = "AE";
                            memPointer++;
                        } else if (i == 1) {
                            data[memPointer] = ifTempHolder;
                            memPointer++;
                        } else if (i == 2) {
                            data[memPointer] = "XX";
                            memPointer++;
                        }
                    }

                    numChild++;

                } else if (numChild == 1) {

                    genTable jumpVar = new genTable("J" + Integer.toString(numJumps), 0);
                    jumpTable.add(jumpVar);
                    numJumps++;

                    for (genTable g : variableTable) {
                        if (g.getVarName().equals(curNode.getName())) {
                            ifTempHolder = g.getTempName();
                        }
                    }

                    for (int i = 0; i < ifIterTwo; i++) {
                        if (i == 0) {
                            data[memPointer] = "EC";
                            memPointer++;
                        } else if (i == 1) {
                            data[memPointer] = ifTempHolder;
                            memPointer++;
                        } else if (i == 2) {
                            data[memPointer] = "XX";
                            memPointer++;
                        } else if (i == 3) {
                            data[memPointer] = "D0";
                            memPointer++;
                        } else if (i == 4) {
                            data[memPointer] = jumpVar.getTempName();
                            memPointer++;
                        }
                    }

                    numChild++;

                } else {
                    numChild = 0;
                }

            } else if (curNode.getParent().getName().equals("WhileStatement")) {

            }

        } else {
            // Children present so show interior branches

            // Recursion loop [FUN] :)
            for (int i = 0; i < curNode.getChildren().size(); i++) {
                iterateTreeRecur(curNode.getChildren().get(i), depth + 1, fullScope);
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
