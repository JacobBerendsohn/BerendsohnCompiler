import java.util.ArrayList;
import objects.parseTree;
import objects.scope;
import objects.token;
import objects.node;

public class semantic {
    // Main parse function for the AST parseTree
    parseTree currTree = new parseTree();
    ArrayList<token> tokens = new ArrayList<token>();
    int currTokenInArray = 0;
    boolean debug = true;
    int errorCount = 0;

    public parseTree startSemantic(ArrayList<token> tokenList) {
        tokens = tokenList;
        semanticProgram();
        parseTree fullScope = scopeCheck(currTree.getRootNode(), 0);
        System.out.println("\n ---------------SYMBOL TABLE--------------- \n");
        System.out.println(fullScope.printSymbolTable());
        tokens = new ArrayList<token>();
        currTokenInArray = 0;
        return currTree;
    }

    public void match(String expected, boolean inAST) {
        if (expected.equals(tokens.get(currTokenInArray).getValue())) {
            if (inAST) {
                currTree.addNode(expected, true);

                if (currTree.getCurrentNode().getName().equals("AssignmentStatement")
                        || currTree.getCurrentNode().getName().equals("VarDecl")) {

                    if (currTree.getCurrentNode().getChildren().get(0).getName().equals(expected)) {

                        currTree.getCurrentNode().getChildren().get(0).addLeafToken(tokens.get(
                                currTokenInArray));

                    } else if (currTree.getCurrentNode().getChildren().get(1).getName().equals(expected)) {

                        currTree.getCurrentNode().getChildren().get(1).addLeafToken(tokens.get(
                                currTokenInArray));
                    }
                } else if (currTree.getCurrentNode().getName().equals("BooleanExpr")
                        || currTree.getCurrentNode().getName().equals("WhileStatement")
                        || currTree.getCurrentNode().getName().equals("IfStatement")) {
                    for (node n : currTree.getCurrentNode().getChildren()) {
                        if (n.getName().equals(expected)) {
                            n.addLeafToken(tokens.get(currTokenInArray));
                        }
                    }
                } else {
                    currTree.getCurrentNode().getChildren().get(0).addLeafToken(tokens.get(
                            currTokenInArray));
                }

            }

            currTokenInArray++;

        } else {
            System.out.println("match");
            createError(expected, tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
    }

    public void matchRegEx(String regEx, boolean inAST) {
        if (tokens.get(currTokenInArray).getValue().matches(regEx)) {

            if (inAST) {
                currTree.addNode(tokens.get(currTokenInArray).getValue(), true);

                if (currTree.getCurrentNode().getName().equals("AssignmentStatement")
                        || currTree.getCurrentNode().getName().equals("VarDecl")
                        || currTree.getCurrentNode().getName().equals("BooleanExpr")) {

                    if (currTree.getCurrentNode().getChildren().get(0).getName().matches(regEx) &&
                            currTree.getCurrentNode().getChildren().get(0)
                                    .getName().equals(tokens.get(currTokenInArray).getValue())
                            && currTree.getCurrentNode().getChildren().get(0).getToken() == null) {

                        currTree.getCurrentNode().getChildren().get(0).addLeafToken(tokens.get(
                                currTokenInArray));

                    } else if (currTree.getCurrentNode().getChildren().get(1).getName().matches(regEx) &&
                            currTree.getCurrentNode().getChildren().get(1).getName()
                                    .equals(tokens.get(currTokenInArray).getValue())) {

                        currTree.getCurrentNode().getChildren().get(1).addLeafToken(tokens.get(
                                currTokenInArray));
                    }
                } else {
                    currTree.getCurrentNode().getChildren().get(0).addLeafToken(tokens.get(
                            currTokenInArray));
                }

            }

            currTokenInArray++;

        } else {
            System.out.println("matchRegEx");
            createError(regEx, tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());

        }
    }

    public void semanticProgram() {
        if (debug) {
            createDebug("semanticProgram()");
        }
        semanticBlock();
        match("$", false);
        currTree.executeOrder66();
    }

    public void semanticBlock() {
        if (debug) {
            createDebug("semanticBlock()");
        }
        currTree.addNode("Block", false);
        match("{", false);
        semanticStatementList();
        match("}", false);
        currTree.executeOrder66();
    }

    public void semanticStatementList() {
        try {
            if (debug) {
                createDebug("semanticStatementList()");
            }
            // Checking if there is a statement and if not, make it an empty string
            if (tokens.get(currTokenInArray).getValue().equals("print")
                    || (tokens.get(currTokenInArray).getType().equals("ID")
                            && tokens.get(currTokenInArray + 1).getType().equals("ASSIGN"))
                    || (tokens.get(currTokenInArray + 1).getType().equals("ID")
                            && (tokens.get(currTokenInArray).getValue().equals("int")
                                    || tokens.get(currTokenInArray).getValue().equals("string")
                                    || tokens.get(currTokenInArray).getValue().equals("boolean")))
                    || tokens.get(currTokenInArray).getValue().equals("while")
                    || tokens.get(currTokenInArray).getValue().equals("if")
                    || tokens.get(currTokenInArray).getValue().equals("{")) {
                semanticStatement();
                semanticStatementList();
            } else {
                // Empty String handling
            }
            // currTree.executeOrder66();
        } catch (Exception e) {

        }

    }

    public void semanticStatement() {
        if (debug) {
            createDebug("semanticStatement()");
        }
        if (tokens.get(currTokenInArray).getValue().equals("print")) {
            semanticPrintStatement();
        } else if (tokens.get(currTokenInArray).getType().equals("ID")
                && tokens.get(currTokenInArray + 1).getType().equals("ASSIGN")) {
            semanticAssignmentStatement();
        } else if (tokens.get(currTokenInArray + 1).getType().equals("ID")
                && (tokens.get(currTokenInArray).getValue().equals("int")
                        || tokens.get(currTokenInArray).getValue().equals("string")
                        || tokens.get(currTokenInArray).getValue().equals("boolean"))) {
            semanticVarDecl();
        } else if (tokens.get(currTokenInArray).getValue().equals("while")) {
            semanticWhileStatement();
        } else if (tokens.get(currTokenInArray).getValue().equals("if")) {
            semanticIfStatement();
        } else if (tokens.get(currTokenInArray).getValue().equals("{")) {
            semanticBlock();
        } else {
            createError("[STATEMENT]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticPrintStatement() {
        if (debug) {
            createDebug("semanticPrintStatement()");
        }
        currTree.addNode("PrintStatement", false);
        match("print", false);
        match("(", false);
        semanticExpr();
        match(")", false);
        currTree.executeOrder66();
    }

    public void semanticAssignmentStatement() {
        if (debug) {
            createDebug("semanticAssignmentStatement()");
        }
        currTree.addNode("AssignmentStatement", false);
        semanticID();
        match("=", false);
        semanticExpr();
        currTree.executeOrder66();
    }

    // Separate the VarDecl for assignment statements in a statement list
    public void semanticVarDecl() {
        if (debug) {
            createDebug("semanticVarDecl()");
        }
        currTree.addNode("VarDecl", false);
        semanticType();
        semanticID();
        currTree.executeOrder66();
    }

    public void semanticWhileStatement() {
        if (debug) {
            createDebug("semanticWhileStatement()");
        }
        currTree.addNode("WhileStatement", false);
        match("while", false);
        semanticBooleanExpr();
        semanticBlock();
        currTree.executeOrder66();
    }

    public void semanticIfStatement() {
        if (debug) {
            createDebug("semanticIfStatement()");
        }
        currTree.addNode("IfStatement", false);
        match("if", false);
        semanticBooleanExpr();
        semanticBlock();
        currTree.executeOrder66();
    }

    public void semanticExpr() {
        if (debug) {
            createDebug("semanticExpr()");
        }
        if (tokens.get(currTokenInArray).getType().equals("DIGIT")) {
            semanticIntExpr();
            // Checking for a quote
        } else if (tokens.get(currTokenInArray).getValue().split("")[0].equals("\"")) {
            semanticStringExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("BOOLEAN_VALUE")
                || tokens.get(currTokenInArray).getValue().equals("(")) {
            semanticBooleanExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("ID")) {
            semanticID();
        } else {
            createError("[EXPR]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticIntExpr() {
        if (debug) {
            createDebug("semanticIntExpr()");
        }
        if (tokens.get(currTokenInArray).getType().equals("DIGIT")
                && tokens.get(currTokenInArray + 1).getValue().equals("+")) {
            semanticDigit();
            semanticIntOp();
            semanticExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("DIGIT")) {
            semanticDigit();
        } else {
            createError("[EXPR_INT]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticStringExpr() {
        if (debug) {
            createDebug("semanticStringExpr()");
        }
        if (tokens.get(currTokenInArray).getValue().equals("\"")) {
            match("\"", false);
            semanticCharList();
            match("\"", false);
        } else {
            createError("[EXPR_STRING]", tokens.get(currTokenInArray).getType(),
                    tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
    }

    public void semanticBooleanExpr() {
        if (debug) {
            createDebug("semanticBooleanExpr()");
        }
        // currTree.addNode("BooleanExpr", false);
        if (tokens.get(currTokenInArray).getValue().equals("(")) {
            match("(", false);
            semanticExpr();
            semanticBoolOp();
            semanticExpr();
            match(")", false);
        } else if (tokens.get(currTokenInArray).getValue().equals("true")
                || tokens.get(currTokenInArray).getValue().equals("false")) {
            semanticBoolVal();
        } else {
            createError("[EXPR_BOOLEAN]", tokens.get(currTokenInArray).getType(),
                    tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticID() {
        if (debug) {
            createDebug("semanticID()");
        }
        semanticChar();
        // currTree.executeOrder66();
    }

    public void semanticCharList() {
        if (debug) {
            createDebug("semanticCharList()");
        }
        if (!tokens.get(currTokenInArray).getValue().equals("")) {
            matchRegEx("[a-z ]+", true);
        } else {
            match("", false);
        }

        // currTree.executeOrder66();
    }

    public void semanticType() {
        if (debug) {
            createDebug("semanticType()");
        }
        if (tokens.get(currTokenInArray).getValue().equals("int")) {
            match("int", true);
        } else if (tokens.get(currTokenInArray).getValue().equals("string")) {
            match("string", true);
        } else if (tokens.get(currTokenInArray).getValue().equals("boolean")) {
            match("boolean", true);
        } else {
            createError("[TYPE]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticChar() {
        if (debug) {
            createDebug("semanticChar()");
        }
        // Using regEx instead of a bunch of ifs to see if token is in alphabet
        if (tokens.get(currTokenInArray).getValue().matches("[a-z]+")) {
            matchRegEx("[a-z]+", true);
        } else {
            createError("[ [a-z]+ ]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticDigit() {
        if (debug) {
            createDebug("semanticDigit()");
        }
        // Using regEx instead of a bunch of ifs to see if token is a digit
        if (tokens.get(currTokenInArray).getValue().matches("[0-9]+")) {
            matchRegEx("[0-9]+", true);
        } else {
            createError("[ [0-9]+ ]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticBoolOp() {
        if (debug) {
            createDebug("semanticBoolOp()");
        }
        if (tokens.get(currTokenInArray).getValue().equals("==")) {
            match("==", false);
        } else if (tokens.get(currTokenInArray).getValue().equals("!=")) {
            match("!=", false);
        } else {
            createError("[BOOL_OP]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticBoolVal() {
        if (debug) {
            createDebug("semanticBoolVal()");
        }
        if (tokens.get(currTokenInArray).getValue().equals("true")) {
            match("true", true);
        } else if (tokens.get(currTokenInArray).getValue().equals("false")) {
            match("false", true);
        } else {
            createError("[BOOL_VAL]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        // currTree.executeOrder66();
    }

    public void semanticIntOp() {
        if (debug) {
            createDebug("semanticIntOp()");
        }
        match("+", false);
        // currTree.executeOrder66();
    }

    // Creates an error to display and stop parse
    public void createError(String expected, String read, String value, String line) {
        currTree.setError(true);
        System.out.println(
                "ERROR Semantic - Expected: " + expected + " Read: " + read + " with value " + value + " on line: "
                        + line);
    }

    // Traverses Tree in order
    parseTree scopeTree = new parseTree();
    node currentScope = null;
    node checkParent = null;
    String typeHolder = "";
    String varNameHolder = "";
    int scopeCounter = 0;
    int numChild = 0;

    public parseTree scopeCheck(node curNode, int depth) {

        // Checking if we need a new scope because of a block
        // and adding scope to the scope tree
        if (curNode.getName().equals("Block") && currentScope == null) {
            scopeTree.addNode("Scope " + Integer.toString(scopeCounter), false);
            currentScope = scopeTree.getCurrentNode();
            scopeCounter++;
        } else if (curNode.getName().equals("Block") && currentScope != null && !curNode.isRoot()) {
            scopeTree.executeOrder66();
            scopeTree.addNode("Scope " + Integer.toString(scopeCounter), false);
            currentScope = scopeTree.getCurrentNode();
            scopeCounter++;
        } else if (curNode.getName().equals("Block") && currentScope != null) {
            scopeTree.addNode("Scope " + Integer.toString(scopeCounter), false);
            currentScope = scopeTree.getCurrentNode();
            scopeCounter++;
        } else {

        }

        // Checking if the current node is a leaf and shares a parent with the last node
        // traversed
        // Checking for leaf nodes
        if (curNode.getChildren().isEmpty()) {

            // For variable declaration we add a new scope
            if (curNode.getParent().getName().equals("VarDecl")) {
                if (numChild == 0) {

                    typeHolder = curNode.getName();
                    checkParent = curNode.getParent();
                    numChild++;

                } else if (numChild == 1) {
                    // Checking if the variable already exists
                    if (scopeTree.getCurrentNode().isScopeEmpty()) {
                        scope newScope = new scope(curNode.getName(), typeHolder,
                                Integer.toString(scopeCounter),
                                curNode.getToken().getLine(), true, false);

                        scopeTree.getCurrentNode().addScope(curNode.getName(), newScope);
                        numChild = 0;
                    } else if (scopeTree.getCurrentNode().getScope(curNode.getName()) == null) {
                        scope newScope = new scope(curNode.getName(), typeHolder,
                                Integer.toString(scopeCounter),
                                curNode.getToken().getLine(), true, false);

                        scopeTree.getCurrentNode().addScope(curNode.getName(), newScope);
                        numChild = 0;
                    } else {
                        numChild = 0;
                        createError("Variable with the name: " + curNode.getName() +
                                " at line " + scopeTree.getCurrentNode().getScope(curNode.getName()).getPosition()
                                + " already exists (VarDecl)");
                    }

                }
                // For assignment statement we just check that the correct type is being used
                // for the entry already in scope
            } else if (curNode.getParent().getName().equals("AssignmentStatement")) {
                if (numChild == 0) {

                    // Checking if current scope is empty
                    if (scopeTree.getCurrentNode().isScopeEmpty()) {
                        node placeholderNode = scopeTree.getCurrentNode().getParent();
                        if (!scopeTree.getCurrentNode().isRoot()) {
                            for (node n : placeholderNode.getChildren()) {
                                if (n.getScope(curNode.getName()) != null) {
                                    varNameHolder = curNode.getName();
                                    numChild++;
                                }
                            }
                        } else {
                            createError(curNode.getName() + " on line: "
                                    + curNode.getToken().getLine()
                                    + " has not been initialized (Assignment)");
                        }

                        // Checking if current scope contains needed variable
                    } else if (scopeTree.getCurrentNode().getScope(curNode.getName()) != null) {
                        varNameHolder = curNode.getName();
                        numChild++;

                        // Checking for current scope not empty and still does not contain needed
                        // variable
                    } else if (scopeTree.getCurrentNode().getScope(curNode.getName()) == null) {
                        node placeholderNode = scopeTree.getCurrentNode().getParent();

                        for (node n : placeholderNode.getChildren()) {
                            if (n.getScope(curNode.getName()) != null) {
                                varNameHolder = curNode.getName();
                                numChild++;
                            }
                        }

                    } else {
                        createError("Variable: " + curNode.getName() + " on line: "
                                + scopeTree.getCurrentNode().getScope(curNode.getName()).getPosition()
                                + " has not been initialized (Assignment)");
                    }

                } else {
                    numChild = 0;
                    node phNode = curNode.getParent();

                    // Case where the current scope has no info
                    if (scopeTree.getCurrentNode().isScopeEmpty()) {
                        node placeholderNode = scopeTree.getCurrentNode().getParent();

                        for (node n : placeholderNode.getChildren()) {
                            if (n.getScope(curNode.getName()) != null) {
                                if (curNode.getToken().getType().equals("ID")) {
                                    // Making sure the variables being assigned are of the same type
                                    if (placeholderNode.getScope(curNode.getName()).isInit()
                                            && placeholderNode.getScope(curNode.getName()).getType()
                                                    .equals(placeholderNode.getScope(varNameHolder).getType())) {
                                        numChild = 0;
                                    } else {
                                        numChild = 0;
                                        createError("Type mismatch error for variable: " + curNode.getName()
                                                + " on line: "
                                                + placeholderNode.getScope(curNode.getName()).getPosition() + " (ID)");
                                    }
                                } else {
                                    if (placeholderNode.getScope(varNameHolder).getType().equals("int")) {
                                        String testNum = curNode.getName();
                                        if (Integer.parseInt(testNum) == 0 || Integer.parseInt(testNum) == 1
                                                || Integer.parseInt(testNum) == 2 || Integer.parseInt(testNum) == 3
                                                || Integer.parseInt(testNum) == 4 || Integer.parseInt(testNum) == 5
                                                || Integer.parseInt(testNum) == 6 || Integer.parseInt(testNum) == 7
                                                || Integer.parseInt(testNum) == 8 || Integer.parseInt(testNum) == 9) {
                                            numChild = 0;
                                        } else {
                                            numChild = 0;
                                            createError("Type mismatch, expected int on line: "
                                                    + curNode.getToken().getLine());
                                        }
                                    } else if (placeholderNode.getScope(varNameHolder).getType().equals("string")) {
                                        String testString = curNode.getName();
                                        String regEx = "[a-z]+";
                                        if (testString.matches(regEx)) {
                                            numChild = 0;
                                        } else {
                                            numChild = 0;
                                            createError("Type mismatch, expected String on line: "
                                                    + curNode.getToken().getLine());
                                        }

                                    } else if (placeholderNode.getScope(varNameHolder).getType().equals("boolean")) {
                                        if (curNode.getName().equals("true") || curNode.getName().equals("false")) {
                                            numChild = 0;
                                        } else {
                                            numChild = 0;
                                            createError("Type mismatch, expected boolean on line: "
                                                    + curNode.getToken().getLine());
                                        }
                                    }
                                }
                            }
                        }

                    } else if (scopeTree.getCurrentNode().getScope(curNode.getName()) != null
                            || scopeTree.getCurrentNode().getScope(phNode.getChildren().get(0).getName()) != null) {

                        // Checking if the current node is another variable or a type
                        if (curNode.getToken().getType().equals("ID")) {
                            // Making sure the variables being assigned are of the same type
                            if (scopeTree.getCurrentNode().getScope(curNode.getName()).isInit()
                                    && scopeTree.getCurrentNode().getScope(curNode.getName()).getType()
                                            .equals(scopeTree.getCurrentNode().getScope(varNameHolder).getType())) {
                                numChild = 0;
                            } else {
                                numChild = 0;
                                createError("Type mismatch error for variable: " + curNode.getName() + " on line: "
                                        + scopeTree.getCurrentNode().getScope(curNode.getName()).getPosition()
                                        + " (ID)");
                            }
                        } else {
                            if (scopeTree.getCurrentNode().getScope(varNameHolder).getType().equals("int")) {
                                String testNum = curNode.getName();
                                try {
                                    if (Integer.parseInt(testNum) == 0 || Integer.parseInt(testNum) == 1
                                            || Integer.parseInt(testNum) == 2 || Integer.parseInt(testNum) == 3
                                            || Integer.parseInt(testNum) == 4 || Integer.parseInt(testNum) == 5
                                            || Integer.parseInt(testNum) == 6 || Integer.parseInt(testNum) == 7
                                            || Integer.parseInt(testNum) == 8 || Integer.parseInt(testNum) == 9) {
                                        numChild = 0;
                                    } else {

                                    }
                                } catch (NumberFormatException e) {
                                    numChild = 0;
                                    createError("Type mismatch, expected int on line: "
                                            + curNode.getToken().getLine());
                                }

                            } else if (scopeTree.getCurrentNode().getScope(varNameHolder).getType().equals("string")) {
                                String testString = curNode.getName();
                                String regEx = "[a-z]+";
                                if (testString.matches(regEx)) {
                                    numChild = 0;
                                } else {
                                    numChild = 0;
                                    createError("Type mismatch, expected String on line: "
                                            + curNode.getToken().getLine());
                                }

                            } else if (scopeTree.getCurrentNode().getScope(varNameHolder).getType().equals("boolean")) {
                                if (curNode.getName().equals("true") || curNode.getName().equals("false")) {
                                    numChild = 0;
                                } else {
                                    numChild = 0;
                                    createError("Type mismatch, expected boolean on line: "
                                            + curNode.getToken().getLine());
                                }
                            }
                        }

                    } else if ((scopeTree.getCurrentNode().getScope(curNode.getName()) == null
                            && scopeTree.getCurrentNode().getParent() != null)
                            || (scopeTree.getCurrentNode().getScope(phNode.getChildren().get(0).getName()) != null
                                    && scopeTree.getCurrentNode().getParent() != null)) {
                        node placeholderNode = scopeTree.getCurrentNode().getParent();

                        for (node n : placeholderNode.getChildren()) {
                            if (n.getScope(curNode.getName()) != null) {
                                if (curNode.getToken().getType().equals("ID")) {
                                    // Making sure the variables being assigned are of the same type
                                    if (placeholderNode.getScope(curNode.getName()).isInit()
                                            && placeholderNode.getScope(curNode.getName()).getType()
                                                    .equals(placeholderNode.getScope(varNameHolder).getType())) {
                                        numChild = 0;
                                    } else {
                                        numChild = 0;
                                        createError("Type mismatch error for variable: " + curNode.getName()
                                                + " on line: "
                                                + placeholderNode.getScope(curNode.getName()).getPosition() + " (ID)");
                                    }
                                } else {
                                    if (placeholderNode.getScope(varNameHolder).getType().equals("int")) {
                                        String testNum = curNode.getName();
                                        if (Integer.parseInt(testNum) == 0 || Integer.parseInt(testNum) == 1
                                                || Integer.parseInt(testNum) == 2 || Integer.parseInt(testNum) == 3
                                                || Integer.parseInt(testNum) == 4 || Integer.parseInt(testNum) == 5
                                                || Integer.parseInt(testNum) == 6 || Integer.parseInt(testNum) == 7
                                                || Integer.parseInt(testNum) == 8 || Integer.parseInt(testNum) == 9) {
                                            numChild = 0;
                                        } else {
                                            numChild = 0;
                                            createError("Type mismatch, expected int on line: "
                                                    + curNode.getToken().getLine());
                                        }
                                    } else if (placeholderNode.getScope(varNameHolder).getType().equals("string")) {
                                        String testString = curNode.getName();
                                        String regEx = "[a-z]+";
                                        if (testString.matches(regEx)) {
                                            numChild = 0;
                                        } else {
                                            numChild = 0;
                                            createError("Type mismatch, expected String on line: "
                                                    + curNode.getToken().getLine());
                                        }

                                    } else if (placeholderNode.getScope(varNameHolder).getType().equals("boolean")) {
                                        if (curNode.getName().equals("true") || curNode.getName().equals("false")) {
                                            numChild = 0;
                                        } else {
                                            numChild = 0;
                                            createError("Type mismatch, expected boolean on line: "
                                                    + curNode.getToken().getLine());
                                        }
                                    }
                                }
                            }
                        }

                    } else {

                        createError(
                                "Type mismatch, expected "
                                        + scopeTree.getCurrentNode().getScope(varNameHolder).getType() + " on line: "
                                        + curNode.getToken().getLine());

                    }

                }

            } else if (curNode.getParent().getName().equals("PrintStatement")) {

            } else if (curNode.getParent().getName().equals("IfStatement")) {
                if (numChild == 0) {
                    numChild++;
                    varNameHolder = curNode.getName();
                } else {
                    if (scopeTree.getCurrentNode().getScope(varNameHolder).isUsed()) {
                        if (scopeTree.getCurrentNode().getScope(varNameHolder).getType()
                                .equals(curNode.getToken().getType())) {
                            numChild = 0;
                        } else {
                            createError("Type mismatch, expected "
                                    + scopeTree.getCurrentNode().getScope(varNameHolder).getType() + " on line: "
                                    + curNode.getToken().getLine());
                        }
                    } else {
                        createError("Variable: " + varNameHolder + "at line: " + curNode.getToken().getLine()
                                + "is not used but is being called");
                    }
                }
            } else if (curNode.getParent().getName().equals("WhileStatement")) {
                if (numChild == 0) {
                    numChild++;
                    varNameHolder = curNode.getName();
                } else {
                    if (scopeTree.getCurrentNode().getScope(varNameHolder).isUsed()) {
                        if (scopeTree.getCurrentNode().getScope(varNameHolder).getType()
                                .equals(curNode.getToken().getType())) {
                            numChild = 0;
                        } else {
                            createError("Type mismatch, expected "
                                    + scopeTree.getCurrentNode().getScope(varNameHolder).getType() + " on line: "
                                    + curNode.getToken().getLine());
                        }
                    } else {
                        createError("Variable: " + varNameHolder + "at line: " + curNode.getToken().getLine()
                                + "is not used but is being called");
                    }
                }
            }

        } else {
            // Children present so show interior branches

            // Recursion loop [FUN] :)
            for (int i = 0; i < curNode.getChildren().size(); i++) {
                scopeCheck(curNode.getChildren().get(i), depth + 1);
            }
        }

        return scopeTree;
    }

    // Creates a debug message
    public void createDebug(String message) {
        System.out.println("DEBUG Semantic - " + message);
    }

    public void createInfo(String message) {
        System.out.println("INFO Semantic - " + message);
    }

    public void createError(String message) {
        currTree.setError(true);
        System.out.println("ERROR Semantic - " + message);
        return;
    }
}
