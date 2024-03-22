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
        sendCommandToServer("CREATE DATABASE " + TESTDATABASE + ";");
        sendCommandToServer("USE " + TESTDATABASE + ";");
        sendCommandToServer("CREATE TABLE marks (Name, Mark, Pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Chris', 20, FALSE);");
        sendCommandToServer("CREATE TABLE marks2 (Name, Mark, Pass);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Simon', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Sion', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Rob', 35, FALSE);");
        sendCommandToServer("INSERT INTO marks2 VALUES ('Chris', 20, FALSE);");
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will time out if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                    return server.handleCommand(command);
                },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testCreate() {
        String response = sendCommandToServer("CREATE TABLE new (like);");
        assertTrue(response.contains("[ERROR]"), "");
        sendCommandToServer("DROP TABLE new;");
        response = sendCommandToServer("CREATE TABLE new (ok);");
        assertTrue(response.contains("[OK]"), "");
        sendCommandToServer("DROP TABLE new;");
        response = sendCommandToServer("CREATE TABLE new (1,2,3,4,a,b,c,d,E,F,G,1a,a1,11,aa);");
        assertTrue(response.contains("[OK]"), "");
        sendCommandToServer("DROP TABLE new;");
        response = sendCommandToServer("CREATE TABLE new (ok);;");
        assertTrue(response.contains("[ERROR]"), "");
    }

    @Test
    public void testDrop() {
        String response = sendCommandToServer("DROP TABLE marks;");
        assertTrue(response.contains("[OK]"), "");
        response = sendCommandToServer("SELECT * FROM TABLE marks;");
        assertTrue(response.contains("[ERROR]"), "");
        response = sendCommandToServer("DROP TABLE marks;");
        assertTrue(response.contains("[ERROR]"), "");
        response = sendCommandToServer("DROP DATABASE test;");
        assertTrue(response.contains("[OK]"), "");
        response = sendCommandToServer("SELECT * FROM marks2;");
        assertTrue(response.contains("[ERROR]"), "");
    }

    @Test
    public void testAlter() {
        String response = sendCommandToServer("ALTER TABLE marks ADD hello;");
        assertTrue(response.contains("[OK]"), "");
        response = sendCommandToServer("UPDATE marks SET hello='hello' WHERE id==1;");
        assertTrue(response.contains("[OK]"), "");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        response = sendCommandToServer("ALTER TABLE marks ADD hello;");
        assertTrue(response.contains("[ERROR]"), "");
        response = sendCommandToServer("ALTER TABLE marks ADD ID;");
        assertTrue(response.contains("[ERROR]"), "");
        response = sendCommandToServer("ALTER TABLE marks ADD true;");
        assertTrue(response.contains("[ERROR]"), "");
    }

    @Test
    public void testInsert() {
        sendCommandToServer("INSERT INTO marks VALUES ('', '', '');");
        String response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        response = sendCommandToServer("SELECT * FROM marks WHERE name == '';");
        System.out.println(response);
        response = sendCommandToServer("SELECT * FROM marks WHERE pass == true;");
        System.out.println(response);
    }

    @Test
    public void testSelect() {
        sendCommandToServer("INSERT INTO marks VALUES ('10', 20, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE name > 0;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "");
    }

    @Test
    public void testUpdate() {
        sendCommandToServer("UPDATE marks SET mark = 0 WHERE name == 'Simon';");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "");
        System.out.println(response);
    }

    @Test
    public void testDelete() {
        String response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        sendCommandToServer("DELETE FROM marks WHERE mark<35;");
        sendCommandToServer("DELETE FROM marks WHERE id>0;");
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
    }

    @Test
    public void testJoin() {
        String response = sendCommandToServer("JOIN marks AND marks ON id AND id;");
        System.out.println(response);
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(response.contains("[ERROR]"), "A valid query was made, however an [ERROR] tag was returned");
        assertTrue(response.contains("Simon"), "An attempt was made to add Simon to the table, but they were not returned by SELECT *");
        assertTrue(response.contains("Chris"), "An attempt was made to add Chris to the table, but they were not returned by SELECT *");

        sendCommandToServer("DELETE FROM marks WHERE ID<3;");
        response = sendCommandToServer("JOIN marks AND marks2 ON id AND id;");
        System.out.println(response);
        sendCommandToServer("DELETE FROM marks WHERE ID>0;");
        response = sendCommandToServer("JOIN marks AND marks2 ON id AND id;");
        System.out.println(response);
    }
}