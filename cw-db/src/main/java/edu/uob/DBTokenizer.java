package edu.uob;

import java.util.*;

public class DBTokenizer {
    List<String> tokens;
    String tokenStr;
    String[] specialCharacters = {"(",")",",",";"};
    String[] unaryLogicalOperators = {">","<","==","!="};
    String[] compositeLogicalOperators = {">=","<="};

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
        input = expandLogicalOperators(input);
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = reformLogicalOperators(input);
        input = input.trim();
        return input.split(" ");
    }

    private String expandSpecialCharacters(String input) {
        for(String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        return input;
    }

    private String expandLogicalOperators(String input) {
        for(String op : unaryLogicalOperators) {
            input = input.replace(op, " " + op + " ");
        }
        return input;
    }

    private String reformLogicalOperators(String input) {
        for(String op : compositeLogicalOperators) {
            input = input.replace(op.charAt(0) + " " + op.charAt(1), " " + op + " ");
        }
        return input;
    }

    public List<String> getTokens() {
        return tokens;
    }
}
