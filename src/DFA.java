import javafx.util.Pair;
import java.util.Stack;

// guide sources: https://www.cs.bgu.ac.il/~comp171/wiki.files/01-scanning.pdf
//                https://grrinchas.github.io/posts/dfa-in-java?fbclid=IwAR37iucBV6iKb1Gg_0hj7-nxDDOZ5fLUGbKjW_0RDbjRGoYj6Ho4m_XG7UM

public class DFA {
    private enum States {

        Q0(false), Q1(true), Q2(true), Q10(true), Q11(true),
        Q12(true), Q13(true), Q14(true), Q15(true), Q16(true),
        Qdead(false), Qbottom(false);

        final boolean accept;

        States(boolean accept) {
            this.accept = accept;
        }

        States line, lparen, rparen, add, mul, lbracket, rbracket, zero, one, two, three, four, five, six, seven, eight, nine;

        // transition states (source -> input -> destination wise)
        static {
            Q0.zero = Q1; Q0.one = Q2; Q0.two = Q2; Q0.three = Q2; Q0.four = Q2; Q0.five = Q2; Q0.six = Q2;
            Q0.seven = Q2; Q0.eight = Q2; Q0.nine = Q2; Q0.line = Q16;
            Q0.mul = Q11; Q0.add = Q10; Q0.lparen = Q12; Q0.lbracket = Q14; Q0.rparen = Q13; Q0.rbracket = Q15;
            Q2.zero = Q2; Q2.one = Q2; Q2.two = Q2; Q2.three = Q2; Q2.four = Q2; Q2.five = Q2;
            Q2.six = Q2; Q2.seven = Q2; Q2.eight = Q2; Q2.nine = Q2;
        }

        States transition(char symbol) {
            switch (symbol) {
                case '\n':
                    return this.line;
                case '(':
                    return this.lparen;
                case ')':
                    return this.rparen;
                case '[':
                    return this.lbracket;
                case ']':
                    return this.rbracket;
                case '+':
                    return this.add;
                case '*':
                    return this.mul;
                case '0':
                    return this.zero;
                case '1':
                    return this.one;
                case '2':
                    return this.two;
                case '3':
                    return this.three;
                case '4':
                    return this.four;
                case '5':
                    return this.five;
                case '6':
                    return this.six;
                case '7':
                    return this.seven;
                case '8':
                    return this.eight;
                case '9':
                    return this.nine;
                default:
                    return Qdead;       // Qdead if input is not in the alphabet
            }
        }
    }

    public String identifyState(String word) {
        // maximal munch code, return the final state
        Stack<Pair<States, Integer>> stack = new Stack<>();
        int i = 1;
        States state;

        // removed the while(true) loop as it only needs to traverse once for this case
            state = States.Q0;
            Pair<States, Integer> pair = new Pair<>(States.Qbottom, i);
            stack.push(pair);

            while (i <= word.length() && state.transition(word.charAt(i-1)) != null) {
                if (state.accept) {
                    stack.empty();
                }
                pair = new Pair<>(state, i);
                stack.push(pair);
                state = state.transition(word.charAt(i-1));

                i++;
            }

            while (!state.accept) {
                if (stack.isEmpty()){
                    return States.Qdead.toString();
                }

                state = stack.peek().getKey();
                i = stack.peek().getValue();
                stack.pop();

                if (state == States.Qbottom) {  // tokenization is impossible, thus a dead state
                    return States.Qdead.toString();
                }
            }

            if (i > word.length()) {
                return state.toString();
            }
            else       // hit a dead state if the length of input exceeds the length to the final state path
                return States.Qdead.toString();
    }
}
