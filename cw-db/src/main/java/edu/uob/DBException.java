package edu.uob;

import java.io.Serial;

public class DBException extends Exception {
    @Serial
    private static final long serialVersionUID = 1;
    String message;
    public DBException(String message) {
        super(message);
    }
}
