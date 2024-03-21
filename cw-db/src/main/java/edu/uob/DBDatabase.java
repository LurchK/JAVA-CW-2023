package edu.uob;

import java.io.*;
import java.util.*;

public class DBDatabase {
    private File databaseDir;
    private String databaseName;
    private Map<String,DBTable> tables;

    public void create(File dir) throws DBException {
        if(dir == null) createError("null input");
        databaseName = dir.getName().toLowerCase();
        databaseDir = new File(dir.getParent(),databaseName);
        if(databaseDir.exists()) createError("path already exists");
        if(!databaseDir.mkdir()) createError("cannot create folder");

        tables = new HashMap<>();
    }

    private void createError(String str) throws DBException {
        throw new DBException("failed to create database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void load(File dir) throws DBException {
        if(dir == null) loadError("null input");
        databaseDir = dir;
        databaseName = dir.getName();
        if(databaseName.matches("[A-Z]")) loadError("name contains uppercase letter");
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
                    tables.put(table.getName().toLowerCase(),table);
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

        for(DBTable table : tables.values()) {
            table.drop();
        }
        if(!databaseDir.delete()) dropError("failed to delete path");
        databaseDir = null;
        databaseName = null;
        tables = null;
    }

    private void dropError(String str) throws DBException {
        throw new DBException("failed to drop database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void createTable(String tableName) throws DBException {
        if(tableName == null) createTableError("null input");
        if(tableName.isBlank()) createTableError("blank name");
        tableName = tableName.toLowerCase();
        if(tables.containsKey(tableName)) {
            createTableError("table with name '" + tableName + "' already exists");
        }

        try {
            DBTable table = new DBTable();
            File tableFile = new File(databaseDir, tableName + DBTable.FILEEXTENSION);
            table.create(tableFile);
            tables.put(table.getName(),table);
        }
        catch (DBException dbe) {
            createTableError(dbe.getMessage());
        }
    }

    private void createTableError(String str) throws DBException {
        throw new DBException("failed to create table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void dropTable(String tableName) throws DBException {
        if(tableName == null) dropTableError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) {
            dropTableError("table with name '" + tableName + "' does not exist");
        }
        DBTable table = tables.get(tableName);

        try {
            table.drop();
            tables.remove(tableName);
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

    public File getDir() {
        return databaseDir;
    }

    public String getName() {
        return databaseName;
    }

    public Map<String, DBTable> getTables() {
        return tables;
    }

    public DBTable getTable(String tableName) throws DBException {
        if(tableName == null) getTableError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) getTableError("table does not exist");
        return tables.get(tableName);
    }

    private void getTableError(String str) throws DBException {
        throw new DBException("failed to get table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }
}
