import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;

public class lexer {

    HashMap<Integer, String> inputLines = new HashMap<>();
    public ArrayList<token> tokens = new ArrayList<token>();
    public ArrayList<String> preTokenList = new ArrayList<String>();
    public String languageString[][];
    public Boolean isQuote = false;

    // Add a varibla to store the current position, reference that instead of
    // passing the current position between functions
    // public int curPosInLineArray = 0;

    public ArrayList<token> lex(File inputFile) {

        BufferedReader reader;

        // Reading the input file line by line
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int currLine = 1;
            while (line != null) {
                // Adding each line from input to a single string
                inputLines.put(currLine, line);
                line = reader.readLine();
                currLine++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Printing out the HashMap
        inputLines.forEach((k, v) -> System.out.println("Line: " + k + " Contains: " + v));
        int curPosInLineArray = 0;
        // Beginning Lex
        for (int line = 1; line < inputLines.size() + 1; line++) {
            // Checking if the line contains more than one character to split if need be

            // Placeholder for current line
            String phLine = inputLines.get(line);
            String[] phLineArray = phLine.split("");
            preTokenList.clear();

            for (String str : phLineArray) {
                preTokenList.add(str);
            }

            // Iterating through the new array to run checks on each potential token
            curPosInLineArray = 0;
            for (String str : preTokenList) {

                // Keyword Check
                if (checkKeyword(preTokenList, curPosInLineArray, line) != null) {
                    token kwT = checkKeyword(preTokenList, curPosInLineArray, line);
                    // Removing items from previous token from ArrayList
                    for (int i = curPosInLineArray; i <= kwT.getNewPos(); i++) {
                        preTokenList.set(i, null);
                    }
                    if (kwT.getType().equals("COMMENT")) {
                        curPosInLineArray = kwT.getNewPos();
                    } else {
                        tokens.add(kwT);
                    }
                }
                // ID Check
                if (checkID(preTokenList, curPosInLineArray, line) != null) {
                    token idT = checkID(preTokenList, curPosInLineArray, line);
                    for (int i = curPosInLineArray; i <= idT.getNewPos(); i++) {
                        preTokenList.set(i, null);
                    }
                    tokens.add(idT);
                }
                // Symbol Check
                if (checkSymbol(preTokenList, curPosInLineArray, line) != null) {
                    token syT = checkSymbol(preTokenList, curPosInLineArray, line);
                    tokens.add(syT);
                }
                // Digit Check
                if (checkDigit(preTokenList, curPosInLineArray, line) != null) {
                    token diT = checkDigit(preTokenList, curPosInLineArray, line);
                    tokens.add(diT);
                }
                // EOP Check
                if (checkEOP(preTokenList, curPosInLineArray, line) != null) {
                    token diT = checkEOP(preTokenList, curPosInLineArray, line);
                    tokens.add(diT);
                }

                // Moving pointer to next item in Array
                curPosInLineArray++;
            }

        }
        // Calling debugger to print tokens
        debug(tokens);
        return tokens;
    }

    public token checkKeyword(ArrayList<String> currLine, int curPos, int currLineInt) {

        if (currLine.get(curPos) != null) {
            // Block Comment Check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equals("/*")) {
                    return createToken("COMMENT", "COMMENT", "COMMENT", checkComment(currLine, curPos, currLineInt));
                }
            }
            // Quotation check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equals("\"")) {
                    curPos = checkComment(currLine, curPos, currLineInt);
                }
            }
            // While check
            if (curPos + 4 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4)).equalsIgnoreCase("while")) {
                    return createToken("WHILE_STATEMENT", "while",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                }
            }
            // Print check
            if (curPos + 4 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4)).equalsIgnoreCase("print")) {
                    return createToken("PRINT_STATEMENT", "print",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                }
            }
            // If check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equalsIgnoreCase("if")) {
                    return createToken("IF_STATEMENT", "if",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
                }
            }
            // Int check
            if (curPos + 2 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2))
                        .equalsIgnoreCase("int")) {
                    return createToken("INT_TYPE", "int",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 2);
                }
            }
            // String check
            if (curPos + 5 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4) + currLine.get(curPos + 5)).equalsIgnoreCase("string")) {
                    return createToken("STRING_TYPE", "string",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 5);
                }
            }
            // Boolean check
            if (curPos + 6 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4) + currLine.get(curPos + 5) + currLine.get(curPos + 6))
                                .equalsIgnoreCase("boolean")) {
                    return createToken("BOOLEAN_TYPE", "boolean",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 6);
                }
            }
            // True check
            if (curPos + 3 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3))
                                .equalsIgnoreCase("true")) {
                    return createToken("BOOLEAN_VALUE", "true",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 3);
                }
            }
            // False check
            if (curPos + 4 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4)).equalsIgnoreCase("print")) {
                    return createToken("BOOLEAN_VALUE", "false",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                }

            }
        }
        return null;
    }

    public token checkID(ArrayList<String> currLine, int curPos, int currLineInt) {

        if (currLine.get(curPos) != null) {

            // Block Comment Check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equals("/*")) {
                    curPos = checkComment(currLine, curPos, currLineInt);
                }
            }
            // Quotation check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equals("\"")) {
                    curPos = checkComment(currLine, curPos, currLineInt);
                }
            }
            // ID Check
            if (currLine.get(curPos).equalsIgnoreCase("a") || currLine.get(curPos).equalsIgnoreCase("b")
                    || currLine.get(curPos).equalsIgnoreCase("c") || currLine.get(curPos).equalsIgnoreCase("d")
                    || currLine.get(curPos).equalsIgnoreCase("e") || currLine.get(curPos).equalsIgnoreCase("f")
                    || currLine.get(curPos).equalsIgnoreCase("g") || currLine.get(curPos).equalsIgnoreCase("h")
                    || currLine.get(curPos).equalsIgnoreCase("i") || currLine.get(curPos).equalsIgnoreCase("j")
                    || currLine.get(curPos).equalsIgnoreCase("k") || currLine.get(curPos).equalsIgnoreCase("l")
                    || currLine.get(curPos).equalsIgnoreCase("m") || currLine.get(curPos).equalsIgnoreCase("n")
                    || currLine.get(curPos).equalsIgnoreCase("o") || currLine.get(curPos).equalsIgnoreCase("p")
                    || currLine.get(curPos).equalsIgnoreCase("q") || currLine.get(curPos).equalsIgnoreCase("r")
                    || currLine.get(curPos).equalsIgnoreCase("s") || currLine.get(curPos).equalsIgnoreCase("t")
                    || currLine.get(curPos).equalsIgnoreCase("u") || currLine.get(curPos).equalsIgnoreCase("v")
                    || currLine.get(curPos).equalsIgnoreCase("w") || currLine.get(curPos).equalsIgnoreCase("x")
                    || currLine.get(curPos).equalsIgnoreCase("y") || currLine.get(curPos).equalsIgnoreCase("z")) {

                // Creating token for ID
                return createToken("ID", currLine.get(curPos),
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);

            }
        }
        return null;
    }

    public token checkSymbol(ArrayList<String> currLine, int curPos, int currLineInt) {
        if (currLine.get(curPos) != null) {
            // Checking for open Brackets
            if (currLine.get(curPos).equals("{")) {
                return createToken("L_BRACE", "{",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
            // Checking for closed Brackets
            if (currLine.get(curPos).equals("}")) {
                return createToken("R_BRACE", "}",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
            // Checking for open Parenthesis
            if (currLine.get(curPos).equals("(")) {
                return createToken("L_PAREN", "(",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
            // Checking for closed Parenthesis
            if (currLine.get(curPos).equals(")")) {
                return createToken("R_PAREN", ")",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
            // Checking for addition
            if (currLine.get(curPos).equals("+")) {
                return createToken("ADDITION", "+",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
            // Checking for assign
            if (currLine.get(curPos).equals("=") && !currLine.get(curPos + 1).equals("=")) {
                return createToken("ASSIGN", "=",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
            // == Check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equals("==")) {
                    return createToken("EQUIVALENT", "==",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
                }
            }
            // != Check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equals("!=")) {
                    return createToken("NOT_EQUIV", "!=",
                            Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
                }
            }
        }
        return null;
    }

    public token checkDigit(ArrayList<String> currLine, int curPos, int currLineInt) {
        if (currLine.get(curPos) != null) {
            if (currLine.get(curPos).equals("0") || currLine.get(curPos).equals("1") || currLine.get(curPos).equals("2")
                    || currLine.get(curPos).equals("3") || currLine.get(curPos).equals("4")
                    || currLine.get(curPos).equals("5")
                    || currLine.get(curPos).equals("6") || currLine.get(curPos).equals("7")
                    || currLine.get(curPos).equals("8")
                    || currLine.get(curPos).equals("9")) {
                return createToken("DIGIT", currLine.get(curPos),
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
        }
        return null;
    }

    public token checkCharacters(String[] currLine, int curPos, int currLineInt) {
        return null;
    }

    public token checkEOP(ArrayList<String> currLine, int curPos, int currLineInt) {
        if (currLine.get(curPos) != null) {
            // Checking for end operator
            if (currLine.get(curPos).equals("$")) {
                return createToken("END_OP", "$",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
        }
        return null;
    }

    public void debug(ArrayList<token> tokens) {
        for (token t : tokens) {
            if (t.getType().equals("END_OP")) {
                System.out.println(
                        "DEBUG Lexer - " + t.getType() + " [  " + t.getValue() + "  ] found at (" + t.getLine() + ")");
                System.out.println(
                        "INFO Lexer - LEX completed with ? errors");
            } else {
                System.out.println(
                        "DEBUG Lexer - " + t.getType() + " [  " + t.getValue() + "  ] found at (" + t.getLine() + ")");
            }

        }
    }

    // Sends back the end position of a quotation in the array
    public int checkQuote(ArrayList<String> currLine, int startPos, int currLineInt) {
        for (int i = startPos; i < currLine.size(); i++) {
            if ((currLine.get(i)).equals("\"")) {
                return i + 1;
            }
        }
        createWarning(Integer.toString(currLineInt) + ":" + Integer.toString(startPos),
                "Missing ' \" ' to end quotation");
        return startPos;
    }

    // Sends back the end position of a block comment in the array
    public int checkComment(ArrayList<String> currLine, int startPos, int currLineInt) {
        for (int i = startPos; i < currLine.size(); i++) {
            if ((currLine.get(i) + currLine.get(i + 1)).equals("*/")) {
                // System.out.println("CheckCOmment Test");
                return i + 2;
            }
        }
        createWarning(Integer.toString(currLineInt) + ":" + Integer.toString(startPos),
                "Missing '*/' to end Block");
        // System.out.println("CheckCOmment Test 3");
        return startPos;
    }

    // Creates a warning for the user but does not stop the lex
    public void createWarning(String position, String message) {
        System.out.println("WARNING Lexer - Position: (" + position + ") Message: " + message);
    }

    // Creates a token object
    public token createToken(String type, String value, String position, int newPos) {
        token returnToken = new token(type, value, position, newPos);
        return returnToken;
    }

}
