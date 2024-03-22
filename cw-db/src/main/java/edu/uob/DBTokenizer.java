package edu.uob;

import java.util.*;

public class DBTokenizer {
    private List<String> tokens;
    private String tokenStr;
    private String[] specialCharacters = {"(",")",",",";"};
    private String[] singleOperators = {">","<","=","!"};
    private String[] compositeOperators = {">=","<=","==","!="};

    public DBTokenizer(String str) {
        tokens = new ArrayList<>();
        tokenStr = str;
    }

    public void tokenize() {
        tokenStr = tokenStr.trim();
        String[] fragments = tokenStr.split("'");
        for (int i=0; i<fragments.length; i++) {
            if (i%2 != 0) {
                tokens.add("'" + fragments[i] + "'");
            }
            else {
                tokens.addAll(Arrays.asList(tokenizeFragment(fragments[i])));
            }
        }
    }
    private String[] tokenizeFragment(String input)
    {
        input = expandSpecialCharacters(input);
        input = expandSingleOperators(input);
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = reformCompositeOperators(input);
        input = input.trim();
        return input.split(" ");
    }

    private String expandSpecialCharacters(String input) {
        for(String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        return input;
    }

    private String expandSingleOperators(String input) {
        for(String op : singleOperators) {
            input = input.replace(op, " " + op + " ");
        }
        return input;
    }

    private String reformCompositeOperators(String input) {
        for(String op : compositeOperators) {
            input = input.replace(op.charAt(0) + " " + op.charAt(1), op);
        }
        return input;
    }

    public List<String> getTokens() {
        return tokens;
    }
}
