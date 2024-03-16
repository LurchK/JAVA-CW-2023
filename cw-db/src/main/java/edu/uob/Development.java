package edu.uob;

import java.io.*;
import java.util.*;

public class Development {
    public static void main(String[] args) {
        String command = "  INSERT  INTO  people   VALUES(  'Simon Lock'  ,35, 'simon@bristol.ac.uk' , 1.8  ) ;  ";
        try {
            DBTokens tokens = new DBTokens(command);
        }
        catch (Exception e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
        System.out.println("\nThis is the end of main.\n");
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
}
