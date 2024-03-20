package edu.uob;

import java.io.*;
import java.util.*;

public class Development {
    public static void main(String[] args) {

        System.out.println("\nThis is the end of main.\n");
    }
    public static void mainTableReadWrite() {
        String fileName = "databases" + File.separator + "testDB" + File.separator + "people.tab";
        File file = new File(fileName);
        DBTable table = new DBTable();
        try {
            table.load(file);
            List<List<String>> selectOutput = table.select(List.of("*"));
            for(List<String> rowData : selectOutput) {
                System.out.println(rowData);
            }
            List<List<String>> data = selectOutput.subList(1,selectOutput.size());
            int name = Integer.parseInt(data.get(2).get(1));
            System.out.println(name);
            name++;
            table.update(List.of(2), List.of(List.of("name", String.valueOf(name))));
            printTable(table);
            //table.updateFile();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void mainGetTabs() {
        String dirName = "databases" + File.separator + "testDB";
        File dir = new File(dirName);
        File[] tabFiles = dir.listFiles((dirTemp, nameTemp) -> nameTemp.toLowerCase().endsWith(".tab"));
        if(tabFiles!=null) {
            for (File file : tabFiles) {
                try (FileReader reader = new FileReader(file);
                     BufferedReader buffReader = new BufferedReader(reader);) {
                    for(String line=buffReader.readLine(); line!=null; line=buffReader.readLine()) {
                        System.out.println(line);
                    }
                }
                catch(IOException e) {
                    System.err.println("Exception happens.");
                }
            }
        }
    }

    public static void mainTestString() {
        String str = "test";
        String str2 = testString(str);
        System.out.println(str + "\n" + str2);
    }

    public static String testString(String str) {
        str = str.concat("_modified");
        return str;
    }

    public static void printTable(DBTable table) {
        List<List<String>> selectOutput = table.select(List.of("*"));
        for(List<String> rowData : selectOutput) {
            System.out.println(rowData);
        }
    }
}
