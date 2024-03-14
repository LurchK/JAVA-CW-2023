package edu.uob;

import java.io.*;

public class Development {
    public static void main(String[] args) throws IOException {
        String fileName = "databases" + File.separator + "people.tab";
        File file = new File(fileName);
        try (FileReader reader = new FileReader(file);
             BufferedReader buffReader = new BufferedReader(reader);) {
            String line = buffReader.readLine();
            while(line != null) {
                System.out.println(line);
                line = buffReader.readLine();
            }
        }
    }
}
