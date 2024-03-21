package edu.uob;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

public class DBInterpreterTests {

    private DBServer server;
    private DBModel dbModel;
    private final static String TESTDATABASE = "test";

    // Create a new server _before_ every @Test
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
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                    return server.handleCommand(command);
                },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // A basic test that creates a database, creates a table, inserts some test data, then queries it.
    // It then checks the response to see that a couple of the entries in the table are returned as expected
    @Test
    public void testBasicCreateAndQuery() {
        sendCommandToServer("CREATE DATABASE " + TESTDATABASE + ";");
        sendCommandToServer("USE " + TESTDATABASE + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        sendCommandToServer("CREATE TABLE marks2 (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Chris', 20, FALSE);");
        String response = sendCommandToServer("JOIN marks AND marks ON id AND id;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");
    }
}