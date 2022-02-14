import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class compiler {

    public static File languageInput;

    public static void main(String[] args) {
        System.out.println("I cannot wait to have a fully functional compiler I made myself!");

        System.out.println("Given Arguments:");
        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument " + i + ": " + args[i]);
        }

        // Taking in the file from the command line
        try {
            Scanner input = new Scanner(new File(args[0]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}