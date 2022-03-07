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
        } else {
            // Add error handling
        }
    }

    public void parseBlock() {

    }

    public void parseStatementList() {

    }

    public void parseStatement() {

    }

    public void parsePrintStatement() {

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
