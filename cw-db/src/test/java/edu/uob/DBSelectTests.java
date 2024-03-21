package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

public class DBSelectTests {

    private DBServer server;
    private DBModel dbModel;
    private final static String TESTDATABASE = "testSelectDB".toLowerCase();

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
        // Initialize and create a test database and table
        server.handleCommand("CREATE DATABASE " + TESTDATABASE + ";");
        server.handleCommand("USE " + TESTDATABASE + ";");
        server.handleCommand("CREATE TABLE students (Name, Age, Pass);");
        // Populate the table with test data
        server.handleCommand("INSERT INTO students VALUES ('Alice', 20, TRUE);");
        server.handleCommand("INSERT INTO students VALUES ('Bob', 22, FALSE);");
        server.handleCommand("INSERT INTO students VALUES ('Charlie', 25, TRUE);");
        server.handleCommand("INSERT INTO students VALUES ('Dana', 20, FALSE);");
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (potential infinite loop)");
    }

    @Test
    public void testSelectWithSimpleCondition() {
        String response = sendCommandToServer("SELECT * FROM students WHERE age == 20;");
        assertTrue(response.contains("[OK]"), "Select query with simple condition should be successful.");
        assertTrue(response.contains("Alice"), "Alice should match the condition.");
        assertTrue(response.contains("Dana"), "Dana should match the condition.");
        assertFalse(response.contains("Bob"), "Bob should not match the condition.");
    }

    @Test
    public void testSelectWithAndCondition() {
        String response = sendCommandToServer("SELECT * FROM students WHERE age > 20 AND pass == TRUE;");
        assertTrue(response.contains("[OK]"), "Select query with AND condition should be successful.");
        assertTrue(response.contains("Charlie"), "Charlie should match the condition.");
        assertFalse(response.contains("Alice"), "Alice should not match the condition.");
    }

    @Test
    public void testSelectWithOrCondition() {
        String response = sendCommandToServer("SELECT * FROM students WHERE age == 20 OR pass == FALSE;");
        assertTrue(response.contains("[OK]"), "Select query with OR condition should be successful.");
        assertTrue(response.contains("Alice"), "Alice should match one part of the condition.");
        assertTrue(response.contains("Bob"), "Bob should match the other part of the condition.");
        assertTrue(response.contains("Dana"), "Dana should match both parts of the condition.");
        assertFalse(response.contains("Charlie"), "Charlie should not match the condition.");
    }

    @Test
    public void testSelectWithComplexCondition() {
        String response = sendCommandToServer("SELECT * FROM students WHERE (age < 25 AND pass == TRUE) OR (name == 'Dana');");
        assertTrue(response.contains("[OK]"), "Select query with complex condition should be successful.");
        assertTrue(response.contains("Alice"), "Alice should match the first part of the condition.");
        assertTrue(response.contains("Dana"), "Dana should match the second part of the condition.");
        assertFalse(response.contains("Bob"), "Bob should not match the condition.");
        assertFalse(response.contains("Charlie"), "Charlie should not match the first part of the condition.");
    }

    @Test
    public void testSelectWithNonexistentColumn() {
        String response = sendCommandToServer("SELECT * FROM students WHERE nonExistentColumn == 'test';");
        assertTrue(response.contains("[ERROR]"), "Select query with nonexistent column should result in an error.");
    }
}
