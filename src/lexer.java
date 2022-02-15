import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                // Test Array
                ArrayList<token> testingKeywords = new ArrayList<token>();

                // Iterating through the new array to run checks on each potential token
                int curPosInLineArray = 0;
                for (String str : phLineArray) {
                    System.out.println(str);

                    // Keyword Check
                    if (checkKeyword(phLineArray, curPosInLineArray, line) != null) {
                        testingKeywords.add(
                                checkKeyword(phLineArray, curPosInLineArray, line));
                    }

                    // Moving pointer to next item in Array
                    curPosInLineArray++;
                }

                for (token tok : testingKeywords) {
                    System.out.println("Token Type: " + tok.getType() + "Token Value: " + tok.getValue()
                            + "Token Position: " + tok.getLine());
                }

            } else {

                System.out.println(inputLines.get(line));
                // Functions for checking which token it is
            }
        }

        return tokens;
    }

    public token checkKeyword(String[] currLine, int curPos, int currLineInt) {
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

    // Creates a token object
    public token createToken(String type, String value, String position) {
        token returnToken = new token(type, value, position);
        return returnToken;
    }

}
