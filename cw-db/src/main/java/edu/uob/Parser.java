package edu.uob;


import java.util.*;

public class Parser {
    int cTok;
    int failTok;
    String failMessage;
    List<String> tokens;

    public Parser(List<String> tokens) {
        this.tokens = tokens;
        cTok = 0;
        failTok = 0;
    }

    public boolean parse() {
        return Command();
    }

    private boolean fail(int tok, String str) {
        if(cTok >= failTok) {
            failTok = cTok;
            failMessage = "Invalid command. " +
                    "Parser error around token number " + failTok + ": " +
                    tokens.get(failTok) + ". " + str;
        }
        cTok = tok;
        return false;
    }

    private boolean Command() {
        if(!CommandType()) {
            return false;
        }

        int bakTok = cTok;
        if(!tokens.get(cTok++).equals(";")) {
            return fail(bakTok, "Not ; at the end of a valid command.");
        }
        if(tokens.size() > cTok) {
            return fail(cTok, "Multiple ; token.");
        }
        return true;
    }

    private boolean CommandType() {
        int bakTok = cTok;
        if(!Use() && !Create() && !Drop() && !Alter() && !Insert() && !Select() &&
                !Update() && !Delete() && !Join()) {
            return fail(bakTok, "Invalid command type.");
        }
        return true;
    }

    private boolean Use() {
        int bakTok = cTok;
        if(!tokens.get(cTok++).equalsIgnoreCase("USE")) {
            return fail(bakTok, "USE key word not found.");
        }

        if(!isPlaintext()) {
            return fail(bakTok, "No text for Use case.");
        }

        return true;
    }
    
    private boolean Create() {
        int bakTok = cTok;
        if(!CreateDatabase() && !CreateTable()) {
            return fail(bakTok, "Invalid create command.");
        }
        return true;
    }
    
    private boolean CreateDatabase() {
        int bakTok = cTok;
        if(!tokens.get(cTok++).equalsIgnoreCase("CREATE")) {
            return fail(bakTok, "CREATE key word not found.");
        }
        if(!tokens.get(cTok++).equalsIgnoreCase("DATABASE")) {
            return fail(bakTok, "DATABASE key word not found.");
        }
        if(!isPlaintext()) {
            return fail(bakTok, "No text for CreateDatabase case.");
        }
        return true;
    }
    
    private boolean CreateTable() {
        int bakTok = cTok;
        if(!tokens.get(cTok++).equalsIgnoreCase("CREATE")) {
            return fail(bakTok, "CREATE key word not found.");
        }
        if(!tokens.get(cTok++).equalsIgnoreCase("TABLE")) {
            return fail(bakTok, "TABLE key word not found.");
        }
        if(!isPlaintext()) {
            return fail(bakTok, "No text for CreateDatabase case.");
        }
        
        if(!tokens.get(cTok).equalsIgnoreCase("(")) {
            return true;
        }
        
        cTok++;
        bakTok = cTok;
        if(!AttributeList()) {
            return fail(bakTok, "Non-valid AttributeList for CreateTable case.");
        }
        if(!tokens.get(cTok++).equalsIgnoreCase(")")) {
            return fail(bakTok, "Missing ) for CreateTable case.");
        }
        return true;
    }

    private boolean AttributeList() {
        if()
    }

    private boolean isPlaintext() {
        int bakTok = cTok;
        if(!tokens.get(cTok++).matches("^[a-zA-Z0-9]+$")) {
            return fail(bakTok, "Not plaintext.");
        }
        return true;
    }
}
