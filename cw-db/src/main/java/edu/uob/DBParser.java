package edu.uob;


import java.util.*;

public class DBParser {
    private int cTok;
    private int failTok;
    private String failMessage;
    private List<String> tokens;

    public DBParser(List<String> tokens) {
        this.tokens = tokens;
        cTok = 0;
        failTok = 0;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public boolean parse() {
        if(tokens.isEmpty()) {
            failMessage = "Empty Command.";
            return false;
        }
        return command();
    }

    private boolean parseFail(int bakTok, String str) {
        if(cTok >= failTok) {
            failTok = cTok;
            failMessage = "Invalid command. \n\t" +
                    "Parser error around token with index " + failTok + ": " +
                    tokens.get(failTok) + ". \n\t" + str;
        }
        cTok = bakTok;
        return false;
    }

    private boolean command() {
        if(!tokens.get(tokens.size()-1).equals(";")) {
            cTok = tokens.size()-1;
            return parseFail(cTok, "Not ; at the end of command.");
        }
        if(!commandType()) {
            return false;
        }

        int bakTok = cTok;
        if(!tokens.get(cTok).equals(";")) {
            return parseFail(bakTok, "Not ; at the end of a valid command.");
        }
        cTok++;

        if(tokens.size() > cTok) {
            return parseFail(bakTok, "Multiple ; token.");
        }
        return true;
    }

    private boolean commandType() {
        int bakTok = cTok;
        if(use() || create() || drop() || alter() || insert() || select() ||
                update() || delete() || join()) {
            return true;
        }
        return parseFail(bakTok, "Invalid CommandType.");
    }

    private boolean use() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("USE")) {
            return parseFail(bakTok, "USE key word not found for Use.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Use.");
        }

        return true;
    }

    private boolean plainText() {
        int bakTok = cTok;
        if(!tokens.get(cTok).matches("^[a-zA-Z0-9]+$")) {
            return parseFail(bakTok, "Not plaintext.");
        }
        cTok++;
        return true;
    }
    
    private boolean create() {
        int bakTok = cTok;
        if(!createDatabase() && !createTable()) {
            return parseFail(bakTok, "Invalid Create.");
        }
        return true;
    }
    
    private boolean createDatabase() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("CREATE")) {
            return parseFail(bakTok, "CREATE key word not found for CreateDatabase.");
        }
        cTok++;
        if(!tokens.get(cTok).equalsIgnoreCase("DATABASE")) {
            return parseFail(bakTok, "DATABASE key word not found for CreateDatabase.");
        }
        cTok++;
        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for CreateDatabase.");
        }
        return true;
    }
    
    private boolean createTable() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("CREATE")) {
            return parseFail(bakTok, "CREATE key word not found for CreateTable.");
        }
        cTok++;
        if(!tokens.get(cTok).equalsIgnoreCase("TABLE")) {
            return parseFail(bakTok, "TABLE key word not found for CreateTable.");
        }
        cTok++;
        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for CreateTable.");
        }
        
        if(!tokens.get(cTok).equalsIgnoreCase("(")) {
            return true;
        }
        cTok++;

        bakTok = cTok;
        if(!attributeList()) {
            return parseFail(bakTok, "Invalid AttributeList for CreateTable.");
        }
        if(!tokens.get(cTok).equalsIgnoreCase(")")) {
            return parseFail(bakTok, "Missing ) for CreateTable.");
        }
        cTok++;
        return true;
    }

    private boolean attributeList() {
        int bakTok = cTok;
        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for AttributeList.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(",")) {
            return true;
        }
        cTok++;

        if(!attributeList()) {
            return parseFail(bakTok, "Invalid AttributeList for AttributeList.");
        }
        return true;
    }

    private boolean drop() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("DROP")) {
            return parseFail(bakTok, "DROP key word not found for Drop.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("DATABASE") &&
                !tokens.get(cTok).equalsIgnoreCase("TABLE")) {
            return parseFail(bakTok, "No DATABASE or TABLE for Drop.");
        }
        cTok++;
        
        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Drop.");
        }
        return true;
    }

    private boolean alter() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("ALTER")) {
            return parseFail(bakTok, "ALTER key word not found for Alter.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("TABLE")) {
            return parseFail(bakTok, "TABLE key word not found for Alter.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Alter.");
        }
        
        if(!alterationType()) {
            return parseFail(bakTok, "Invalid AlterationType for Alter.");
        }

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Alter.");
        }
        return true;
    }
    
    private boolean alterationType() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("ADD") &&
                !tokens.get(cTok).equalsIgnoreCase("DROP")) {
            return parseFail(bakTok, "No ADD or DROP key word for AlterationType.");
        }
        cTok++;
        return true;
    }
    
    private boolean insert() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("INSERT")) {
            return parseFail(bakTok, "INSERT key word not found for Insert.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("INTO")) {
            return parseFail(bakTok, "INTO key word not found for Insert.");
        }
        cTok++;
        
        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Insert.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("VALUES")) {
            return parseFail(bakTok, "VALUES key word not found for Insert.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("(")) {
            return parseFail(bakTok, "( not found for Insert.");
        }
        cTok++;

        if(!valueList()) {
            return parseFail(bakTok, "Invalid ValueList for Insert.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(")")) {
            return parseFail(bakTok, ") not found for Insert.");
        }
        cTok++;
        return true;
    }

    private boolean valueList() {
        int bakTok = cTok;
        if(!value()) {
            return parseFail(bakTok, "Invalid Value for ValueList.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(",")) {
            return true;
        }
        cTok++;

        if(!valueList()) {
            return parseFail(bakTok, "Invalid ValueList for ValueList.");
        }
        return true;
    }

    private boolean value() {
        int bakTok = cTok;
        if(stringLiteral() || booleanLiteral() || floatLiteral() || integerLiteral()) {
            return true;
        }
        if(!tokens.get(cTok).equalsIgnoreCase("NULL")) {
            return parseFail(bakTok, "Invalid Value.");
        }
        cTok++;
        return true;
    }

    private boolean stringLiteral() {
        int bakTok = cTok;
        String regexStr = "^'[ a-zA-Z0-9!#$%&()*+,\\-./:;>=<?@\\[\\\\\\]^_`{}~]*'$";
        if(!tokens.get(cTok).matches(regexStr)) {
            return parseFail(bakTok, "Not StringLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean booleanLiteral() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("TRUE") &&
                !tokens.get(cTok).equalsIgnoreCase("FALSE")){
            return parseFail(bakTok, "Not BooleanLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean floatLiteral() {
        int bakTok = cTok;
        String regexStr = "^[-+]?[0-9]+.[0-9]+$";
        if(!tokens.get(cTok).matches(regexStr)) {
            return parseFail(bakTok, "Not FloatLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean integerLiteral() {
        int bakTok = cTok;
        String regexStr = "^[-+]?[0-9]+$";
        if(!tokens.get(cTok).matches(regexStr)) {
            return parseFail(bakTok, "Not IntegerLiteral.");
        }
        cTok++;
        return true;
    }

    private boolean select() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("SELECT")) {
            return parseFail(bakTok, "SELECT key word not found for Select.");
        }
        cTok++;

        if(!wildAttribList()) {
            return parseFail(bakTok, "Invalid WildAttribList for Select.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("FROM")) {
            return parseFail(bakTok, "FROM key word not found for Select.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Select.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("WHERE")) {
            return true;
        }
        cTok++;

        if(!condition()) {
            return parseFail(bakTok, "Invalid Condition for Select.");
        }
        return true;
    }

    private boolean wildAttribList() {
        int bakTok = cTok;
        if(attributeList()) {
            return true;
        }
        if(tokens.get(cTok).equals("*")) {
            cTok++;
            return true;
        }
        return parseFail(bakTok, "Invalid WildAttribList.");
    }

    private boolean condition() {
        int bakTok = cTok;
        if(tokens.get(cTok).equals("(")) {
            cTok++;
            if(!condition()) {
                return parseFail(bakTok, "Invalid Condition for Condition.");
            }
            if(!tokens.get(cTok).equals(")")) {
                return parseFail(bakTok, "Missing ) for Condition.");
            }
            cTok++;
        }
        else if(plainText()) {
            if(!comparator()) {
                return parseFail(bakTok, "Invalid Comparator for Condition.");
            }
            if(!value()) {
                return parseFail(bakTok, "Invalid PlainText for Condition.");
            }
        }
        else {
            return parseFail(bakTok, "Invalid PlainText for Condition.");
        }

        if(!boolOperator()) {
            return true;
        }

        if(!condition()) {
            return parseFail(bakTok, "Invalid Condition for Condition.");
        }
        return true;
    }

    private boolean boolOperator() {
        int bakTok = cTok;
        if(tokens.get(cTok).equalsIgnoreCase("AND") ||
                tokens.get(cTok).equalsIgnoreCase("OR")) {
            cTok++;
            return true;
        }
        return parseFail(bakTok, "Not BoolOperator.");
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
            cTok++;
            return true;
        }
        return parseFail(bakTok, "Not Comparator.");
    }

    private boolean update() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("UPDATE")) {
            return parseFail(bakTok, "UPDATE key word not found for Update.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Update.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("SET")) {
            return parseFail(bakTok, "SET key word not found for Update.");
        }
        cTok++;

        if(!nameValueList()) {
            return parseFail(bakTok, "Invalid NameValueList for Update.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("WHERE")) {
            return parseFail(bakTok, "WHERE key word not found for Update.");
        }
        cTok++;

        if(!condition()) {
            return parseFail(bakTok, "Invalid Condition for Update.");
        }

        return true;
    }

    private boolean nameValueList() {
        int bakTok = cTok;
        if(!nameValuePair()) {
            return parseFail(bakTok, "Invalid NameValuePair for NameValueList.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase(",")) {
            return true;
        }
        cTok++;

        if(!nameValueList()) {
            return parseFail(bakTok, "Invalid NameValueList for NameValueList.");
        }

        return true;
    }

    private boolean nameValuePair() {
        int bakTok = cTok;
        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for NameValuePair.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("=")) {
            return parseFail(bakTok, "Missing = for NameValuePair");
        }
        cTok++;

        if(!value()) {
            return parseFail(bakTok, "Invalid Value for NameValuePair.");
        }

        return true;
    }

    private boolean delete() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("DELETE")) {
            return parseFail(bakTok, "DELETE key word not found for Delete.");
        }
        cTok++;

        if(!tokens.get(cTok).equalsIgnoreCase("FROM")) {
            return parseFail(bakTok, "FROM key word not found for Delete.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Delete.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("WHERE")) {
            return parseFail(bakTok, "WHERE key word not found for Delete.");
        }
        cTok++;

        if(!condition()) {
            return parseFail(bakTok, "Invalid Condition for Delete.");
        }

        return true;
    }

    private boolean join() {
        int bakTok = cTok;
        if(!tokens.get(cTok).equalsIgnoreCase("JOIN")) {
            return parseFail(bakTok, "JOIN key word not found for Join.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Join.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("AND")) {
            return parseFail(bakTok, "AND key word not found for Join.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Join.");
        }

        if(!tokens.get(cTok).equalsIgnoreCase("ON")) {
            return parseFail(bakTok, "ON key word not found for Join.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Join.");
        }
        if(!tokens.get(cTok).equalsIgnoreCase("AND")) {
            return parseFail(bakTok, "AND key word not found for Join.");
        }
        cTok++;

        if(!plainText()) {
            return parseFail(bakTok, "Invalid PlainText for Join.");
        }

        return true;
    }
}
