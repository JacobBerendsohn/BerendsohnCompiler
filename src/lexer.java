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

        inputLines.forEach((k, v) -> System.out.println("Line: " + k + " Contains: " + v));

        // Beginning Lex
        for (int line = 1; line < inputLines.size() + 1; line++) {

            // Checking if the line contains more than one character to split if need be
            if (inputLines.get(line).length() > 1) {

                // Placeholder for current line
                String phLine = inputLines.get(line);
                String[] phLineArray = phLine.split("");

                // Iterating through the new array to run checks on each potential token
                int curPosInLineArray = 0;
                for (String str : phLineArray) {
                    System.out.println(str);

                    // Keyword Check
                    if (checkKeyword(phLineArray, curPosInLineArray, line) != null) {
                        tokens.add(checkKeyword(phLineArray, curPosInLineArray, line));
                    }

                    // ID Check
                    if (checkID(phLineArray, curPosInLineArray, line) != null) {
                        tokens.add(checkID(phLineArray, curPosInLineArray, line));
                    }

                    // Moving pointer to next item in Array
                    curPosInLineArray++;
                }

                /*
                 * for (token tok : testingKeywords) {
                 * System.out.println("Token Type: " + tok.getType() + "Token Value: " +
                 * tok.getValue()
                 * + "Token Position: " + tok.getLine());
                 * }
                 */

            } else {

                System.out.println(inputLines.get(line));
                // Functions for checking which token it is
            }
        }

        // Calling debugger to print tokens
        debug(tokens);
        return tokens;
    }

    public token checkKeyword(String[] currLine, int curPos, int currLineInt) {

        // Block Comment Check
        if (curPos + 1 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1]).equals("/*")) {
                curPos = checkComment(currLine, curPos, currLineInt);
            }
        }
        // Quotation check
        if (curPos + 1 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1]).equals("/*")) {
                curPos = checkComment(currLine, curPos, currLineInt);
            }
        }
        // While check
        if (curPos + 4 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1] + currLine[curPos + 2] + currLine[curPos + 3]
                    + currLine[curPos + 4]).equalsIgnoreCase("while")) {
                return createToken("WHILE_STATEMENT", "while",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }
        }
        // Print check
        if (curPos + 4 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1] + currLine[curPos + 2] + currLine[curPos + 3]
                    + currLine[curPos + 4]).equalsIgnoreCase("print")) {
                return createToken("PRINT_STATEMENT", "print",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }
        }
        // If check
        if (curPos + 1 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1]).equalsIgnoreCase("if")) {
                return createToken("IF_STATEMENT", "if",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }
        }
        // Int check
        if (curPos + 2 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1] + currLine[curPos + 2]).equalsIgnoreCase("int")) {
                return createToken("INT_TYPE", "int",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }
        }
        // String check
        if (curPos + 5 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1] + currLine[curPos + 2] + currLine[curPos + 3]
                    + currLine[curPos + 4] + currLine[curPos + 5]).equalsIgnoreCase("string")) {
                return createToken("STRING_TYPE", "string",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }
        }
        // Boolean check
        if (curPos + 6 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1] + currLine[curPos + 2] + currLine[curPos + 3]
                    + currLine[curPos + 4] + currLine[curPos + 5] + currLine[curPos + 6]).equalsIgnoreCase("boolean")) {
                return createToken("BOOLEAN_TYPE", "boolean",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }
        }
        // True check
        if (curPos + 3 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1] + currLine[curPos + 2] + currLine[curPos + 2])
                    .equalsIgnoreCase("true")) {
                return createToken("BOOLEAN_VALUE", "true",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }
        }
        // False check
        if (curPos + 4 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1] + currLine[curPos + 2] + currLine[curPos + 3]
                    + currLine[curPos + 4]).equalsIgnoreCase("print")) {
                return createToken("BOOLEAN_VALUE", "false",
                        Integer.toString(currLineInt) + ":" + Integer.toString(curPos));
            }

        }
        return null;
    }

    public token checkID(String[] currLine, int curPos, int currLineInt) {

        // Block Comment Check
        if (curPos + 1 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1]).equals("/*")) {
                curPos = checkComment(currLine, curPos, currLineInt);
            }
        }
        // Quotation check
        if (curPos + 1 < currLine.length) {
            if ((currLine[curPos] + currLine[curPos + 1]).equals("/*")) {
                curPos = checkComment(currLine, curPos, currLineInt);
            }
        }
        // ID Check
        if (currLine[curPos].equalsIgnoreCase("a") || currLine[curPos].equalsIgnoreCase("b")
                || currLine[curPos].equalsIgnoreCase("c") || currLine[curPos].equalsIgnoreCase("d")
                || currLine[curPos].equalsIgnoreCase("e") || currLine[curPos].equalsIgnoreCase("f")
                || currLine[curPos].equalsIgnoreCase("g") || currLine[curPos].equalsIgnoreCase("h")
                || currLine[curPos].equalsIgnoreCase("i") || currLine[curPos].equalsIgnoreCase("j")
                || currLine[curPos].equalsIgnoreCase("k") || currLine[curPos].equalsIgnoreCase("l")
                || currLine[curPos].equalsIgnoreCase("m") || currLine[curPos].equalsIgnoreCase("n")
                || currLine[curPos].equalsIgnoreCase("o") || currLine[curPos].equalsIgnoreCase("p")
                || currLine[curPos].equalsIgnoreCase("q") || currLine[curPos].equalsIgnoreCase("r")
                || currLine[curPos].equalsIgnoreCase("s") || currLine[curPos].equalsIgnoreCase("t")
                || currLine[curPos].equalsIgnoreCase("u") || currLine[curPos].equalsIgnoreCase("v")
                || currLine[curPos].equalsIgnoreCase("w") || currLine[curPos].equalsIgnoreCase("x")
                || currLine[curPos].equalsIgnoreCase("y") || currLine[curPos].equalsIgnoreCase("z")) {

            // Creating token for ID
            return createToken("ID", currLine[curPos],
                    Integer.toString(currLineInt) + ":" + Integer.toString(curPos));

        }
        return null;
    }

    public token checkSymbol(String[] currLine, int curPos, int currLineInt) {
        return null;
    }

    public token checkDigit(String[] currLine, int curPos, int currLineInt) {
        return null;
    }

    public token checkCharacters(String[] currLine, int curPos, int currLineInt) {
        return null;
    }

    public void debug(ArrayList<token> tokens) {
        for (token t : tokens) {
            System.out.println(
                    "DEBUG Lexer - " + t.getType() + " [  " + t.getValue() + "  ] found at (" + t.getLine() + ")");
        }
    }

    public int checkQuote(String[] currLine, int startPos, int currLineInt) {
        for (int i = startPos; i < currLine.length; i++) {
            if ((currLine[i]).equals("\"")) {
                return i + 1;
            } else {
                // Case where quote is not closed
                createWarning(Integer.toString(currLineInt) + ":" + Integer.toString(startPos),
                        "Missing ' \" ' to end quotation");
                break;
            }
        }
        return startPos;
    }

    // Sends back the end position of a block comment in the array
    public int checkComment(String[] currLine, int startPos, int currLineInt) {
        for (int i = startPos; i < currLine.length; i++) {
            if ((currLine[i] + currLine[i + 1]).equals("*/")) {
                // System.out.println("CheckCOmment Test");
                return i + 2;
            } else {
                // System.out.println("CheckCOmment Test 2");
                // Case where block does not end
                createWarning(Integer.toString(currLineInt) + ":" + Integer.toString(startPos),
                        "Missing '*/' to end Block");
                break;
            }
        }
        // System.out.println("CheckCOmment Test 3");
        return startPos;
    }

    // Creates a warning for the user but does not stop the lex
    public void createWarning(String position, String message) {
        System.out.println("WARNING Lexer - Position: (" + position + ") Message: " + message);
    }

    // Creates a token object
    public token createToken(String type, String value, String position) {
        token returnToken = new token(type, value, position);
        return returnToken;
    }

}
