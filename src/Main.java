import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Main {
    public static void main(String[] args)throws Exception {
        HashMap<String, Rule> grammarRules;

        BufferedReader br = new BufferedReader(new FileReader("grammar.txt"));  // read grammar text file
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            grammarRules = GrammarParser.createRules(sb.toString());    // create grammar Rules per line
        } finally {
            br.close();
        }

        ArrayList<Token> tokenList = new ArrayList<>();
        BufferedReader br2 = new BufferedReader(new FileReader("input.txt"));  // read input text file
        try {
            StringBuilder sb = new StringBuilder();
            String line = br2.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br2.readLine();
            }
            tokenList.addAll(LexicalAnalyzer.process(sb.toString()));
        } finally {
            br2.close();
        }

        Stack<Token> stack = new Stack<>();         // input for the parser class
        ArrayList<Token> list = new ArrayList<>();  // holder for input tokens

        // save the output into a text file
        System.out.println("Parsing finished! Please check your output.txt file.");
        File file = new File("output.txt");
        PrintStream stream = new PrintStream(file);
        System.setOut(stream);

        for (int i = 0; i < tokenList.size(); i++) {
            if (tokenList.get(i).tokenType.toString().equals("NEWLINE")) {  // if token is NEWLINE, parse the input chunk, remove NEWLINE token
                stack.addAll(list);

                for (int x = list.size() - 1; x >= 0; x--) {
                    System.out.print(list.get(x).getLexeme() + " ");
                }
                list.clear();

                if (!stack.isEmpty()) {                         // do not parse empty inputs
                    GrammarParser.parse(stack, grammarRules);
                    stack.clear();
                }
            } else {
                list.add(0, tokenList.get(i));      // put the corresponding tokens in a single input stream
            }
        }
    }
}
