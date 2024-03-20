package edu.uob;

import java.io.*;
import java.util.*;

public class DBModel {
    private File databasesDir;
    private Map<String, DBDatabase> databases;
    private DBDatabase currentDatabase;

    public DBModel(File dir) throws DBException {
        this.databasesDir = dir;
        databases = new HashMap<>();
        currentDatabase = null;
        loadDatabases();
    }

    private void loadDatabases() throws DBException {
        if(!databasesDir.exists()) loadDatabasesError("path does not exist");
        if(!databasesDir.isDirectory()) loadDatabasesError("path is not a directory");
        if(!databasesDir.canRead()) loadDatabasesError("cannot read path");

        File[] dbDirs = databasesDir.listFiles(File::isDirectory);
        if(dbDirs!=null) {
            for (File dbDir : dbDirs) {
                try {
                    DBDatabase db = new DBDatabase();
                    db.load(dbDir);
                    databases.put(db.getName(), db);
                }
                catch(DBException dbe) {
                    loadDatabasesError(dbe.getMessage());
                }
            }
        }

    }

    private void loadDatabasesError(String str) throws DBException {
        throw new DBException("Failed to initialize DBModel:\n\t" +
                databasesDir + ",\n\t" +
                str);
    }

    public void use(String databaseName) throws DBException {
        if(databaseName == null) useError("null input");
        databaseName = databaseName.toLowerCase();
        if(!databases.containsKey(databaseName)) useError("database does not exist");
        currentDatabase = databases.get(databaseName);
    }

    private void useError(String str) throws DBException {
        throw new DBException("Failed to use database in database model:\n\t" +
                databasesDir + ",\n\t" +
                str);
    }

    public void createDatabase(String databaseName) throws DBException {
        if(databaseName == null) createDatabaseError("null input");
        if(databaseName.isBlank()) createDatabaseError("blank name");
        databaseName = databaseName.toLowerCase();
        if(databases.containsKey(databaseName)) {
            createDatabaseError("database with name '" + databaseName + "' already exists");
        }

        try {
            DBDatabase database = new DBDatabase();
            File databaseDir = new File(databasesDir, databaseName);
            database.create(databaseDir);
            databases.put(database.getName(),database);
        }
        catch (DBException dbe) {
            createDatabaseError(dbe.getMessage());
        }
    }

    private void createDatabaseError(String str) throws DBException {
        throw new DBException("Failed to create database in database model:\n\t" +
                databasesDir + ",\n\t" +
                str);
    }

    public void dropDatabase(String databaseName) throws DBException {
        if(databaseName == null) dropDatabaseError("null input");
        databaseName = databaseName.toLowerCase();
        if(!databases.containsKey(databaseName)) {
            dropDatabaseError("database with name '" + databaseName + "' does not exist");
        }
        DBDatabase database = databases.get(databaseName);

        try {
            database.drop();
            databases.remove(databaseName);
        }
        catch(DBException dbe) {
            dropDatabaseError(dbe.getMessage());
        }
    }

    private void dropDatabaseError(String str) throws DBException {
        throw new DBException("Failed to drop database in database model:\n\t" +
                databasesDir + ",\n\t" +
                str);
    }

    public File getDatabasesDir() {
        return databasesDir;
    }

    public Map<String, DBDatabase> getDatabases() {
        return databases;
    }

    public DBDatabase getCurrentDatabase() {
        return currentDatabase;
    }
}
