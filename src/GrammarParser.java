import java.util.*;

class Rule {
    // rule representation. Capital letter = non-terminal. Small letter = terminal.
    public String LHS;         // the LHS rule
    public List<String> RHS = new ArrayList<>();   // the RHS rules
}

class Backstack{
    public String production;           // the production or LHS
    public int total_productions;       // counts how many RHS there is in the rule
    public int used_productions;        // pointer to dictate which productions were already called

    public Backstack(String production, int total_productions, int used_productions) {
        this.production = production;
        this.total_productions = total_productions;
        this.used_productions = used_productions;
    }

    public int getTotal_productions() {
        return total_productions;
    }

    public int getUsed_productions() {
        return used_productions;
    }

    public String getProduction() {
        return production;
    }

    public void setUsed_productions(int used_productions) {
        this.used_productions = used_productions;
    }
}

public class GrammarParser {
    public static HashMap<String, Rule> createRules(String GrammarText){
        HashMap<String, Rule> rules = new HashMap<>();             // hashmap for all the grammar rules
        String[] productions = GrammarText.split(";\n");     // read production per line

        for (String p: productions) {
            String[] line = p.split(": ");            // arrow separates the LHS and RHS

            Rule X = new Rule();
            X.LHS = line[0];

            if (line[1].contains("|")) {
                String[] prod = line[1].split(" \\| ");       // different productions are separated by the | symbol

                X.RHS.addAll(Arrays.asList(prod));
            }
            else
                X.RHS.add(line[1]);

            rules.put(line[0], X);
        }

        return rules;           // returns the grammar productions
    }

    public static void parse(Stack<Token> input_original, HashMap<String, Rule> rulesMap){
        Stack<String> stack = new Stack<>();          // current stack of grammar rules
        Stack<Token> backinput = new Stack<>();          // stack for backtracking input
        Stack<Token> input = input_original;
        List<String> production = new ArrayList<>();     // for the current RHS
        List<Backstack> backstack = new ArrayList<>();   // history of productions for backtracking
        stack.push("S");  // S is the start symbol

        boolean noRule = false;
        production.add("0");

        // parsing happens by checking each character and matching production rules.
        while (!stack.isEmpty() && !input.isEmpty()){
            String top = stack.peek();

            if (rulesMap.get(top) != null) {            // if top of stack is a production
                production = rulesMap.get(top).RHS;
                if (backstack.isEmpty()){
                    expand(stack, production.get(0), backstack, rulesMap);
                }
                else{
                    expand(stack, production.get(backstack.get(backstack.size()-1).getUsed_productions()), backstack, rulesMap);
                }
            }
            else if (top.equals("ε")){      // an epsilon indicates that the non-terminal can be removed
                stack.pop();
            }
            else if (input.peek().getTokenType().toString().equals("ERR")){     // if input is invalid, perform panic mode (ignore the rest)
                noRule = true;
                stack.clear();
                break;
            }
            else{                                       // else it is a terminal
                for (int x=0; x < Token.TokenType.values().length; x++) {
                    if (top.equals(input.peek().getTokenType().toString())){        // terminal found
                        stack.pop();
                        input.pop();
                        noRule = false;

                        break;
                    }
                    else{
                        noRule = true;
                    }
                }
                if (noRule){
                    input.clear();
                    input.addAll(backinput);            // move input back the previous state

                    performBacktrack(stack, backstack, rulesMap);
                }
            }
            backinput.clear();
            backinput.addAll(input);                    // save previous input into a stack
        }
        boolean missing = false;

        if (!stack.isEmpty()){
            while(!stack.isEmpty()) {
                if (rulesMap.get(stack.peek())!= null)
                    stack.pop();
                else {
                    System.out.println(" - REJECT. Missing token '" + Token.matchToLexeme(stack.pop()) + "'"); // get the missing token
                    missing = true;
                }
            }
        }
        if (noRule && !missing) {
            System.out.println(" - REJECT. Offending token '" + input.peek().getLexeme() + "'");
        }
        else if (!missing) {
            System.out.println(" - ACCEPT");
        }
    }

    // expands a production rule, replacing the LHS with its RHS and stores it back to the stack.
    public static void expand(Stack stack, String production, List<Backstack>backstack, HashMap<String, Rule> rulesMap){
        if (stack.isEmpty()){
            return;
        }

        stack.pop();
        String[] prod = production.split(" ");

        for(int i = prod.length-1; i >= 0; i--){
            stack.push(prod[i]);
            if (rulesMap.get(prod[i]) != null)      // if production is a non-terminal
                backstack.add(new Backstack(prod[i], rulesMap.get(prod[i]).RHS.size(), 0));
            else
                backstack.add(new Backstack(prod[i], 0, 0));
        }
    }

    // reverts back to its LHS rule
    public static void performBacktrack(Stack stack, List<Backstack>backstack, HashMap<String, Rule> rulesMap){
        while(true){
            if (backstack.isEmpty()){           // empty backstack indicates we've reached the start state
                return;
            }
            else if (stack.isEmpty()){
                while (rulesMap.get(backstack.get(backstack.size()-1)) != null){        // traverse until the latest non-terminal
                    backstack.remove(backstack.size() - 1);
                }

                stack.push(backstack.get(backstack.size() - 1).getProduction());
                backstack.get(backstack.size()-1).setUsed_productions(backstack.get(backstack.size()-1).getUsed_productions()+1);
                break;
            }

            // remove the recently pushed tokens in the stack, replace it with the previous production
            if(!stack.peek().equals(backstack.get(backstack.size()-1).getProduction())){
                if (backstack.get(backstack.size() - 1).getUsed_productions() >= backstack.get(backstack.size() - 1).getTotal_productions()) {
                    while ((backstack.get(backstack.size() - 1).getUsed_productions() >= backstack.get(backstack.size() - 1).getTotal_productions()))
                        backstack.remove(backstack.size() - 1);

                }
                stack.push(backstack.get(backstack.size() - 1).getProduction());
                backstack.get(backstack.size()-1).setUsed_productions(backstack.get(backstack.size()-1).getUsed_productions()+1);
                break;
            }
            else{
                backstack.remove(backstack.size() - 1);
                stack.pop();
            }
        }

        // if all rules in a production is used, backtrack again
        if (backstack.get(backstack.size()-1).getUsed_productions() >= backstack.get(backstack.size()-1).getTotal_productions()){
            performBacktrack(stack, backstack, rulesMap);
        }
    }
}
