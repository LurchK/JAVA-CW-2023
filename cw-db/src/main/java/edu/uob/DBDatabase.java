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
        if(!databaseDir.delete()) dropError("failed to delete path");
        databaseDir = null;
        databaseName = null;
    }

    private void dropError(String str) throws DBException {
        throw new DBException("failed to drop database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void updateFiles() throws DBException {
        for(DBTable table : tables.values()) {
            try {
                table.updateFile();
            }
            catch(DBException dbe) {
                updateFilesError(dbe.getMessage());
            }
        }
    }

    private void updateFilesError(String str) throws DBException {
        throw new DBException("failed to update table files in database " + databaseName + ":\n\t" +
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

    public void alterAdd(String tableName, String attribute) throws DBException {
        if(tableName == null || attribute == null) alterError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) alterError("table does not exist");
        DBTable table = tables.get(tableName);

        try {
            table.alterAdd(attribute);
        }
        catch(DBException dbe) {
            alterError(dbe.getMessage());
        }
    }

    public void alterDrop(String tableName, String attribute) throws DBException {
        if(tableName == null || attribute == null) alterError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) alterError("table does not exist");
        DBTable table = tables.get(tableName);

        try {
            table.alterDrop(attribute);
        }
        catch(DBException dbe) {
            alterError(dbe.getMessage());
        }
    }

    private void alterError(String str) throws DBException {
        throw new DBException("failed to alter table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void insert(String tableName, List<String> valueList) throws DBException {
        if(tableName == null || valueList == null) insertError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) insertError("table does not exist");
        DBTable table = tables.get(tableName);

        try {
            table.insert(valueList);
        }
        catch(DBException dbe) {
            insertError(dbe.getMessage());
        }
    }

    private void insertError(String str) throws DBException {
        throw new DBException("failed to insert values into table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public List<List<String>> select(String tableName, List<String> attributeList) throws DBException {
        if(tableName == null || attributeList == null) selectError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) selectError("table does not exist");
        DBTable table = tables.get(tableName);

        return table.select(attributeList);
    }

    private void selectError(String str) throws DBException {
        throw new DBException("failed to select values from table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void update(String tableName, List<Integer> rows, List<List<String>> nameValueList) throws DBException {
        if(tableName == null || rows == null || nameValueList == null) updateError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) updateError("table does not exist");
        DBTable table = tables.get(tableName);

        for (List<String> nameValuePair : nameValueList) {
            try {
                table.update(rows, nameValueList);
            }
            catch(DBException dbe) {
                updateError(dbe.getMessage());
            }
        }
    }

    private void updateError(String str) throws DBException {
        throw new DBException("failed to update values in table in database " + databaseName + ":\n\t" +
                databaseDir + ",\n\t" +
                str);
    }

    public void delete(String tableName, List<Integer> rows) throws DBException {
        if(tableName == null || rows == null) deleteError("null input");
        tableName = tableName.toLowerCase();
        if(!tables.containsKey(tableName)) deleteError("table does not exist");
        DBTable table = tables.get(tableName);

        try {
            table.delete(rows);
        }
        catch(DBException dbe) {
            deleteError(dbe.getMessage());
        }
    }

    private void deleteError(String str) throws DBException {
        throw new DBException("failed to delete rows in table in database " + databaseName + ":\n\t" +
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
