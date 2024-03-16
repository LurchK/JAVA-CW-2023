package edu.uob;

import java.io.*;
import java.util.*;

public class DBDatabase {
    File databaseFile;
    String databaseName;
    List<DBTable> tables;

    public void createDBDatabase(File dir) throws DBException {
        databaseFile = dir;
        databaseName = dir.getName();
        if(dir.exists()) createDBDatabaseError("path already exists");
        if(!dir.mkdir())createDBDatabaseError("cannot create folder");

        tables = new ArrayList<>();
    }

    private void createDBDatabaseError(String str) throws DBException {
        throw new DBException("Failed to create database " + databaseName + ", " + str + ": " + databaseFile);
    }

    public void loadDBDatabase(File dir) throws DBException {
        databaseFile = dir;
        databaseName = dir.getName();
        if(!dir.exists()) createDBDatabaseError("path does not exist");
        if(!dir.isDirectory()) loadDBDatabaseError("path is not a directory");
        if(!dir.canRead()) loadDBDatabaseError("cannot read path");

        tables = new ArrayList<>();
        File[] tabFiles = dir.listFiles((dirTemp, nameTemp) -> nameTemp.toLowerCase().endsWith(".tab"));
        if(tabFiles!=null) {
            for (File tabFile : tabFiles) {
                DBTable table = new DBTable();
                table.loadDBTable(tabFile);
                tables.add(table);
            }
        }
    }

    private void loadDBDatabaseError(String str) throws DBException {
        throw new DBException("Failed to load database " + databaseName + ", " + str + ": " + databaseFile);
    }

    public void drop() {

    }
}
