import java.util.ArrayList;

import objects.parseTree;
import objects.token;

public class parser {
    // Main parse function
    parseTree currTree = new parseTree();
    ArrayList<token> tokens = new ArrayList<token>();
    int currTokenInArray = 0;
    boolean debug = true;

    int prCt = 0;

    public parseTree startParse(ArrayList<token> tokenList) {
        tokens = tokenList;
        parseProgram();
        tokens = new ArrayList<token>();
        currTokenInArray = 0;
        return currTree;
    }

    public void match(String expected) {
        if (expected.equals(tokens.get(currTokenInArray).getValue())) {
            currTree.addNode(expected, true);
            currTokenInArray++;
        } else {
            createError(expected, tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
    }

    public void matchRegEx(String regEx) {
        if (tokens.get(currTokenInArray).getValue().matches(regEx)) {
            currTree.addNode(tokens.get(currTokenInArray).getValue(), true);
            currTokenInArray++;
        } else {
            createError(regEx, tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());

        }
    }

    public void parseProgram() {
        if (debug) {
            createDebug("parseProgram()");
        }
        currTree.addNode("Program", false);
        prCt++;
        parseBlock();
        match("$");
        currTree.executeOrder66();
    }

    public void parseBlock() {
        if (debug) {
            createDebug("parseBlock()");
        }
        currTree.addNode("Block", false);
        match("{");
        parseStatementList();
        match("}");
        currTree.executeOrder66();
    }

    public void parseStatementList() {
        try {
            if (debug) {
                createDebug("parseStatementList()");
            }
            currTree.addNode("StatementList", false);
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
                parseStatement();
                parseStatementList();
            } else {
                // Empty String handling
            }
            currTree.executeOrder66();
        } catch (Exception e) {

        }

    }

    public void parseStatement() {
        if (debug) {
            createDebug("parseStatement()");
        }
        currTree.addNode("Statement", false);
        if (tokens.get(currTokenInArray).getValue().equals("print")) {
            parsePrintStatement();
        } else if (tokens.get(currTokenInArray).getType().equals("ID")
                && tokens.get(currTokenInArray + 1).getType().equals("ASSIGN")) {
            parseAssignmentStatement();
        } else if (tokens.get(currTokenInArray + 1).getType().equals("ID")
                && (tokens.get(currTokenInArray).getValue().equals("int")
                        || tokens.get(currTokenInArray).getValue().equals("string")
                        || tokens.get(currTokenInArray).getValue().equals("boolean"))) {
            parseVarDecl();
        } else if (tokens.get(currTokenInArray).getValue().equals("while")) {
            parseWhileStatement();
        } else if (tokens.get(currTokenInArray).getValue().equals("if")) {
            parseIfStatement();
        } else if (tokens.get(currTokenInArray).getValue().equals("{")) {
            parseBlock();
        } else {
            createError("[STATEMENT]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parsePrintStatement() {
        if (debug) {
            createDebug("parsePrintStatement()");
        }
        currTree.addNode("PrintStatement", false);
        match("print");
        match("(");
        parseExpr();
        match(")");
        currTree.executeOrder66();
    }

    public void parseAssignmentStatement() {
        if (debug) {
            createDebug("parseAssignmentStatement()");
        }
        currTree.addNode("AssignmentStatement", false);
        parseID();
        match("=");
        parseExpr();
        currTree.executeOrder66();
    }

    // Separate the VarDecl for assignment statements in a statement list
    public void parseVarDecl() {
        if (debug) {
            createDebug("parseVarDecl()");
        }
        currTree.addNode("VarDecl", false);
        parseType();
        parseID();
        currTree.executeOrder66();
    }

    public void parseWhileStatement() {
        if (debug) {
            createDebug("parseWhileStatement()");
        }
        currTree.addNode("WhileStatement", false);
        match("while");
        parseBooleanExpr();
        parseBlock();
        currTree.executeOrder66();
    }

    public void parseIfStatement() {
        if (debug) {
            createDebug("parseIfStatement()");
        }
        currTree.addNode("IfStatement", false);
        match("if");
        parseBooleanExpr();
        parseBlock();
        currTree.executeOrder66();
    }

    public void parseExpr() {
        if (debug) {
            createDebug("parseExpr()");
        }
        currTree.addNode("Expr", false);
        if (tokens.get(currTokenInArray).getType().equals("DIGIT")) {
            parseIntExpr();
            // Checking for a quote
        } else if (tokens.get(currTokenInArray).getValue().split("")[0].equals("\"")) {
            parseStringExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("BOOLEAN_VALUE")
                || tokens.get(currTokenInArray).getValue().equals("(")) {
            parseBooleanExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("ID")) {
            parseID();
        } else {
            createError("[EXPR]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseIntExpr() {
        if (debug) {
            createDebug("parseIntExpr()");
        }
        currTree.addNode("IntExpr", false);
        if (tokens.get(currTokenInArray).getType().equals("DIGIT")
                && tokens.get(currTokenInArray + 1).getValue().equals("+")) {
            parseDigit();
            parseIntOp();
            parseExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("DIGIT")) {
            parseDigit();
        } else {
            createError("[EXPR_INT]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseStringExpr() {
        if (debug) {
            createDebug("parseStringExpr()");
        }
        currTree.addNode("StringExpr", false);
        if (tokens.get(currTokenInArray).getValue().equals("\"")) {
            match("\"");
            parseCharList();
            match("\"");
        } else {
            createError("[EXPR_STRING]", tokens.get(currTokenInArray).getType(),
                    tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
    }

    public void parseBooleanExpr() {
        if (debug) {
            createDebug("parseBooleanExpr()");
        }
        currTree.addNode("BooleanExpr", false);
        if (tokens.get(currTokenInArray).getValue().equals("(")) {
            match("(");
            parseExpr();
            parseBoolOp();
            parseExpr();
            match(")");
        } else if (tokens.get(currTokenInArray).getValue().equals("true")
                || tokens.get(currTokenInArray).getValue().equals("false")) {
            parseBoolVal();
        } else {
            createError("[EXPR_BOOLEAN]", tokens.get(currTokenInArray).getType(),
                    tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseID() {
        if (debug) {
            createDebug("parseID()");
        }
        currTree.addNode("ID", false);
        parseChar();
        currTree.executeOrder66();
    }

    public void parseCharList() {
        if (debug) {
            createDebug("parseCharList()");
        }
        currTree.addNode("CharList", false);
        if (!tokens.get(currTokenInArray).getValue().equals("")) {
            matchRegEx("[a-z ]+");
        } else {
            match("");
        }

        currTree.executeOrder66();
    }

    public void parseType() {
        if (debug) {
            createDebug("parseType()");
        }
        currTree.addNode("Type", false);
        if (tokens.get(currTokenInArray).getValue().equals("int")) {
            match("int");
        } else if (tokens.get(currTokenInArray).getValue().equals("string")) {
            match("string");
        } else if (tokens.get(currTokenInArray).getValue().equals("boolean")) {
            match("boolean");
        } else {
            createError("[TYPE]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseChar() {
        if (debug) {
            createDebug("parseChar()");
        }
        currTree.addNode("Char", false);
        // Using regEx instead of a bunch of ifs to see if token is in alphabet
        if (tokens.get(currTokenInArray).getValue().matches("[a-z]+")) {
            matchRegEx("[a-z]+");
        } else {
            createError("[ [a-z]+ ]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseSpace() {
        if (debug) {
            createDebug("parseSpace()");
        }
        currTree.addNode("Space", false);
        match(" ");
        currTree.executeOrder66();
    }

    public void parseDigit() {
        if (debug) {
            createDebug("parseDigit()");
        }
        currTree.addNode("Digit", false);
        // Using regEx instead of a bunch of ifs to see if token is a digit
        if (tokens.get(currTokenInArray).getValue().matches("[0-9]+")) {
            matchRegEx("[0-9]+");
        } else {
            createError("[ [0-9]+ ]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseBoolOp() {
        if (debug) {
            createDebug("parseBoolOp()");
        }
        currTree.addNode("BoolOp", false);
        if (tokens.get(currTokenInArray).getValue().equals("==")) {
            match("==");
        } else if (tokens.get(currTokenInArray).getValue().equals("!=")) {
            match("!=");
        } else {
            createError("[BOOL_OP]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseBoolVal() {
        if (debug) {
            createDebug("parseBoolVal()");
        }
        currTree.addNode("BoolVal", false);
        if (tokens.get(currTokenInArray).getValue().equals("true")) {
            match("true");
        } else if (tokens.get(currTokenInArray).getValue().equals("false")) {
            match("false");
        } else {
            createError("[BOOL_VAL]", tokens.get(currTokenInArray).getType(), tokens.get(currTokenInArray).getValue(),
                    tokens.get(currTokenInArray).getLine());
        }
        currTree.executeOrder66();
    }

    public void parseIntOp() {
        if (debug) {
            createDebug("parseIntOp()");
        }
        currTree.addNode("IntOp", false);
        match("+");
        currTree.executeOrder66();
    }

    // Creates an error to display and stop parse
    public void createError(String expected, String read, String value, String line) {
        currTree.setError(true);
        System.out.println(
                "ERROR Parse - Expected: " + expected + " Read: " + read + " with value " + value + " on line: "
                        + line);
    }

    // Creates a debug message
    public void createDebug(String message) {
        System.out.println("DEBUG Parse - " + message);
    }

    public void createInfo(String message) {
        System.out.println("INFO Parse - " + message);
    }
}
