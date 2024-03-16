package edu.uob;

import java.io.*;
import java.util.*;

public class DBTable {
    File tableFile;
    String tableName;
    int numOfRows;
    int numOfColumns;
    List<String> headings;
    List<List<String>> data;

    public void createDBTable(File file) throws DBException {
        tableFile = file;
        tableName = file.getName();
        if(file.exists()) createDBTableError("file already exists");
        try{
            if(!tableFile.createNewFile()) createDBTableError("cannot create file");
        }
        catch(IOException e) {
            createDBTableError("IO exception " + e);
        }
        numOfRows = 0;
        numOfColumns = 0;
        headings = new ArrayList<>();
        data = new ArrayList<>();
    }

    private void createDBTableError(String str) throws DBException {
        throw new DBException("Failed to create table " + tableName + ", " + str + ": " + tableFile);
    }

    public void loadDBTable(File file) throws DBException {
        tableFile = file;
        tableName = file.getName();
        if(!tableFile.isFile()) loadDBTableError("path is not a file");
        if(!tableFile.canRead()) loadDBTableError("cannot read file");

        try (FileReader reader = new FileReader(tableFile);
             BufferedReader buffReader = new BufferedReader(reader);) {

            String line=buffReader.readLine();
            if(line == null) loadDBTableError("empty file");
            else {
                headings = new ArrayList<>(List.of(line.split("\t")));
                numOfColumns = headings.size();
            }
            if(!headings.get(0).equals("id")) loadDBTableError("file format incorrect");

            data = new ArrayList<>();
            numOfRows = 0;
            for(line=buffReader.readLine(); line!=null; line=buffReader.readLine()) {
                List<String> lineList = new ArrayList<>(List.of(line.split("\t")));
                if(lineList.size() != numOfColumns) loadDBTableError("wrong number of columns in next row ");
                data.add(lineList);
            }
        }
        catch(IOException e) {
            loadDBTableError("IO exception" + e );
        }
    }

    private void loadDBTableError(String str) throws DBException {
        throw new DBException("Failed to load table " + tableName + ", " +
                str + ": " + tableFile);
    }

    public void drop() throws DBException {
        if(!tableFile.delete()) {
            throw new DBException("Failed to delete table " + tableName + ": " + tableFile);
        }
    }
}
