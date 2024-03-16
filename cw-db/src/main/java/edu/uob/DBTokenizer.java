package edu.uob;

import java.util.*;

public class DBTokenizer {
    List<String> tokens;
    String[] specialCharacters = {"(",")",",",";"};

    public DBTokenizer(String str) {
        tokens = new ArrayList<>();
        str = str.trim();
        String[] fragments = str.split("'");
        for (int i=0; i<fragments.length; i++) {
            if (i%2 != 0) {
                tokens.add("'" + fragments[i] + "'");
            }
            else {
                tokens.addAll(Arrays.asList(tokenize(fragments[i])));
            }
        }
    }

    private String[] tokenize(String input)
    {
        for (String specialCharacter : specialCharacters) {
            input = input.replace(specialCharacter, " " + specialCharacter + " ");
        }
        while (input.contains("  ")) input = input.replaceAll("  ", " ");
        input = input.trim();
        return input.split(" ");
    }

    public List<String> getTokens() {
        return tokens;
    }
}
