package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

public class DBComplexConditionTests {

    private DBServer server;
    private DBModel dbModel;
    private final static String TESTDATABASE = "complexConditionDB".toLowerCase();

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
        server.handleCommand("CREATE TABLE employees (name, age, department, salary, active);");
        // Populate the table with test data
        server.handleCommand("INSERT INTO employees VALUES ('Alice', 30, 'HR', 50000, TRUE);");
        server.handleCommand("INSERT INTO employees VALUES ('Bob', 45, 'Sales', 55000, FALSE);");
        server.handleCommand("INSERT INTO employees VALUES ('Charlie', 28, 'IT', 60000, TRUE);");
        server.handleCommand("INSERT INTO employees VALUES ('Dana', 35, 'IT', 62000, FALSE);");
        server.handleCommand("INSERT INTO employees VALUES ('Eli', 50, 'Sales', 70000, TRUE);");
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (potential infinite loop)");
    }

    @Test
    public void testComplexCondition() {
        // Test for a complex condition: Select active IT employees under 40 or Sales employees with salary over 65000
        String response = sendCommandToServer("SELECT * FROM employees WHERE " +
                "((department == 'IT' AND age < 40 AND active == TRUE) OR " +
                "(department == 'Sales' AND salary > 65000)) AND id != '1';");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "Complex condition query should be successful.");
        assertTrue(response.contains("Charlie"), "Charlie matches the IT condition.");
        assertFalse(response.contains("Dana"), "Dana does not match because she is not active.");
        assertFalse(response.contains("Bob"), "Bob does not match the Sales condition.");
        assertTrue(response.contains("Eli"), "Eli matches the Sales condition.");
        assertFalse(response.contains("Alice"), "Alice does not match the complex condition despite being active because she is in HR.");
    }

    @Test
    public void testLike() {
        String response = sendCommandToServer("SELECT * FROM employees WHERE " +
                "salary >= '50000';");

        System.out.println(response);
    }
}
