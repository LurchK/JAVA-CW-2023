package edu.uob;

import java.io.*;
import java.util.*;

public class DBModel {
    private File databasesDir;
    private List<DBDatabase> databases;
    private DBDatabase currentDatabase;

    public DBModel(File dir) throws DBException {
        this.databasesDir = dir;
        databases = new ArrayList<>();
        currentDatabase = null;
        loadDatabases();
    }

    private void loadDatabases() throws DBException {
        if(!databasesDir.exists()) loadDatabasesError("path does not exist");
        if(!databasesDir.isDirectory()) loadDatabasesError("path is not a directory");
        if(!databasesDir.canRead()) loadDatabasesError("cannot read path");

        File[] dbDirs = databasesDir.listFiles((dirTemp) -> dirTemp.isDirectory());
        if(dbDirs!=null) {
            for (File dbDir : dbDirs) {
                DBDatabase db = new DBDatabase();
                try {
                    db.load(dbDir);
                }
                catch(DBException dbe) {

                }
            }
        }

    }
    private void loadDatabasesError(String str) throws DBException {
        throw new DBException("Failed to initialize DBModel, " + str + ": " + databasesDir);
    }
}
