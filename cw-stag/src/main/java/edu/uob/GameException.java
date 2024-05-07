package edu.uob;

import java.io.Serial;

public class GameException extends Exception{
    @Serial
    private static final long serialVersionUID = 1;

    public GameException(String message) {
        super(message);
    }

    public static class PlayerNameNotPlayerEntityException extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public PlayerNameNotPlayerEntityException(String playerName) {
            super("Player name '" + playerName + "' is already taken by an entity that is not a player." );
        }
    }
    public static class PlayerNameIsReservedKeyword extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public PlayerNameIsReservedKeyword(String playerName) {
            super("Player name '" + playerName + "' is a reserved keyword!");
        }
    }
    public static class NoMatchedTrigger extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public NoMatchedTrigger() {
            super("Nothing to do here.");
        }
    }
    public static class NoPossibleAction extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public NoPossibleAction() {
            super("No action is possible.");
        }
    }
    public static class MultiplePossibleActions extends GameException {
        @Serial private static final long serialVersionUID = 1;
        public MultiplePossibleActions() {
            super("Multiple actions possible.");
        }
    }
}
