package edu.uob;

import java.io.*;
import java.util.*;

public class DBTable {
    private File tableFile;
    private String tableName;
    public final static String FILEEXTENSION = ".tab";
    public final static String COLUMNDELIMITER = "\t";
    public final static String LINEOFDELETED = "DELETED";
    public final static String EMPTY = "NULL";
    private int numOfRows;
    private int numOfColumns;
    private List<String> headings;
    private List<List<String>> data;

    public void create(File file) throws DBException {
        if(file == null) createError("null input");
        tableName = file.getName().toLowerCase().replaceAll(FILEEXTENSION, "");
        tableFile = new File(file.getParent(), tableName + FILEEXTENSION);
        if(tableFile.exists()) createError("file already exists");
        try{
            if(!tableFile.createNewFile()) createError("cannot create file");
        }
        catch(IOException e) {
            createError("IO exception " + e);
        }
        numOfRows = 0;
        numOfColumns = 1;
        headings = new ArrayList<>();
        headings.add("id");
        data = new ArrayList<>();
    }

    private void createError(String str) throws DBException {
        throw new DBException("failed to create table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    public void load(File file) throws DBException {
        if(file == null) loadError("null input");
        tableFile = file;
        tableName = file.getName().replaceAll(FILEEXTENSION, "");
        if(tableName.matches("[A-Z]")) loadError("name contains uppercase letter");
        if(!tableFile.exists()) loadError("file does not exist");
        if(!tableFile.isFile()) loadError("path is not a file");
        if(!tableFile.canRead()) loadError("cannot read file");

        try(BufferedReader buffReader = new BufferedReader(new FileReader(tableFile))) {

            String line = buffReader.readLine();
            if(line == null || line.isBlank()) loadError("empty header");
            else {
                headings = new ArrayList<>(List.of(line.split(COLUMNDELIMITER)));
                numOfColumns = headings.size();
            }
            if(!headings.get(0).equalsIgnoreCase("id")) loadError("incorrect file format");

            data = new ArrayList<>();
            numOfRows = 0;
            for(line=buffReader.readLine(); line!=null; line=buffReader.readLine()) {
                if(line.trim().equalsIgnoreCase(LINEOFDELETED)) {
                    numOfRows++;
                    continue;
                }
                String[] rowData = line.split(COLUMNDELIMITER);
                if(rowData.length != numOfColumns || !rowData[0].matches("^ *[0-9]+ *$")) {
                    continue;
                }
                data.add(new ArrayList<>(List.of(rowData)));
                numOfRows++;
            }
        }
        catch(IOException e) {
            loadError("IO exception" + e );
        }
    }

    private void loadError(String str) throws DBException {
        throw new DBException("failed to load table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    public void drop() throws DBException {
        if(!tableFile.exists()) dropError("file does not exist");
        if(!tableFile.canWrite()) dropError("doesn't have write permission");
        if(!tableFile.delete()) dropError("failed to delete file");
        tableFile = null;
        tableName = null;
    }

    private void dropError(String str) throws DBException {
        throw new DBException("failed to drop table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    public void updateFile() throws DBException {
        if(!tableFile.exists()) updateFileError("file does not exist");
        if(!tableFile.canWrite()) updateFileError("doesn't have write permission");
        try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(tableFile))) {
            buffWriter.write(String.join(COLUMNDELIMITER, headings));
            buffWriter.newLine();
            for(List<String> rowData : data) {
                buffWriter.write(String.join(COLUMNDELIMITER, rowData));
                buffWriter.newLine();
            }
        }
        catch (IOException ioe) {
            updateFileError("IO error");
        }
    }

    private void updateFileError(String str) throws DBException {
        throw new DBException("failed to update file for table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    public void alterAdd(String attribute) throws DBException {
        if(attribute == null) alterError("null input");
        if(attribute.isBlank()) alterError("blank input");
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();
        String attributeLC = attribute.toLowerCase();
        if(headingsLC.contains(attributeLC)) alterError("attribute '" + attribute + "' already exists");
        headings.add(attribute);
        numOfColumns++;

        for(List<String> rowData : data) {
            rowData.add(EMPTY);
        }
    }

    public void alterDrop(String attribute) throws DBException {
        if(attribute == null) alterError("null input");
        if(attribute.isBlank()) alterError("blank input");
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();
        String attributeLC = attribute.toLowerCase();
        if(!headingsLC.contains(attributeLC)) alterError(("attribute '" + attribute + "' does not exist"));
        int index = headingsLC.indexOf(attributeLC);
        headings.remove(index);
        numOfColumns--;

        for(List<String> rowData : data) {
            rowData.remove(index);
        }
    }

    private void alterError(String str) throws DBException{
        throw new DBException("failed to alter table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    public void insert(List<String> insertData) throws DBException{
        if(insertData == null) insertError("null input");
        if(insertData.size() != numOfColumns-1) insertError("incorrect number of values");

        List<String> insertRow = new ArrayList<>();
        insertRow.add(String.valueOf(numOfRows));
        insertRow.addAll(insertData);

        data.add(insertRow);
        numOfRows++;
    }

    private void insertError(String str) throws DBException {
        throw new DBException("failed to insert values into table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    public List<List<String>> select(List<String> attributeList) {
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();
        List<String> attributeListLC = attributeList.stream().map(String::toLowerCase).toList();
        List<List<String>> returnList = new ArrayList<>();

        if(attributeListLC.contains("*")) {
            returnList.add(headings);
            returnList.addAll(data);
            return returnList;
        }

        List<String> returnHeader = new ArrayList<>(numOfColumns);
        List<Integer> indicesOfHeadings = new ArrayList<>(numOfColumns);

        for(String attribute : attributeList) {
            String attributeLC = attribute.toLowerCase();
            if(headingsLC.contains(attributeLC)) {
                returnHeader.add(attribute);
                indicesOfHeadings.add(headingsLC.indexOf(attributeLC));
            }
        }
        returnList.add(returnHeader);

        for(List<String> rowData : data) {
            List<String> returnData = new ArrayList<>(returnHeader.size());
            for(int index : indicesOfHeadings) {
                returnData.add(rowData.get(index));
            }
            returnList.add(returnData);
        }

        return returnList;
    }

    public void update(List<Integer> rows, List<List<String>> nameValueList) throws DBException {
        List<String> headingsLC = headings.stream().map(String::toLowerCase).toList();
        for(int row : rows) {
            if(row<0 || row >= numOfRows) updateError("invalid row index");
        }
        for(List<String> nameValuePair : nameValueList) {
            if(nameValuePair.size() != 2) updateError("invalid size of name value pair");
            String key = nameValuePair.get(0);
            if(key == null || key.isBlank() || !headingsLC.contains(key.toLowerCase())) updateError("invalid key");
            if(key.equalsIgnoreCase("id")) updateError("id update is not permitted");
        }

        for(int row : rows) {
            for(List<String> nameValuePair : nameValueList) {
                String key = nameValuePair.get(0);
                String value = nameValuePair.get(1);
                if(value == null) value = "NULL";
                int column = headingsLC.indexOf(key.toLowerCase());
                data.get(row).set(column, value);
            }
        }
    }

    private void updateError(String str) throws DBException {
        throw new DBException("failed to update value in table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    public void delete(List<Integer> rows) throws DBException {
        for(int row : rows) {
            if(row<0 || row >= numOfRows) deleteError("invalid row index");
        }
        for(int row : rows) {
            data.set(row, new ArrayList<>(List.of(LINEOFDELETED)));
        }
    }

    private void deleteError(String str) throws DBException {
        throw new DBException("failed to delete value in table " + tableName + ":\n\t" +
                tableFile + ",\n\t" +
                str);
    }

    // The following methods are functionalities which are not shown in the BNF document

    public File getFile() {
        return tableFile;
    }

    public String getName() {
        return tableName;
    }

    public List<String> getHeadings() {
        return headings;
    }
}
