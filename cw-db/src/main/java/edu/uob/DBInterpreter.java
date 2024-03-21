package edu.uob;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DBInterpreter {
    private final static List<String> KEYWORDS = List.of(
            ";","use","create","database","table","(",")", "drop","alter",
            "insert","into","values","select","from","where","update","set", "delete",
            "join","and","on",",","=","add","true","false","null","*","or",
            "==",">","<",">=","<=","!=","like");
    private int cTok;
    private DBModel dbModel;
    private List<String> tokens;
    private String dataMessage;

    public DBInterpreter(DBModel dbModel) {
        this.dbModel = dbModel;
        cTok = 0;
        dataMessage = "";
    }

    private void writeDataMessage(List<List<String>> outputData) {
        if(outputData == null) return;
        for(List<String> outputRow : outputData) {
            dataMessage = dataMessage + "\n" + String.join("\t", outputRow);
        }
    }

    public String getDataMessage() {
        return dataMessage;
    }

    private void checkName(String name) throws DBException {
        if(KEYWORDS.contains(name.toLowerCase())) throw new DBException("name is a keyword");
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
        }
    }

    private void use() throws DBException {
        String databaseName = tokens.get(cTok++);
        dbModel.use(databaseName);
    }

    private void create() throws DBException {
        switch(tokens.get(cTok++).toLowerCase()) {
            case "database":
                createDatabase();
                break;
            case "table":
                createTable();
                break;
        }
    }

    private void createDatabase() throws DBException {
        String databaseName = tokens.get(cTok++);
        checkName(databaseName);
        dbModel.createDatabase(databaseName);
    }

    private void createTable() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        String tableName = tokens.get(cTok++);
        checkName(tableName);
        database.createTable(tableName);
        DBTable table = database.getTable(tableName);

        if(tokens.get(cTok++).equalsIgnoreCase("(")) {
            List<String> attributeList = getAttributeList();
            for(String attribute : attributeList) {
                table.alterAdd(attribute);
            }
            table.updateFile();
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
                break;
        }
    }

    private void alter() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        cTok++;
        String tableName = tokens.get(cTok++);
        DBTable table = database.getTable(tableName);
        String type = tokens.get(cTok++).toLowerCase();
        String attribute = tokens.get(cTok);
        checkName(attribute);
        switch(type) {
            case "add":
                table.alterAdd(attribute);
                break;
            case "drop":
                table.alterDrop(attribute);
                break;
        }
    }

    private void insert() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        cTok++;
        String tableName = tokens.get(cTok++);
        DBTable table = database.getTable(tableName);
        cTok+=2;
        List<String> valueList = getValueList();
        table.insert(valueList);
        table.updateFile();
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

        // collect the wildAttribList
        List<String> wildAttribList = new ArrayList<>();
        do {
            wildAttribList.add(tokens.get(cTok++));
        }
        while(tokens.get(cTok++).equalsIgnoreCase(","));

        // get the needed lists, also ensuring they are deep copies
        String tableName = tokens.get(cTok++);
        List<String> headings = new ArrayList<>(database.getTable(tableName).getHeadings());
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();
        List<List<String>> data = new ArrayList<>();
        for(List<String> row : database.getTable(tableName).getData()) data.add(new ArrayList<>(row));

        // filter the rows of data using conditions
        if(tokens.get(cTok++).equalsIgnoreCase("where")) {
            data = condition(headingsLC, data);
        }

        // select columns to be included by firstly check if there is such an attribute
        // then collect these attribute as output headings, and record the indices
        // then for each row from data, add the needed column values to output data
        // eventually, swap the names.
        if(!wildAttribList.contains("*")) {
            List<String> outputHeadings = new ArrayList<>();
            List<Integer> outputColumnIndices = new ArrayList<>();
            List<List<String>> outputData = new ArrayList<>();
            for(String attribute : wildAttribList) {
                String attributeLC = attribute.toLowerCase();
                int index = headingsLC.indexOf(attributeLC);
                if(index!=-1) {
                    outputHeadings.add(headings.get(index));
                    outputColumnIndices.add(index);
                }
            }
            for(List<String> rowData : data) {
                List<String> outputRowData = new ArrayList<>();
                for(int index : outputColumnIndices) {
                    outputRowData.add(rowData.get(index));
                }
                outputData.add(outputRowData);
            }
            headings = outputHeadings;
            data = outputData;
        }

        List<List<String>> output = new ArrayList<>();
        output.add(headings);
        output.addAll(data);
        writeDataMessage(output);
    }

    // based on the following definition:
    // <Condition>  ::=     "(" <Condition> ")"              | "(" <Condition> ")" <BoolOperator> <Condition> |
    //                      [PlainText] <Comparator> [Value] | [PlainText] <Comparator> [Value] <BoolOperator> <Condition>
    private List<List<String>> condition(List<String> headingsLC, List<List<String>> inputData) throws DBException {
        List<List<String>> outputData;
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

        String boolOperator = tokens.get(cTok).toLowerCase();
        switch(boolOperator) {
            case "and":
                cTok++;
                outputData = condition(headingsLC, outputData);
                break;
            case "or":
                cTok++;
                List<List<String>> outputData2 = condition(headingsLC, inputData);
                outputData2.removeAll(outputData);
                outputData.addAll(outputData2);
                outputData.sort(Comparator.comparing(l -> Integer.parseInt(l.get(0))));
                break;
        }
        return outputData;

    }

    private List<List<String>> compare(List<List<String>> inputData, int column, String comparator, String value) {
        String regexFloat = "^[-+]?[0-9]+.[0-9]+$";
        String regexInt = "^[-+]?[0-9]+$";
        List<List<String>> returnData = new ArrayList<>();

        switch(comparator) {
            case "==":
                for(List<String> rowData : inputData) {
                    if(rowData.get(column).equals(value)) returnData.add(rowData);
                }
                return returnData;
            case "like":
                for(List<String> rowData : inputData) {
                    if(rowData.get(column).contains(value)) returnData.add(rowData);
                }
                return returnData;
            case "!=":
                for(List<String> rowData : inputData) {
                    if(!rowData.get(column).equals(value)) returnData.add(rowData);
                }
                return returnData;
        }

        double fValue, fDataValue;
        for (List<String> rowData : inputData) {
            if (value.matches(regexFloat) || value.matches(regexInt)) {
                fValue = Float.parseFloat(value);
            } else {
                continue;
            }
            String dataValue = rowData.get(column);
            if (dataValue.matches(regexFloat) || dataValue.matches(regexInt)) {
                fDataValue = Float.parseFloat(dataValue);
            } else {
                continue;
            }

            switch (comparator) {
                case ">=":
                    if (fDataValue >= fValue) returnData.add(rowData);
                    break;
                case "<=":
                    if (fDataValue <= fValue) returnData.add(rowData);
                    break;
                case ">":
                    if (fDataValue > fValue) returnData.add(rowData);
                    break;
                case "<":
                    if (fDataValue < fValue) returnData.add(rowData);
                    break;
            }
        }
        return returnData;
    }

    private void update() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        // get the table
        String tableName = tokens.get(cTok++);
        DBTable table = database.getTable(tableName);

        // get NameValueList
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

        // get the headings and data, filter the rows of data with condition
        List<String> headings = table.getHeadings();
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();
        List<List<String>> data = table.getData();
        List<List<String>> filteredData = condition(headingsLC, data);

        // get the indices of the rows to be updated, use the table's method instead to update values
        List<String> idColumn = new ArrayList<>();
        for(List<String> rowData : data) idColumn.add(rowData.get(0));
        List<Integer> rowIndices = new ArrayList<>();
        for(List<String> filteredRowData : filteredData) {
            rowIndices.add(idColumn.indexOf(filteredRowData.get(0)));
        }

        table.update(rowIndices, nameValueList);
        table.updateFile();
    }

    private void delete() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        // get the table
        cTok++;
        String tableName = tokens.get(cTok++);
        DBTable table = database.getTable(tableName);
        cTok++;

        // get the headings and data of the table, filter the rows with conditions
        List<String> headings = table.getHeadings();
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();
        List<List<String>> data = table.getData();
        List<List<String>> filteredData = condition(headingsLC, data);

        // get the indices of the rows to be updated, use the table's method instead to update values
        List<String> idColumn = new ArrayList<>();
        for(List<String> rowData : data) idColumn.add(rowData.get(0));
        List<Integer> rowIndices = new ArrayList<>();
        for(List<String> filteredRowData : filteredData) {
            rowIndices.add(idColumn.indexOf(filteredRowData.get(0)));
        }

        table.delete(rowIndices);
        table.updateFile();
    }

    private void join() throws DBException {
        DBDatabase database = dbModel.getCurrentDatabase();
        if(database == null) throw new DBException("no database used");

        // get names and attributes
        String table1Name = tokens.get(cTok++);
        cTok++;
        String table2Name = tokens.get(cTok++);
        cTok++;
        String attribute1 = tokens.get(cTok++);
        String attribute1LC = attribute1.toLowerCase();
        cTok++;
        String attribute2 = tokens.get(cTok++);
        String attribute2LC = attribute2.toLowerCase();

        // get headings and data
        List<String> table1Headings = new ArrayList<>(database.getTable(table1Name).getHeadings());
        List<String> table2Headings = new ArrayList<>(database.getTable(table2Name).getHeadings());
        List<String> table1HeadingsLC = table1Headings.stream().map(String::toLowerCase).toList();
        List<String> table2HeadingsLC = table2Headings.stream().map(String::toLowerCase).toList();
        // to ensure they are deep copies...
        List<List<String>> table1Data = new ArrayList<>();
        for(List<String> row : database.getTable(table1Name).getData()) table1Data.add(new ArrayList<>(row));
        List<List<String>> table2Data = new ArrayList<>();
        for(List<String> row : database.getTable(table2Name).getData()) table2Data.add(new ArrayList<>(row));

        // calculate the index to be joined for each table
        int table1ColumnIndex = table1HeadingsLC.indexOf(attribute1LC);
        int table2ColumnIndex = table2HeadingsLC.indexOf(attribute2LC);

        // initialize the data for responding
        List<List<String>> outputData = new ArrayList<>();
        // now we can modify the headings to include the table name
        // and join them, with the first column being id
        table1Headings.replaceAll(s -> table1Name + "." + s);
        table1Headings.remove(table1ColumnIndex);
        if(table1ColumnIndex != 0) table1Headings.remove(0);
        table2Headings.replaceAll(s -> table2Name + "." + s);
        table2Headings.remove(table2ColumnIndex);
        if(table2ColumnIndex != 0) table2Headings.remove(0);
        List<String> outputHeadings = new ArrayList<>(List.of("id"));
        outputHeadings.addAll(table1Headings);
        outputHeadings.addAll(table2Headings);
        outputData.add(outputHeadings);

        // get the column of data from table 2
        List<String> table2Column = new ArrayList<>();
        for(List<String> table2Row : table2Data) {
            table2Column.add(table2Row.get(table2ColumnIndex));
        }

        // for each row in table 1, check if the value used for joining is contained by table 2
        // then delete the joining columns and append that row of table 2 to this row of table 1
        int id = 0;
        for(List<String> table1Row : table1Data) {
            String table1Value = table1Row.get(table1ColumnIndex);
            int table2JoinIndex = table2Column.indexOf(table1Value);
            if(table2JoinIndex!=-1) {
                table1Row.remove(table1ColumnIndex);
                if(table1ColumnIndex != 0) table1Row.remove(0);
                List<String> table2Row = table2Data.get(table2JoinIndex);
                table2Row.remove(table2ColumnIndex);
                if(table2ColumnIndex != 0) table2Row.remove(0);
                List<String> outputRow = new ArrayList<>(List.of(String.valueOf(id++)));
                outputRow.addAll(table1Row);
                outputRow.addAll(table2Row);
                outputData.add(outputRow);
            }
        }
        writeDataMessage(outputData);
    }
}
