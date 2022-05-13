import java.io.File;

import objects.node;
import objects.parseTree;
import objects.token;

public class compiler {

    public static File languageInput;

    public static void main(String[] args) {

        lexer lex = new lexer();
        parser parse = new parser();
        semantic semantic = new semantic();
        codeGen codeGen = new codeGen();
        parseTree pTree = new parseTree();
        token initToken = new token();
        node initNode = new node();

        // Enthusiastic Intro
        System.out.println("I cannot wait to have a fully functional compiler I made myself!");

        // Reading out given parameters
        System.out.println("Given Arguments:");
        for (int i = 0; i < args.length; i++) {
            System.out.println("Argument " + i + ": " + args[i]);
        }

        // Taking in the file from the command line
        if (0 < args.length) {
            languageInput = new File(args[0]);
        } else {
            System.err.println("Invalid arguments count:" + args.length);
        }

        // Starting the lexer
        lex.lex(languageInput, parse, semantic, codeGen);

    }
}