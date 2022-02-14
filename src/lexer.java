import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class lexer {

    public ArrayList<token> tokens = new ArrayList<token>();
    public ArrayList<String> preTokenList = new ArrayList<String>();
    public String languageString = "";
    public Boolean isQuote = false;

    public ArrayList<token> lex(File inputFile) {

        BufferedReader reader;

        // Reading the input file line by line
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);

                // Adding each line from input to a single string
                languageString += line;
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(languageString);

        // Splitting the Input into an array to go through each item for Lexing
        String[] languageArray = languageString.split("");

        // Putting items from the array into an ArrayList
        for (String string : languageArray) {
            preTokenList.add(string);
        }

        /*
         * for (String str : languageArray) {
         * System.out.println(str);
         * }
         */

        // Getting rid of all spaces not in quotes in given languages
        for (int i = 0; i < preTokenList.size(); i++) {
            if (preTokenList.get(i).equals("\"")) {
                isQuote = !isQuote;
            }
            if ((preTokenList.get(i).equals(" ") || preTokenList.get(i).equals("\t")) && !isQuote) {
                preTokenList.remove(i);
                // Taking into account the position of the removed item
                i -= 1;
            }
        }

        for (String str : preTokenList) {
            System.out.println(str);
        }

        return tokens;
    }

}
