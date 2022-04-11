import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import objects.parseTree;
import objects.token;

import java.io.BufferedReader;
import java.io.FileReader;

public class lexer {

    HashMap<Integer, String> inputLines = new HashMap<>();
    public ArrayList<token> tokens = new ArrayList<token>();
    public ArrayList<String> preTokenList = new ArrayList<String>();
    public String languageString[][];
    public int errorCount = 0;
    public int warningCount = 0;
    public int programCount = 1;
    public boolean debug = true;

    // Add a varibla to store the current position, reference that instead of
    // passing the current position between functions
    // public int curPosInLineArray = 0;

    public ArrayList<token> lex(File inputFile, parser parse, semantic semantic) {

        BufferedReader reader;

        // Reading the input file line by line
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int currLine = 1;
            while (line != null) {
                // Adding each line from input to a single string
                if (!"".equals(line)) {
                    inputLines.put(currLine, line);
                    currLine++;
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Printing out the HashMap
        inputLines.forEach((k, v) -> System.out.println("Line: " + k + " Contains: " + v));

        System.out.println("\n\nINFO Lexer - LEX Beginning for Program 1");

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
            for (int curPosInLineArray = 0; curPosInLineArray < preTokenList.size(); curPosInLineArray++) {

                // Comment Check
                if (checkComment(preTokenList, curPosInLineArray, line) != null) {
                    token commentT = checkComment(preTokenList, curPosInLineArray, line);
                    curPosInLineArray = commentT.getNewPos();
                } else

                // Quote Check
                if (checkQuote(preTokenList, curPosInLineArray, line) != null) {
                    ArrayList<token> qT = checkQuote(preTokenList, curPosInLineArray, line);

                    curPosInLineArray = qT.get(qT.size() - 1).getNewPos();

                    if (!qT.get(0).getType().equals("ERROR")) {
                        for (token t : qT) {
                            tokens.add(t);
                        }
                    } else {
                        createError(qT.get(0).getLine(),
                                "Illegal characters in quote: " + qT.get(0).getValue());
                    }
                } else

                // Keyword Check
                if (checkKeyword(preTokenList, curPosInLineArray, line) != null) {
                    token kwT = checkKeyword(preTokenList, curPosInLineArray, line);
                    // Removing items from previous token from ArrayList
                    for (int i = curPosInLineArray; i <= kwT.getNewPos(); i++) {
                        preTokenList.set(i, null);
                    }

                    if (!kwT.getType().equals("ERROR")) {
                        tokens.add(kwT);
                    } else {
                        createError(kwT.getLine(),
                                "Found capital letters in keyword: " + kwT.getValue());
                    }

                } else
                // ID Check
                if (checkID(preTokenList, curPosInLineArray, line) != null) {
                    token idT = checkID(preTokenList, curPosInLineArray, line);

                    if (!idT.getType().equals("ERROR")) {
                        tokens.add(idT);
                    } else {
                        createError(idT.getLine(),
                                "Found capital letters for ID: " + idT.getValue());
                    }
                } else
                // Symbol Check
                if (checkSymbol(preTokenList, curPosInLineArray, line) != null) {
                    token syT = checkSymbol(preTokenList, curPosInLineArray, line);
                    for (int i = curPosInLineArray; i <= syT.getNewPos(); i++) {
                        preTokenList.set(i, null);
                    }
                    tokens.add(syT);

                } else
                // Digit Check
                if (checkDigit(preTokenList, curPosInLineArray, line) != null) {
                    token diT = checkDigit(preTokenList, curPosInLineArray, line);
                    tokens.add(diT);

                } else
                // EOP Check
                if (checkEOP(preTokenList, curPosInLineArray, line) != null) {
                    token diT = checkEOP(preTokenList, curPosInLineArray, line);
                    tokens.add(diT);
                    runParse(line, inputLines, parse, semantic);
                } else
                // Unrecognized Token Check
                if (preTokenList.get(curPosInLineArray) != null && !preTokenList.get(curPosInLineArray).equals(" ")
                        && !preTokenList.get(curPosInLineArray).equals("/")
                        && !preTokenList.get(curPosInLineArray).equals("*")
                        && !preTokenList.get(curPosInLineArray).equals("\"")) {
                    createError(line + ":" + curPosInLineArray,
                            "Unrecognized Token: " + preTokenList.get(curPosInLineArray));
                }

            }

        }

        return tokens;
    }

    // Ends lex for each individual program and sends it through parsing
    public void runParse(int currLine, HashMap<Integer, String> inputLines, parser parse, semantic semantic) {
        // Check for end of program to run Parse and then Lex next Program
        if (!tokens.isEmpty()) {
            if (tokens.get(tokens.size() - 1).getValue().equals("$")) {
                // Calling debugger to print tokens
                if (debug) {
                    debug(tokens);
                }

                programCount++;
                if (errorCount == 0) {
                    createInfo("Lex Complete with " + warningCount + " warning(s)\n");

                    // I plan on moving all calls to Parse and Sem analysis to main class

                    ////////
                    // Begin Parse HERE
                    ////////

                    parse.createInfo("Parsing Program " + Integer.toString(programCount - 1) + "...");
                    parseTree p = parse.startParse(tokens);
                    if (!p.isError()) {
                        parse.createInfo("Parse Completed for Program " + Integer.toString(programCount - 1) + "\n");
                        parse.createInfo("CST for program " + Integer.toString(programCount - 1));
                        System.out.println(p.toString());

                    } else {
                        System.out.println("Error(s) found in program " + Integer.toString(programCount - 1)
                                + " stopped in parse\n");
                    }

                    // Clearing tree so next string is not muttled
                    p.clearTree();

                    ////////
                    // End Parse HERE
                    ////////

                    ////////
                    // Start Semantic Analysis
                    ////////

                    parse.createInfo(
                            "Semantic Analysis (Second Parse) starting for Program "
                                    + Integer.toString(programCount - 1) + "...");
                    parseTree AST = semantic.startSemantic(tokens);
                    if (!p.isError()) {
                        semantic.createInfo(
                                "Semantic Analysis Completed for Program " + Integer.toString(programCount - 1) + "\n");
                        parse.createInfo("AST for program " + Integer.toString(programCount - 1));
                        System.out.println(AST.toString());

                    } else {
                        System.out.println("Error(s) found in program " + Integer.toString(programCount - 1)
                                + " stopped in parse\n");
                    }

                    ////////
                    // End Semantic Analysis
                    ////////

                    tokens.clear();

                    if (inputLines.get(currLine + 1) != null) {
                        createInfo("Lexing program " + programCount + "...");
                    }
                    errorCount = 0;
                } else {
                    tokens.clear();
                    createInfo("Lex failed with " + errorCount + " error(s)\n");
                    if (inputLines.get(currLine + 1) != null) {
                        createInfo("Lexing program " + programCount + "...");
                    }
                    errorCount = 0;
                }
            }
        }
    }

    // Checks for keywords found in the grammar
    public token checkKeyword(ArrayList<String> currLine, int curPos, int currLineInt) {

        if (currLine.get(curPos) != null) {
            // While check
            if (curPos + 4 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4)).equalsIgnoreCase("while")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                            + currLine.get(curPos + 3)
                            + currLine.get(curPos + 4)).equals("while")) {

                        return createToken("WHILE_STATEMENT", "while",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                    } else {
                        return createToken("ERROR",
                                (currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                                        + currLine.get(curPos + 3)
                                        + currLine.get(curPos + 4)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                    }
                }
            }
            // Print check
            if (curPos + 4 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4)).equalsIgnoreCase("print")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                            + currLine.get(curPos + 3)
                            + currLine.get(curPos + 4)).equals("print")) {
                        return createToken("PRINT_STATEMENT", "print",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                    } else {
                        return createToken("ERROR",
                                (currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                                        + currLine.get(curPos + 3)
                                        + currLine.get(curPos + 4)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                    }
                }
            }
            // If check
            if (curPos + 1 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1)).equalsIgnoreCase("if")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1)).equals("if")) {
                        return createToken("IF_STATEMENT", "if",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
                    } else {
                        return createToken("ERROR", (currLine.get(curPos) + currLine.get(curPos + 1)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
                    }
                }
            }
            // Int check
            if (curPos + 2 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2))
                        .equalsIgnoreCase("int")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2))
                            .equals("int")) {
                        return createToken("TYPE_INT", "int",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 2);
                    } else {
                        return createToken("ERROR",
                                (currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 2);
                    }
                }
            }
            // String check
            if (curPos + 5 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4) + currLine.get(curPos + 5)).equalsIgnoreCase("string")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                            + currLine.get(curPos + 3)
                            + currLine.get(curPos + 4) + currLine.get(curPos + 5)).equals("string")) {
                        return createToken("TYPE_STRING", "string",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 5);
                    } else {
                        return createToken("ERROR",
                                (currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                                        + currLine.get(curPos + 3)
                                        + currLine.get(curPos + 4) + currLine.get(curPos + 5)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 5);
                    }
                }
            }
            // Boolean check
            if (curPos + 6 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4) + currLine.get(curPos + 5) + currLine.get(curPos + 6))
                        .equalsIgnoreCase("boolean")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                            + currLine.get(curPos + 3)
                            + currLine.get(curPos + 4) + currLine.get(curPos + 5) + currLine.get(curPos + 6))
                            .equals("boolean")) {
                        return createToken("TYPE_BOOLEAN", "boolean",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 6);
                    } else {
                        return createToken("ERROR",
                                (currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                                        + currLine.get(curPos + 3)
                                        + currLine.get(curPos + 4) + currLine.get(curPos + 5)
                                        + currLine.get(curPos + 6)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 6);
                    }
                }
            }
            // True check
            if (curPos + 3 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3))
                        .equalsIgnoreCase("true")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                            + currLine.get(curPos + 3))
                            .equals("true")) {
                        return createToken("BOOLEAN_VALUE", "true",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 3);
                    } else {
                        return createToken("ERROR",
                                (currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                                        + currLine.get(curPos + 3)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 3);
                    }
                }
            }
            // False check
            if (curPos + 4 < currLine.size()) {
                if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                        + currLine.get(curPos + 3)
                        + currLine.get(curPos + 4)).equalsIgnoreCase("false")) {
                    if ((currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                            + currLine.get(curPos + 3)
                            + currLine.get(curPos + 4)).equals("false")) {
                        return createToken("BOOLEAN_VALUE", "false",
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                    } else {
                        return createToken("ERROR",
                                (currLine.get(curPos) + currLine.get(curPos + 1) + currLine.get(curPos + 2)
                                        + currLine.get(curPos + 3)
                                        + currLine.get(curPos + 4)),
                                Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 4);
                    }
                }

            }
        }
        return null;
    }

    public token checkID(ArrayList<String> currLine, int curPos, int currLineInt) {

        if (currLine.get(curPos) != null) {
            // ID Check
            if (currLine.get(curPos).matches("[a-z]+")) {
                return createToken("ID", currLine.get(curPos),
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);
            }
            // Creating token for ID
        }
        return null;
    }

    public token checkSymbol(ArrayList<String> currLine, int curPos, int currLineInt) {
        if (currLine.get(curPos) != null) {
            // Checking for open Brackets
            if (currLine.get(curPos).equals("{")) {
                return createToken("L_BRACE", "{",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);
            }
            // Checking for closed Brackets
            if (currLine.get(curPos).equals("}")) {
                return createToken("R_BRACE", "}",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);
            }
            // Checking for open Parenthesis
            if (currLine.get(curPos).equals("(")) {
                return createToken("L_PAREN", "(",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);
            }
            // Checking for closed Parenthesis
            if (currLine.get(curPos).equals(")")) {
                return createToken("R_PAREN", ")",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);
            }
            // Checking for addition
            if (currLine.get(curPos).equals("+")) {
                return createToken("ADDITION", "+",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);
            }
            // Checking for assign
            if (currLine.get(curPos).equals("=") && !currLine.get(curPos + 1).equals("=")) {
                return createToken("ASSIGN", "=",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos);
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

    // Checks for individual numbers
    public token checkDigit(ArrayList<String> currLine, int curPos, int currLineInt) {
        if (currLine.get(curPos) != null) {
            if (currLine.get(curPos).matches("[0-9]+")) {
                return createToken("DIGIT", currLine.get(curPos),
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), curPos + 1);
            }
        }
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

    // Debug function to display token values
    public void debug(ArrayList<token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(
                    "DEBUG Lexer - " + tokens.get(i).getType() + " [  " + tokens.get(i).getValue()
                            + "  ] found at (" + tokens.get(i).getLine() + ")");
        }

    }

    // Sends back the end position of a quotation in the array
    public ArrayList<token> checkQuote(ArrayList<String> currLine, int curPos, int currLineInt) {
        ArrayList<String> quote = new ArrayList<String>();
        if (currLine.get(curPos) != null) {
            // Checking space for comments
            if ((currLine.get(curPos)).equals("\"")) {
                for (int i = curPos + 1; i < currLine.size(); i++) {
                    if ((currLine.get(i)).equals("\"")) {
                        // Checking if current line ends to avoid null pointer
                        if (currLine.size() == curPos) {
                            preTokenList.add(" ");
                        }
                        // Getting the quotes value to add to the token (Concatenation)
                        for (int j = curPos; j <= i; j++) {
                            quote.add(currLine.get(j));
                        }
                        if (quote.contains("A") || quote.contains("B") || quote.contains("C") || quote.contains("D")
                                || quote.contains("E") || quote.contains("F") || quote.contains("G")
                                || quote.contains("H") || quote.contains("I") || quote.contains("J")
                                || quote.contains("K") || quote.contains("L") || quote.contains("M")
                                || quote.contains("N") || quote.contains("O") || quote.contains("P")
                                || quote.contains("Q") || quote.contains("R") || quote.contains("S")
                                || quote.contains("T") || quote.contains("U") || quote.contains("V")
                                || quote.contains("W") || quote.contains("X") || quote.contains("Y")
                                || quote.contains("Z") || quote.contains("1") || quote.contains("2")
                                || quote.contains("3") || quote.contains("4") || quote.contains("5")
                                || quote.contains("6") || quote.contains("7") || quote.contains("8")
                                || quote.contains("9") || quote.contains("0")) {
                            // Creating error token if a quote contains illegal characters
                            ArrayList<token> sendBack = new ArrayList<token>();

                            sendBack.add(createToken("ERROR", String.join("", quote),
                                    Integer.toString(currLineInt) + ":" + Integer.toString(curPos) + "-"
                                            + Integer.toString(i),
                                    i));

                            return sendBack;
                        } else {
                            ArrayList<token> sendBack = new ArrayList<token>();

                            // Separating the quotes from the actual quote itself for parse and sending back
                            // all 3 tokens
                            sendBack.add(createToken("QUOTE", "\"",
                                    Integer.toString(currLineInt) + ":" + Integer.toString(curPos), i));

                            sendBack.add(createToken("STRING_EXPR",
                                    String.join("", quote).substring(1, String.join("", quote).length() - 1),
                                    Integer.toString(currLineInt) + ":" + Integer.toString(curPos + 1) + "-"
                                            + Integer.toString(i - 1),
                                    i));

                            sendBack.add(createToken("QUOTE", "\"",
                                    Integer.toString(currLineInt) + ":" + Integer.toString(i), i));

                            // Sending back the real token if it contains no illegal characters
                            return sendBack;
                        }

                    }
                }
                createError(Integer.toString(currLineInt) + ":" + Integer.toString(curPos),
                        "Missing '\"' to end String");
            }
        }
        return null;
    }

    // Sends back the end position of a block comment in the array
    public token checkComment(ArrayList<String> currLine, int curPos, int currLineInt) {
        try {
            if (currLine.get(curPos) != null) {
                // Checking space for comments
                if (curPos + 1 < currLine.size()) {
                    if ((currLine.get(curPos) + (currLine.get(curPos + 1))).equals("/*")) {
                        for (int i = curPos; i < currLine.size(); i++) {
                            if ((currLine.get(i) + currLine.get(i + 1)).equals("*/")) {
                                // Checking if current line ends to avoid null pointer
                                if (currLine.size() == curPos) {
                                    preTokenList.add(" ");
                                }
                                return createToken("COMMENT", "COMMENT",
                                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos), i + 1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            createError(Integer.toString(currLineInt) + ":" + Integer.toString(curPos),
                    "Missing '*/' to end Block");
        }

        // System.out.println("CheckCOmment Test 3");
        return null;
    }

    // Creates a warning with a message to display
    public void createWarning(String position, String message) {
        System.out.println("WARNING Lexer - Position: (" + position + ") Warning: " + message);
        warningCount++;
    }

    // Creates an error to display and stop lex
    public void createError(String position, String message) {
        System.out.println("ERROR Lexer - Position: (" + position + ") Error: " + message);
        errorCount++;
    }

    // Creates a message with information to display
    public void createInfo(String message) {
        System.out.println("INFO Lexer - " + message);
    }

    // Creates a token object
    public token createToken(String type, String value, String position, int newPos) {
        token returnToken = new token(type, value, position, newPos);
        return returnToken;
    }

}
