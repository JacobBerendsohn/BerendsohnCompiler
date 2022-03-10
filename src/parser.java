import java.util.ArrayList;

public class parser {
    // Main parse function
    parseTree currTree = new parseTree();
    ArrayList<token> tokens = new ArrayList<token>();
    int currTokenInArray = 0;

    int prCt = 0;

    public parseTree startParse(ArrayList<token> tokenList) {
        this.tokens = tokenList;
        parseProgram();
        tokens.clear();
        return currTree;
    }

    public void match(String expected) {
        if (expected.equals(tokens.get(currTokenInArray).getValue())) {
            currTree.addNode(expected, "leaf");
            currTokenInArray++;
        } else {
            // Error handling
            System.out.println("Error in match");
        }
    }

    public void matchRegEx(String regEx) {
        if (tokens.get(currTokenInArray).getValue().matches(regEx)) {
            currTree.addNode(tokens.get(currTokenInArray).getValue(), "leaf");
            currTokenInArray++;
        } else {
            // Add error handling
            System.out.println("Error in matchRegEx");

        }
    }

    public void parseProgram() {

        currTree.addNode("Program", "branch");
        prCt++;
        parseBlock();
        match("$");
        currTree.executeOrder66();
    }

    public void parseBlock() {
        currTree.addNode("Block", "branch");
        match("{");
        parseStatementList();
        match("}");
        currTree.executeOrder66();
    }

    public void parseStatementList() {
        currTree.addNode("StatementList", "branch");
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
    }

    public void parseStatement() {
        currTree.addNode("Statement", "branch");
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
        currTree.addNode("PrintStatement", "branch");
        match("print");
        match("(");
        parseExpr();
        match(")");
        currTree.executeOrder66();
    }

    public void parseAssignmentStatement() {
        currTree.addNode("AssignmentStatement", "branch");
        parseID();
        match("=");
        parseExpr();
        currTree.executeOrder66();
    }

    public void parseVarDecl() {
        currTree.addNode("VarDecl", "branch");
        parseType();
        parseID();
        currTree.executeOrder66();
    }

    public void parseWhileStatement() {
        currTree.addNode("WhileStatement", "branch");
        match("while");
        parseBooleanExpr();
        parseBlock();
        currTree.executeOrder66();
    }

    public void parseIfStatement() {
        currTree.addNode("IfStatement", "branch");
        match("if");
        parseBooleanExpr();
        parseBlock();
        currTree.executeOrder66();
    }

    public void parseExpr() {
        currTree.addNode("Expr", "branch");
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
        currTree.addNode("IntExpr", "branch");
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
        currTree.addNode("StringExpr", "branch");
        if (tokens.get(currTokenInArray).getValue().equals("\"")) {
            match("\"");
            parseCharList();
            match("\"");
        } else {
            // Error Handling
        }
    }

    public void parseBooleanExpr() {
        currTree.addNode("BooleanExpr", "branch");
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
        currTree.addNode("ID", "branch");
        parseChar();
        currTree.executeOrder66();
    }

    public void parseCharList() {
        currTree.addNode("CharList", "branch");
        matchRegEx("[a-z ]+");
        currTree.executeOrder66();
    }

    public void parseType() {
        currTree.addNode("Type", "branch");
        if (tokens.get(currTokenInArray).getValue().equals("int")) {
            match("int");
        } else if (tokens.get(currTokenInArray).getValue().equals("string")) {
            match("string");
        } else if (tokens.get(currTokenInArray).getValue().equals("boolean")) {
            match("boolean");
        } else {
            // Error Handling
        }
        currTree.executeOrder66();
    }

    public void parseChar() {
        currTree.addNode("Char", "branch");
        // Using regEx instead of a bunch of ifs to see if token is in alphabet
        if (tokens.get(currTokenInArray).getValue().matches("[a-z]+")) {
            matchRegEx("[a-z]+");
        } else {
            // Error Handling
        }
        currTree.executeOrder66();
    }

    public void parseSpace() {
        currTree.addNode("Space", "branch");
        match(" ");
        currTree.executeOrder66();
    }

    public void parseDigit() {
        currTree.addNode("Digit", "branch");
        // Using regEx instead of a bunch of ifs to see if token is a digit
        if (tokens.get(currTokenInArray).getValue().matches("[0-9]+")) {
            matchRegEx("[0-9]+");
        } else {
            // Error Handling
        }
        currTree.executeOrder66();
    }

    public void parseBoolOp() {
        currTree.addNode("BoolOp", "branch");
        if (tokens.get(currTokenInArray).getValue().equals("==")) {
            match("==");
        } else if (tokens.get(currTokenInArray).getValue().equals("!=")) {
            match("!=");
        } else {
            // Error handling
        }
        currTree.executeOrder66();
    }

    public void parseBoolVal() {
        currTree.addNode("BoolVal", "branch");
        if (tokens.get(currTokenInArray).getValue().equals("true")) {
            match("true");
        } else if (tokens.get(currTokenInArray).getValue().equals("false")) {
            match("false");
        } else {
            // Error handling
        }
        currTree.executeOrder66();
    }

    public void parseIntOp() {
        currTree.addNode("IntOp", "branch");
        match("+");
        currTree.executeOrder66();
    }
}
