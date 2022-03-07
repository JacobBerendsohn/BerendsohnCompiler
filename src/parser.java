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
    }

    public void match(String expected) {
        if (expected.equals(tokens.get(currTokenInArray).getValue())) {
            currTree.addNode(expected, true);
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
        parseStatement();
        parseStatementList();

        // Figure out what to do with empty string

    }

    public void parseStatement() {
        currTree.addNode("Statement", false);
        if (tokens.get(currTokenInArray).getValue().equals("print")) {
            parsePrintStatement();
        } else if (tokens.get(currTokenInArray).getType().equals("ID")
                && tokens.get(currTokenInArray + 1).getType().equals("ASSIGN")) {
            parseAssignmentStatement();
        } else if (tokens.get(currTokenInArray).getType().equals("ID")
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
    }

    public void parsePrintStatement() {
        currTree.addNode("PrintStatement", false);

    }

    public void parseAssignmentStatement() {

    }

    public void parseVarDecl() {

    }

    public void parseWhileStatement() {

    }

    public void parseIfStatement() {

    }

    public void parseExpr() {

    }

    public void parseIntExpr() {

    }

    public void parseStringExpr() {

    }

    public void parseBooleanExpr() {

    }

    public void parseID() {

    }

    public void parseCharList() {

    }

    public void parseType() {

    }

    public void parseChar() {

    }

    public void parseSpace() {

    }

    public void parseDigit() {

    }

    public void parseBoolOp() {

    }

    public void parseBoolVal() {

    }

    public void parseIntOp() {

    }
}
