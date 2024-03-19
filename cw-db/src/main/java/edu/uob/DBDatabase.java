package edu.uob;

import java.io.*;
import java.util.*;

public class DBDatabase {
    private File databaseDir;
    private String databaseName;
    private Map<String,DBTable> tables;

    public void create(File dir) throws DBException {
        if(dir == null) createError("null input");
        databaseDir = dir;
        databaseName = dir.getName();
        if(dir.exists()) createError("path already exists");
        if(!dir.mkdir()) createError("cannot create folder");

        tables = new HashMap<>();
    }

    private void createError(String str) throws DBException {
        throw new DBException("failed to create database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void createTable(String tableName) throws DBException {
        if(tableName == null) createTableError("null input");
        if(tableName.isBlank()) createTableError("empty table name");
        List<String> tableNames = tables.keySet().stream().map(String::toLowerCase).toList();;
        if(tableNames.contains(tableName.toLowerCase())) {
            createTableError("table with name " + tableName + " already exists");
        }

        try {
            DBTable table = new DBTable();
            File tableFile = new File(databaseDir, tableName + DBTable.FILEEXTENSION);
            table.create(tableFile);
            tables.put(table.getTableName(),table);
        }
        catch (DBException dbe) {
            createTableError(dbe.getMessage());
        }
    }

    public void createTable(String tableName, List<String> attributeList) throws DBException {
        createTable(tableName);
        try {

        }
    }

    private void createTableError(String str) throws DBException {
        throw new DBException("failed to create table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void load(File dir) throws DBException {
        if(dir == null) loadError("null input");
        databaseDir = dir;
        databaseName = dir.getName();
        if(!dir.exists()) loadError("path does not exist");
        if(!dir.isDirectory()) loadError("path is not a directory");
        if(!dir.canRead()) loadError("cannot read path");

        tables = new HashMap<>();
        File[] tabFiles = dir.listFiles((dirTemp, nameTemp) -> nameTemp.toLowerCase().endsWith(".tab"));
        if(tabFiles!=null) {
            for (File tabFile : tabFiles) {
                try {
                    DBTable table = new DBTable();
                    table.load(tabFile);
                    tables.put(table.getTableName(),table);
                }
                catch (DBException dbe) {
                    loadError(dbe.getMessage());
                }
            }
        }
    }

    private void loadError(String str) throws DBException {
        throw new DBException("failed to load database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void drop() throws DBException {
        if(!databaseDir.exists()) dropError("path does not exist");
        if(!databaseDir.canWrite()) dropError("doesn't have write permission");
        if(!databaseDir.delete()) dropError("failed to delete path");
        databaseDir = null;
        databaseName = null;
    }

    private void dropError(String str) throws DBException {
        throw new DBException("failed to drop database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void dropTable(DBTable table) throws DBException {
        if(table == null) dropTableError("null input");
        if(!tables.contains(table)) dropTableError("table does not exist in database");
        try {
            table.drop();
            tables.remove(table);
        }
        catch(DBException dbe) {
            dropTableError(dbe.getMessage());
        }
    }

    private void dropTableError(String str) throws DBException {
        throw new DBException("failed to drop table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void updateFiles() throws DBException {
        for(DBTable table : tables) {
            try {
                table.updateFile();
            }
            catch(DBException dbe) {
                updateFilesError(dbe.getMessage());
            }
        }
    }

    private void updateFilesError(String str) throws DBException {
        throw new DBException("failed to drop table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }


}
