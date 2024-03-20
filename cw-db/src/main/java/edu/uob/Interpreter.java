package edu.uob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Interpreter {
    private int cTok;
    private DBModel dbModel;
    private List<String> tokens;
    private String dataMessage;

    public Interpreter(DBModel dbModel) {
        this.dbModel = dbModel;
        cTok = 0;
        dataMessage = "";
    }

    public String getDataMessage() {
        return dataMessage;
    }

    public void interpret(List<String> tokens) throws DBException {
        this.tokens = tokens;

        switch(tokens.get(cTok++).toLowerCase()) {
            case "use":
                use();
                break;
            case "create":
                create();
                break;
            case "drop":
                drop();
                break;
            case "alter":
                alter();
                break;
            case "insert":
                insert();
                break;
            case "select":
                select();
                break;
            case "update":
                update();
                break;
            case "delete":
                delete();
                break;
            case "join":
                join();
                break;
            default:
                throw new DBException("failed to interpret");
        }
    }

    private void use() throws DBException {
        dbModel.use(tokens.get(cTok++));
    }
    private void create() throws DBException {
        switch(tokens.get(cTok++).toLowerCase()) {
            case "database":
                createDatabase();
                break;
            case "table":
                createTable();
        }
    }

    private void createDatabase() throws DBException {
        dbModel.createDatabase(tokens.get(cTok++));
    }

    private void createTable() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        String tableName = tokens.get(cTok++);
        database.createTable(tableName);

        if(tokens.get(cTok++).equalsIgnoreCase("(")) {
            List<String> attributeList = getAttributeList();
            for(String attribute : attributeList) {
                database.alterAdd(tableName, attribute);
            }
            database.updateFiles();
        }
    }

    private List<String> getAttributeList() {
        List<String> attributeList = new ArrayList<>();
        do {
            attributeList.add(tokens.get(cTok++));
        }
        while(tokens.get(cTok++).equalsIgnoreCase(","));
        cTok--;
        return attributeList;
    }

    private void drop() throws DBException {
        switch(tokens.get(cTok++).toLowerCase()) {
            case "database":
                dbModel.dropDatabase(tokens.get(cTok));
                break;
            case "table":
                DBDatabase database = dbModel.getCurrentDatabase();
                if(database == null) throw new DBException("no database used");

                database.dropTable(tokens.get(cTok));
        }
    }

    private void alter() throws DBException {
        cTok++;
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        String tableName = tokens.get(cTok++);
        switch(tokens.get(cTok++).toLowerCase()) {
            case "add":
                database.alterAdd(tableName, tokens.get(cTok));
                break;
            case "drop":
                database.alterDrop(tableName, tokens.get(cTok));
                break;
        }
    }

    private void insert() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        cTok++;
        String tableName = tokens.get(cTok++);
        cTok+=2;
        List<String> valueList = getValueList();
        database.insert(tableName, valueList);
        database.updateFiles();
    }

    private List<String> getValueList() {
        List<String> valueList = new ArrayList<>();
        do {
            valueList.add(getValue());
        }
        while(tokens.get(cTok++).equalsIgnoreCase(","));
        cTok--;
        return valueList;
    }

    private String getValue() {
        String value = tokens.get(cTok++);
        if(value.matches("^'.*'$")){
            value = value.substring(1, value.length()-1);
        }
        return value;
    }

    private void select() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        List<String> wildAttribList = new ArrayList<>();
        do {
            wildAttribList.add(tokens.get(cTok++));
        }
        while(tokens.get(cTok++).equalsIgnoreCase(","));

        String tableName = tokens.get(cTok++);
        List<List<String>> outputData = database.select(tableName, wildAttribList);
        if(tokens.get(cTok++).equalsIgnoreCase("where")) {
            List<String> headings = outputData.get(0);
            List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();;
            List<List<String>> data = outputData.subList(1, outputData.size());
            data = condition(headingsLC, data);
            data.add(0, headings);
            outputData = data;
        }
        writeDataMessage(outputData);
    }

    private List<List<String>> condition(List<String> headingsLC, List<List<String>> inputData) throws DBException {
        List<List<String>> outputData = new ArrayList<>();
        if(tokens.get(cTok).equals("(")) {
            cTok++;
            outputData = condition(headingsLC, inputData);
            cTok++;
        }
        else {
            String heading = tokens.get(cTok++);
            String headingLC = heading.toLowerCase();
            if(!headingsLC.contains(headingLC)) throw new DBException("table does not contain attribute: " + heading);

            int column = headingsLC.indexOf(headingLC);
            String comparator = tokens.get(cTok++).toLowerCase();
            String value = getValue();
            outputData = compare(inputData, column, comparator, value);
        }

        String boolOperator = tokens.get(cTok++).toLowerCase();
        switch(boolOperator) {
            case "and":
                outputData = condition(headingsLC, outputData);
                break;
            case "or":
                List<List<String>> outputData2 = condition(headingsLC, inputData);
                outputData2.removeAll(outputData);
                outputData.addAll(outputData2);
                outputData.sort(Comparator.comparing(l -> l.get(0)));
        }
        return outputData;

    }

    private List<List<String>> compare(List<List<String>> inputData, int column, String comparator, String value) {
        String regexFloat = "^[-+]?[0-9]+.[0-9]+$";
        String regexInt = "^[-+]?[0-9]+$";
        List<List<String>> returnData = new ArrayList<>();
        double fvalue = 0, fdataValue = 0;

        switch(comparator) {
            case "==":
                return inputData.stream()
                        .filter(list -> list.get(column).equals(value))
                        .toList();
            case "like":
                return inputData.stream()
                        .filter(list -> list.get(column).matches(value))
                        .toList();
            case "!=":
                return inputData.stream()
                        .filter(list -> !list.get(column).equals(value))
                        .toList();
        }

        for (List<String> rowData : inputData) {
            if (value.matches(regexFloat) || value.matches(regexInt)) {
                fvalue = Float.parseFloat(value);
            } else {
                continue;
            }
            String dataValue = rowData.get(column);
            if (dataValue.matches(regexFloat) || dataValue.matches(regexInt)) {
                fdataValue = Float.parseFloat(dataValue);
            } else {
                continue;
            }

            switch (comparator) {
                case ">=":
                    if (fdataValue >= fvalue) returnData.add(rowData);
                    break;
                case "<=":
                    if (fdataValue <= fvalue) returnData.add(rowData);
                    break;
                case ">":
                    if (fdataValue > fvalue) returnData.add(rowData);
                    break;
                case "<":
                    if (fdataValue < fvalue) returnData.add(rowData);
                    break;
            }
        }
        return returnData;
    }

    private void writeDataMessage(List<List<String>> outputData) {
        if(outputData == null) return;
        for(List<String> outputRow : outputData) {
            dataMessage = dataMessage + "\n" + String.join("\t", outputRow);
        }
    }

    private void update() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        String tableName = tokens.get(cTok++);
        cTok++;
        List<List<String>> nameValueList = new ArrayList<>();
        do {
            List<String> nameValuePair = new ArrayList<>();
            nameValuePair.add(tokens.get(cTok++));
            cTok++;
            nameValuePair.add(tokens.get(cTok++));
            nameValueList.add(nameValuePair);
        }
        while(tokens.get(cTok++).equalsIgnoreCase(","));

        List<List<String>> wholeTableData = database.select(tableName, List.of("*"));
        List<String> headings = wholeTableData.get(0);
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();;
        List<List<String>> data = wholeTableData.subList(1,wholeTableData.size());
        data = condition(headingsLC, data);

        List<Integer> rowIndices = new ArrayList<>();
        for(List<String> rowData : data) {
            rowIndices.add(Integer.parseInt(rowData.get(0)));
        }

        database.update(tableName, rowIndices, nameValueList);
        database.updateFiles();
    }

    private void delete() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        cTok++;
        String tableName = tokens.get(cTok++);
        cTok++;

        List<List<String>> wholeTableData = database.select(tableName, List.of("*"));
        List<String> headings = wholeTableData.get(0);
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();;
        List<List<String>> data = wholeTableData.subList(1,wholeTableData.size());
        data = condition(headingsLC, data);

        List<Integer> rowIndices = new ArrayList<>();
        for(List<String> rowData : data) {
            rowIndices.add(Integer.parseInt(rowData.get(0)));
        }

        database.delete(tableName, rowIndices);
        database.updateFiles();
    }

    private void join() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        String table1Name = tokens.get(cTok++);
        cTok++;
        String table2Name = tokens.get(cTok++);
        cTok++;
        String attribute1 = tokens.get(cTok++);
        cTok++;
        String attribute2 = tokens.get(cTok++);


    }
}
