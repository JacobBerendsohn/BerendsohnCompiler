import java.io.File;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

public class lexer {

    public ArrayList[] tokens;

    public ArrayList[] lex(File inputFile) {

        BufferedReader reader;

        // Reading the input file line by line
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            while (!line.isEmpty()) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tokens;
    }

}
