package edu.uob;

import java.util.Map;
import java.util.Set;

public class GameActionLook extends GameAction {
    public GameActionLook() {
        super();
        addTrigger("look");
    }

    @Override
    public boolean isPossible(GameEntityPlayer player, Set<String> commandEntities) {
        return commandEntities.isEmpty();
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        GameEntityLocation currentLocation = (GameEntityLocation) player.getCurrentLocation();
        StringBuilder message = new StringBuilder("Location:\n");
        message.append("\t").append(currentLocation.getDescription()).append("\n");
        Map<String, GameEntity> locationEntities = currentLocation.getEntities();
        if(!locationEntities.isEmpty()) {
            message.append("It has the following things:\n");
            for (GameEntity entity:locationEntities.values()) {
                message.append("\t").append(entity.getDescription()).append("\n");
            }
        }
        Map<String, GameEntityLocation> allowedLocations = currentLocation.getAllowedLocations();
        if(allowedLocations.isEmpty()) {
            message.append("You cannot go anywhere.");
        }
        else {
            message.append("You can goto the following locations:\n");
            for (GameEntityLocation location:allowedLocations.values()) {
                message.append("\t").append(location.getDescription()).append("\n");
            }
        }
        return message.toString();
    }
}
