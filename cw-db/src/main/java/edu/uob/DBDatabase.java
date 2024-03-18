package edu.uob;

import java.io.*;
import java.util.*;

public class DBDatabase {
    private File databaseDir;
    private String databaseName;
    private List<DBTable> tables;

    public void create(File dir) throws DBException {
        databaseDir = dir;
        databaseName = dir.getName();
        if(dir.exists()) createError("path already exists");
        if(!dir.mkdir()) createError("cannot create folder");

        tables = new ArrayList<>();
    }

    private void createError(String str) throws DBException {
        throw new DBException("Failed to create database " + databaseName +
                ", " + str + ":\n\t" + databaseDir);
    }

    public void load(File dir) throws DBException {
        databaseDir = dir;
        databaseName = dir.getName();
        if(!dir.exists()) createError("path does not exist");
        if(!dir.isDirectory()) loadError("path is not a directory");
        if(!dir.canRead()) loadError("cannot read path");

        tables = new ArrayList<>();
        File[] tabFiles = dir.listFiles((dirTemp, nameTemp) -> nameTemp.toLowerCase().endsWith(".tab"));
        if(tabFiles!=null) {
            for (File tabFile : tabFiles) {
                DBTable table = new DBTable();
                table.load(tabFile);
                tables.add(table);
            }
        }
    }

    private void loadError(String str) throws DBException {
        throw new DBException("Failed to load database " + databaseName +
                ", " + str + ":\n\t" + databaseDir);
    }

    public void drop() throws DBException {

    }

    private void dropError(String str) throws DBException {

    }
}
