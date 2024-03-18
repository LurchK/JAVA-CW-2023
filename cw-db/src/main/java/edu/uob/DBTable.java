package edu.uob;

import java.io.*;
import java.util.*;

public class DBTable {
    private File tableFile;
    private String tableName;
    private String COLUMNDELIMITER = "\t";
    private String LINEOFDELETED = "DELETED";
    private int numOfRows;
    private int numOfColumns;
    private List<String> headings;
    private List<List<String>> data;

    public void create(File file) throws DBException {
        tableFile = file;
        tableName = file.getName();
        if(file.exists()) createError("file already exists");
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
        throw new DBException("Failed to create table " +
                tableName + ", " + str + ":\n\t" + tableFile);
    }

    public void load(File file) throws DBException {
        tableFile = file;
        tableName = file.getName();
        if(!tableFile.exists()) createError("file does not exist");
        if(!tableFile.isFile()) loadError("path is not a file");
        if(!tableFile.canRead()) loadError("cannot read file");

        try(BufferedReader buffReader = new BufferedReader(new FileReader(tableFile))) {

            String line = buffReader.readLine();
            if(line == null) loadError("empty header");
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
        throw new DBException("Failed to load table " +
                tableName + ", " + str + ":\n\t" + tableFile);
    }

    public void drop() throws DBException {
        if(!tableFile.exists()) dropError("file does not exist");
        if(!tableFile.canWrite()) dropError("doesn't have write permission");
        if(!tableFile.delete()) dropError("failed to delete file");
    }

    private void dropError(String str) throws DBException {
        throw new DBException("Failed to drop table " + tableName +
                ", " + str + ":\n\t" + tableFile);
    }

    public void updateFile() throws DBException {
        if(!tableFile.exists()) dropError("file does not exist");
        if(!tableFile.canWrite()) dropError("doesn't have write permission");
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

    public void updateFileError(String str) throws DBException {
        throw new DBException("Failed to update file for table " + tableName +
                ", " + str + ":\n\t" + tableFile);
    }

    public void alterAdd(String attribute) throws DBException {
        if(headings.contains(attribute)) alterError("attribute '" + attribute + "' already exists");
        headings.add(attribute);
        numOfColumns++;

        for(List<String> rowData : data) {
            rowData.add("");
        }
    }

    public void alterDrop(String attribute) throws DBException {
        if(!headings.contains(attribute)) alterError(("attribute '" + attribute + "' does not exist"));
        int index = headings.indexOf(attribute);
        headings.remove(attribute);
        numOfColumns--;

        for(List<String> rowData : data) {
            rowData.remove(index);
        }
    }

    private void alterError(String str) throws DBException{
        throw new DBException("Failed to alter table " + tableName +
                ", " + str + ":\n\t" + tableFile);
    }

    public void insert(String[] insertData) throws DBException{
        if(insertData.length != numOfColumns-1) insertError("incorrect number of values");

        List<String> insertRow = new ArrayList<>();
        insertRow.add(String.valueOf(numOfRows));
        insertRow.addAll(List.of(insertData));

        data.add(insertRow);
        numOfRows++;
    }

    private void insertError(String str) throws DBException {
        throw new DBException("Failed to insert values into table " + tableName +
                ", " + str + ":\n\t" + tableFile);
    }

    public List<List<String>> select(List<String> attributeList) {
        List<List<String>> returnList = new ArrayList<>();

        if(attributeList.contains("*")) {
            returnList.add(headings);
            returnList.addAll(data);
            return returnList;
        }

        List<String> returnHeader = new ArrayList<>(numOfColumns);
        List<Integer> indicesOfHeadings = new ArrayList<>(numOfColumns);

        for(String attribute : attributeList) {
            if(headings.contains(attribute)) {
                returnHeader.add(attribute);
                indicesOfHeadings.add(headings.indexOf(attribute));
            }
        }
        returnList.add(returnHeader);

        for(List<String> rowData : data) {
            List<String> returnData = new ArrayList<>(headings.size());
            for(int index : indicesOfHeadings) {
                returnData.add(rowData.get(index));
            }
            returnList.add(returnData);
        }

        return returnList;
    }

    public void update() {

    }

    public void delete() throws DBException {

    }

    private void deleteError(String str) throws DBException {
        throw new DBException("Failed to delete value in table " + tableName +
                ", " + str + ":\n\t" + tableFile);
    }

}
