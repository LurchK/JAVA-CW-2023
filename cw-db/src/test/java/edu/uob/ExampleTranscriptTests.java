package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ExampleTranscriptTests {

    private DBServer server;
    private DBModel dbModel;
    private final static String TESTDATABASE = "exampleTranscriptTests".toLowerCase();

    @BeforeEach
    public void setup() {
        server = new DBServer();
        dbModel = server.getDBModel();
        if (dbModel.getDatabases().containsKey(TESTDATABASE)) {
            try {
                dbModel.dropDatabase(TESTDATABASE);
            } catch (DBException dbe) {
                System.out.println(dbe.getMessage());
            }
        }
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (potential infinite loop)");
    }

    @Test
    public void test() {
        String response = sendCommandToServer("CREATE DATABASE " + TESTDATABASE + ";");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Create database query should be successful.");
        response = sendCommandToServer("USE " + TESTDATABASE + ";");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "USE query should be successful.");
        response = sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Create table query should be successful.");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks WHERE name != 'Sion';");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks WHERE pass == TRUE;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        System.out.println();

        sendCommandToServer("CREATE TABLE coursework (task, submission);");
        sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 2);");
        sendCommandToServer("INSERT INTO coursework VALUES ('DB', 0);");
        sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 3);");
        sendCommandToServer("INSERT INTO coursework VALUES ('STAG', 1);");
        response = sendCommandToServer("SELECT * FROM coursework;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        System.out.println();

        response = sendCommandToServer("JOIN coursework AND marks ON submission AND id;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Join query should be successful.");
        assertTrue(response.contains("coursework.task"), "headings should show table name.");
        System.out.println();

        response = sendCommandToServer("UPDATE marks SET mark = 38 WHERE name == 'Chris';");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Update query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks WHERE name == 'Chris';");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        assertTrue(response.contains("38"), "Update should be effective.");
        System.out.println();

        response = sendCommandToServer("DELETE FROM marks WHERE name == 'Sion';");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Delete query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        assertFalse(response.contains("Sion"), "Sion should be deleted.");
        System.out.println();

        response = sendCommandToServer("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks WHERE name LIKE 'i';");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        response = sendCommandToServer("SELECT id FROM marks WHERE pass == FALSE;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        response = sendCommandToServer("SELECT name FROM marks WHERE mark>60;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        System.out.println();

        response = sendCommandToServer("DELETE FROM marks WHERE mark<40;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Delete query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        System.out.println();

        response = sendCommandToServer("ALTER TABLE marks ADD age;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Alter query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        System.out.println();

        response = sendCommandToServer("UPDATE marks SET age = 35 WHERE name == 'Simon';");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Update query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        System.out.println();

        response = sendCommandToServer("ALTER TABLE marks DROP pass;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Alter query should be successful.");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Select query should be successful.");
        System.out.println();

        response = sendCommandToServer("DROP TABLE marks;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Drop query should be successful.");
        response = sendCommandToServer("DROP DATABASE " + TESTDATABASE + ";");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Drop query should be successful.");
        System.out.println();
    }
}