package edu.uob;

public class GameException extends Exception{

    public GameException(String message) {
        super(message);
    }

    public static class PlayerNameNotPlayerEntityException extends GameException {
        public PlayerNameNotPlayerEntityException(String playerName) {
            super("Player name '" + playerName + "' is already taken by an entity that is not a player." );
        }
    }
    public static class PlayerNameIsReservedKeyword extends GameException {
        public PlayerNameIsReservedKeyword(String playerName) {
            super("Player name '" + playerName + "' is a reserved keyword!");
        }
    }
}
