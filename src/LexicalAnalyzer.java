/* references:
    Extended Backus-Naur Form: https://www.youtube.com/watch?v=JO_0e9mPofY
                               https://karmin.ch/ebnf/examples


 */
import java.util.ArrayList;

class Token {
    enum TokenType {
        NUM, ADD, MUL, OPENP, CLOSEP, OPENB, CLOSEB, NEWLINE, ERR
    }

    public TokenType tokenType;
    public String lexeme;

    public Token (String word){
        this.tokenType = identifyToken(word);
        this.lexeme = word;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getLexeme() {
        return lexeme;
    }

    // returns the corresponding token type identified for a string
    public static TokenType identifyToken(String word) {

        DFA dfa = new DFA();
        // Creating array of string length
        char[] ch = new char[word.length()];

        // Copy character by character into array
        for (int x = 0; x < word.length(); x++) {
            ch[x] = word.charAt(x);
        }

        String final_state = dfa.identifyState(word);

        if (final_state.equals("Q10"))
            return TokenType.ADD;
        else if (final_state.equals("Q11"))
            return TokenType.MUL;
        else if (final_state.equals("Q12"))
            return TokenType.OPENP;
        else if (final_state.equals("Q13"))
            return TokenType.CLOSEP;
        else if (final_state.equals("Q14"))
            return TokenType.OPENB;
        else if (final_state.equals("Q15"))
            return TokenType.CLOSEB;
        else if (final_state.equals("Q16"))
            return TokenType.NEWLINE;
        else if (!final_state.equals("Q0") && !final_state.equals("Qdead") && !final_state.equals("Qbottom"))
            return TokenType.NUM;
        else
            return TokenType.ERR;
    }

    // used only to match stack tokentype to its original lexeme (does not include number)
    public static String matchToLexeme(String Tokentype){
        if (Tokentype.equals("OPENP")){
            return "(";
        }
        else if (Tokentype.equals("CLOSEP")){
            return ")";
        }
        else if (Tokentype.equals("OPENB")){
            return "[";
        }
        else if (Tokentype.equals("CLOSEB")){
            return "]";
        }
        else if (Tokentype.equals("ADD")){
            return "+";
        }
        else if (Tokentype.equals("MUL")){
            return "*";
        }
        else
            return "unknown";
    }
}

class LexicalAnalyzer {
    public static String output = "";                                // output string to print

    static ArrayList<Token> process(String sourceCode){
        // scan the input
        String[] words = sourceCode.split(" ");     // split by whitespace
        ArrayList<Token> tokenList = new ArrayList<>();
        String token = "";                                 // placeholder to stitch all the numericals

        for (String w: words){
            if(!w.isEmpty()) {
                for (int i=0; i<w.length(); i++){
                    if((w.charAt(i) < 48 || w.charAt(i) > 57) && w.charAt(i)!='\n'){    // if character is a symbol, turn the previous numbers into a token
                        if(!token.isEmpty()) {
                            Token t = new Token(token);
                            tokenList.add(t);
                            token = "";
                        }
                        Token t = new Token(w.charAt(i)+"");
                        tokenList.add(t);
                    }
                    else if (w.charAt(i) == '\n'){      // if character is a new line, make a token NEWLINE to separate inputs for parsing
                        if(!token.isEmpty()) {
                            Token t = new Token(token);
                            tokenList.add(t);
                            token = "";
                        }
                        Token t = new Token(w.charAt(i)+"");
                        tokenList.add(t);
                    }
                    else{
                        token = token.concat(w.charAt(i)+"");   // place all numbers into a single string, so this could be converted into a single NUM
                    }
                }
            }
        }
        return tokenList;
    }
}

