package edu.uob;

import edu.uob.GameException.*;

import java.util.Map;

public class GameCommandInterpreter {
    private GameModel model;
    private GameEntityPlayer player;
    private String command;
    public String interp(GameModel model, String playerName, String command) throws GameException {
        this.model = model;
        this.player = getPlayer(playerName);
        this.command = command;

        return "";
    }

    private GameEntityPlayer getPlayer(String playerName) throws GameException {
        if(model.getEntities().containsKey(playerName)) {
            if(!(model.getEntities().get(playerName) instanceof GameEntityPlayer)) {
                throw(new PlayerNameNotPlayerEntityException(playerName));
            }
            return (GameEntityPlayer) model.getEntities().get(playerName);
        }
        else {
            return createNewPlayer(playerName);
        }
    }

    private GameEntityPlayer createNewPlayer(String playerName) {
        Map<String, GameEntity> entities = model.getEntities();
        String startLocationName = model.getStartLocation();
        GameEntityLocation startLocationEntity = (GameEntityLocation) entities.get(startLocationName);
        GameEntityPlayer newPlayer = new GameEntityPlayer(playerName);
        newPlayer.setCurrentLocation(startLocationEntity);
        entities.put(playerName, newPlayer);
        startLocationEntity.addEntity(newPlayer);
        return newPlayer;
    }
}
