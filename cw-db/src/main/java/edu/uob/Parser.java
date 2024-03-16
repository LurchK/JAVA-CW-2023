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
        return command();
    }

    private boolean fail(int bakTok, String str) {
        if(cTok >= failTok) {
            failTok = cTok;
            failMessage = "Invalid command. " +
                    "Parser error around token number " + failTok + ": " +
                    tokens.get(failTok) + ". " + str;
        }
        cTok = bakTok;
        return false;
    }

    private boolean command() {
        if(!commandType()) {
            return false;
        }

        int bakTok = cTok;
        if(!tokens.get(cTok).equals(";")) {
            return fail(bakTok, "Not ; at the end of a valid command.");
        }
        cTok++;
        if(tokens.size() > cTok) {
            return fail(bakTok, "Multiple ; token.");
        }
        return true;
    }

    private boolean commandType() {
        int bakTok = cTok;
        if(use() || create() || drop() || alter() || insert() || select() ||
                update() || delete() || join()) {
            return true;
        }
        return fail(bakTok, "Invalid CommandType.");
    }

    private boolean use() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("USE")) {
            return fail(bakTok, "USE key word not found for Use.");
        }
        cTok++;

        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for Use.");
        }

        return true;
    }
    
    private boolean create() {
        int bakTok = cTok;
        if(!createDatabase() && !createTable()) {
            return fail(bakTok, "Invalid Create.");
        }
        return true;
    }
    
    private boolean createDatabase() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("CREATE")) {
            return fail(bakTok, "CREATE key word not found for CreateDatabase.");
        }
        cTok++;
        if(!tokens.get(cTok).equalsIgnoreCase("DATABASE")) {
            return fail(bakTok, "DATABASE key word not found for CreateDatabase.");
        }
        cTok++;
        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for CreateDatabase.");
        }
        return true;
    }
    
    private boolean createTable() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("CREATE")) {
            return fail(bakTok, "CREATE key word not found for CreateTable.");
        }
        cTok++;
        if(!tokens.get(cTok).equalsIgnoreCase("TABLE")) {
            return fail(bakTok, "TABLE key word not found for CreateTable.");
        }
        cTok++;
        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for CreateTable.");
        }
        
        if(!tokens.get(cTok).equalsIgnoreCase("(")) {
            return true;
        }
        cTok++;

        bakTok = cTok;
        if(!attributeList()) {
            return fail(bakTok, "Invalid AttributeList for CreateTable.");
        }
        if(!tokens.get(cTok).equalsIgnoreCase(")")) {
            return fail(bakTok, "Missing ) for CreateTable.");
        }
        cTok++;
        return true;
    }

    private boolean attributeList() {
        int bakTok = cTok;
        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for AttributeList.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(",")) {
            return true;
        }
        cTok++;

        if(!attributeList()) {
            return fail(bakTok, "Invalid AttributeList for AttributeList.");
        }
        return true;
    }

    private boolean drop() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("DROP")) {
            return fail(bakTok, "DROP key word not found for Drop.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("DATABASE") &&
                !tokens.get(cTok).equalsIgnoreCase("TABLE")) {
            return fail(bakTok, "No DATABASE or TABLE for Drop.");
        }
        cTok++;
        
        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for Drop.");
        }
        return true;
    }

    private boolean alter() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("ALTER")) {
            return fail(bakTok, "ALTER key word not found for Alter.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("TABLE")) {
            return fail(bakTok, "TABLE key word not found for Alter.");
        }
        cTok++;

        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for Alter.");
        }
        
        if(!alterationType()) {
            return fail(bakTok, "Invalid AlterationType for Alter.");
        }

        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for Alter.");
        }
        return true;
    }
    
    private boolean alterationType() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("ADD") &&
                !tokens.get(cTok).equalsIgnoreCase("DROP")) {
            return fail(bakTok, "No ADD or DROP key word for AlterationType.");
        }
        cTok++;
        return true;
    }
    
    private boolean insert() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("INSERT")) {
            return fail(bakTok, "INSERT key word not found for Insert.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("INTO")) {
            return fail(bakTok, "INTO key word not found for Insert.");
        }
        cTok++;
        
        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for Insert.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("VALUES")) {
            return fail(bakTok, "VALUES key word not found for Insert.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("(")) {
            return fail(bakTok, "( not found for Insert.");
        }
        cTok++;

        if(!valueList()) {
            return fail(bakTok, "Invalid ValueList for Insert.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(")")) {
            return fail(bakTok, ") not found for Insert.");
        }
        cTok++;
        return true;
    }

    private boolean valueList() {
        int bakTok = cTok;
        if(!value()) {
            return fail(bakTok, "Invalid Value for ValueList.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(",")) {
            return true;
        }
        cTok++;

        if(!valueList()) {
            return fail(bakTok, "Invalid ValueList for ValueList.");
        }
        return true;
    }

    private boolean value() {
        int bakTok = cTok;
        if(stringLiteral() || booleanLiteral() || floatLiteral() || integerLiteral()) {
            return true;
        }
        if(!tokens.get(cTok).equalsIgnoreCase("NULL")) {
            return fail(bakTok, "Invalid Value.");
        }
        cTok++;
        return true;
    }

    private boolean stringLiteral() {
        int bakTok = cTok;
        String regexStr = "^'[ a-zA-Z0-9!#$%&()*+,\\-./:;>=<?@\\[\\\\\\]^_`{}~]*'$";
        if(!tokens.get(cTok).matches(regexStr)) {
            return fail(bakTok, "Not StringLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean booleanLiteral() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("TRUE") &&
                !tokens.get(cTok).equalsIgnoreCase("FALSE")){
            return fail(bakTok, "Not BooleanLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean floatLiteral() {
        int bakTok = cTok;
        String regexStr = "^[-+]?[0-9]+.[0-9]+$";
        if(!tokens.get(cTok).matches(regexStr)) {
            return fail(bakTok, "Not FloatLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean integerLiteral() {
        int bakTok = cTok;
        String regexStr = "^[-+]?[0-9]$";
        if(!tokens.get(cTok).matches(regexStr)) {
            return fail(bakTok, "Not IntegerLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean select() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("SELECT")) {
            return fail(bakTok, "SELECT key word not found for Select.");
        }
        cTok++;

        if(!wildAttribList()) {
            return fail(bakTok, "Invalid WildAttribList for Select.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("FROM")) {
            return fail(bakTok, "FROM key word not found for Select.");
        }
        cTok++;

        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for Select.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("WHERE")) {
            return true;
        }
        cTok++;

        if(!condition()) {
            return fail(bakTok, "Invalid Condition for Select.");
        }
        return true;
    }

    private boolean wildAttribList() {
        int bakTok = cTok;
        if(attributeList()) {
            return true;
        }
        if(tokens.get(cTok).equals("*")) {
            return true;
        }
        return fail(bakTok, "Invalid WildAttribList.");
    }

    private boolean condition() {
        int bakTok = cTok;
        if(tokens.get(cTok).equals("(")) {
            cTok++;
            if(!condition()) {
                return fail(bakTok, "Invalid Condition for Condition.");
            }
            if(!tokens.get(cTok).equals(")")) {
                return fail(bakTok, "Missing ) for Condition.");
            }
            cTok++;
            return true;
        }

        if(condition()) {
            if(!boolOperator()) {
                return fail(bakTok, "Invalid BoolOperation for Condition.");
            }
            if(!condition()) {
                return fail(bakTok, "Invalid Condition for Condition.");
            }
            return true;
        }

        if(plainText()) {
            if(!comparator()) {
                return fail(bakTok, "Invalid Comparator for Condition.");
            }
            if(!plainText()) {
                return fail(bakTok, "Invalid PlainText for Condition.");
            }
            return true;
        }

        return fail(bakTok, "Invalid Condition.");
    }

    private boolean boolOperator() {
        int bakTok = cTok;
        if(tokens.get(cTok).equalsIgnoreCase("AND") ||
                tokens.get(cTok).equalsIgnoreCase("OR")) {
            cTok++;
            return true;
        }
        return fail(bakTok, "Not BoolOperator.");
    }

    private boolean comparator() {
        int bakTok = cTok;
        if(tokens.get(cTok).equalsIgnoreCase("==") ||
                tokens.get(cTok).equalsIgnoreCase(">") ||
                tokens.get(cTok).equalsIgnoreCase("<") ||
                tokens.get(cTok).equalsIgnoreCase(">=") ||
                tokens.get(cTok).equalsIgnoreCase("<=") ||
                tokens.get(cTok).equalsIgnoreCase("!=") ||
                tokens.get(cTok).equalsIgnoreCase("LIKE")) {
            return true;
        }
        return fail(bakTok, "Not Comparator.");
    }

    private boolean update() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("UPDATE")) {
            return fail(bakTok, "UPDATE key word not found for Update.");
        }
        cTok++;

        if(!plainText()) {
            return fail(bakTok, "Invalid PlainText for Update.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("SET")) {
            return fail(bakTok, "SET key word not found for Update.");
        }
        cTok++;

        if(!nameValueList()) {
            return fail(bakTok, "Invalid NameValueList for Update.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("WHERE")) {
            return fail(bakTok, "WHERE key word not found for Update.");
        }
        cTok++;

        if(!condition()) {
            return fail(bakTok, "Invalid Condition for Update.");
        }

        return true;
    }

    private boolean nameValueList() {
        int bakTok = cTok;
        if(!nameValuePair()) {
            return fail(bakTok, "Invalid NameValuePair for NameValueList.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(",")) {
            return true;
        }
        cTok++;

        if(!nameValueList()) {
            return fail(bakTok, "Invalid NameValueList for NameValueList.");
        }
        return true;
    }

    private boolean nameValuePair() {
        int bakTok = cTok;
        
    }

    private boolean plainText() {
        int bakTok = cTok;
        if(!tokens.get(cTok).matches("^[a-zA-Z0-9]+$")) {
            return fail(bakTok, "Not plaintext.");
        }
        cTok++;
        return true;
    }
}
