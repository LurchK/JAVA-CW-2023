package edu.uob;

import java.util.Set;

public class GameActionGoto extends GameAction {
    private String actionEntityName;

    public GameActionGoto() {
        super();
        addTrigger("goto");
        actionEntityName = null;
    }

    @Override
    public boolean isPossible(GameEntityPlayer player, Set<String> commandEntities) {
        if (commandEntities.size() != 1) return false;
        for (String entity:commandEntities) {
            setActionEntityName(entity);
        }
        return true;
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        GameEntityLocation currentLocation = (GameEntityLocation) player.getCurrentLocation();
        if (currentLocation.getAllowedLocations().containsKey(actionEntityName)) {
            GameEntityLocation destination = currentLocation.getAllowedLocations().get(actionEntityName);
            player.setCurrentLocation(destination);
            currentLocation.removeEntity(player.getName());
            destination.addEntity(player);
            return "You are now in the " + destination.getName() + ".";
        }
        else {
            return "You can't go there.";
        }
    }

    public void setActionEntityName(String actionEntityName) {
        this.actionEntityName = actionEntityName;
    }
}
