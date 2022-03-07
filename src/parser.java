import java.util.ArrayList;

public class parser {
    // Main parse function
    parseTree currTree = new parseTree();
    ArrayList<token> tokens = new ArrayList<token>();
    int currTokenInArray = 0;

    public void startParse(ArrayList<token> tokenList) {
        this.tokens = tokenList;
        parseProgram();
    }

    public void parseProgram() {
        currTree.addNode("Program", false);
        parseBlock();
        match("$");
        currTree.executeOrder66();
        currTree.toString();
    }

    public void match(String expected) {
        if (expected.equals(tokens.get(currTokenInArray).getValue())) {
            currTree.addNode(expected, true);
            currTokenInArray++;
        } else {
            // Add error handling
        }
    }

    public void matchChar(String regEx) {
        if (tokens.get(currTokenInArray).getValue().matches(regEx)) {
            currTree.addNode(tokens.get(currTokenInArray).getValue(), true);
            currTokenInArray++;
        } else {
            // Add error handling
        }
    }

    public void parseBlock() {
        currTree.addNode("Block", false);
        match("{");
        parseStatementList();
        match("}");
        currTree.executeOrder66();
    }

    public void parseStatementList() {
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
            // empty string handling
        }
        currTree.executeOrder66();
    }

    public void parseStatement() {
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
            // Error Handling
        }
        currTree.executeOrder66();
    }

    public void parsePrintStatement() {
        currTree.addNode("PrintStatement", false);
        match("print");
        match("(");
        parseExpr();
        match(")");
        currTree.executeOrder66();
    }

    public void parseAssignmentStatement() {
        currTree.addNode("AssignmentStatement", false);
        parseID();
        match("=");
        parseExpr();
        currTree.executeOrder66();
    }

    public void parseVarDecl() {
        currTree.addNode("VarDecl", false);
        parseType();
        parseID();
        currTree.executeOrder66();
    }

    public void parseWhileStatement() {
        currTree.addNode("WhileStatement", false);
        match("while");
        parseBooleanExpr();
        parseBlock();
        currTree.executeOrder66();
    }

    public void parseIfStatement() {
        currTree.addNode("IfStatement", false);
        match("if");
        parseBooleanExpr();
        parseBlock();
        currTree.executeOrder66();
    }

    public void parseExpr() {
        currTree.addNode("Expr", false);
        if (tokens.get(currTokenInArray).getType().equals("DIGIT")) {
            parsePrintStatement();
            // Checking for a quote
        } else if (tokens.get(currTokenInArray).getValue().split("")[0].equals("\"")) {
            parseStringExpr();
        } else if (tokens.get(currTokenInArray).getValue().equals("(")) {
            parseBooleanExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("ID")) {
            parseID();
        } else {
            // Error Handling
        }
        currTree.executeOrder66();
    }

    public void parseIntExpr() {
        currTree.addNode("IntExpr", false);
        if (tokens.get(currTokenInArray).getType().equals("DIGIT")
                && tokens.get(currTokenInArray).getValue().equals("+")) {
            parseDigit();
            parseIntOp();
            parseExpr();
        } else if (tokens.get(currTokenInArray).getType().equals("DIGIT")) {
            parseDigit();
        }
        currTree.executeOrder66();
    }

    public void parseStringExpr() {
        currTree.addNode("StringExpr", false);

    }

    public void parseBooleanExpr() {
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
            // Error Handling
        }
        currTree.executeOrder66();
    }

    public void parseID() {
        currTree.addNode("ID", false);
        parseChar();
        currTree.executeOrder66();
    }

    public void parseCharList() {
        currTree.addNode("CharList", false);

    }

    public void parseType() {
        currTree.addNode("Type", false);

    }

    public void parseChar() {
        currTree.addNode("Char", false);
        if (tokens.get(currTokenInArray).getValue().matches("[a-z]+")) {
            matchChar("[a-z]+");
        } else {
            // Error Handling
        }
        currTree.executeOrder66();
    }

    public void parseSpace() {
        currTree.addNode("Space", false);
    }

    public void parseDigit() {
        currTree.addNode("Digit", false);
    }

    public void parseBoolOp() {
        currTree.addNode("BoolOp", false);
    }

    public void parseBoolVal() {
        currTree.addNode("BoolVal", false);
    }

    public void parseIntOp() {
        currTree.addNode("IntOp", false);
    }
}
