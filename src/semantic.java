import java.util.ArrayList;
import objects.parseTree;
import objects.token;

public class semantic {
    // Main parse function for the AST parseTree
    parseTree currTree = new parseTree();
    ArrayList<token> tokens = new ArrayList<token>();
    int currTokenInArray = 0;
    boolean debug = true;

    public parseTree startSemantic(ArrayList<token> tokenList) {
        tokens = tokenList;
        semanticProgram();
        tokens = new ArrayList<token>();
        currTokenInArray = 0;
        return currTree;
    }

    public void match(String expected, boolean inAST) {
        if (expected.equals(tokens.get(currTokenInArray).getValue())) {
            if (inAST) {
                currTree.addNode(expected, true);
            }
            currTokenInArray++;
        } else {
            createError(expected, tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
    }

    public void matchRegEx(String regEx, boolean inAST) {
        if (tokens.get(currTokenInArray).getValue().matches(regEx)) {
            currTree.addNode(tokens.get(currTokenInArray).getValue(), true);
            currTokenInArray++;
        } else {
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

    public void semanticSpace() {
        if (debug) {
            createDebug("semanticSpace()");
        }
        match(" ", false);
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
        currTree.executeOrder66();
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
        currTree.executeOrder66();
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

    // Creates a debug message
    public void createDebug(String message) {
        System.out.println("DEBUG Semantic - " + message);
    }

    public void createInfo(String message) {
        System.out.println("INFO Semantic - " + message);
    }
}
