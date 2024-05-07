package edu.uob;

import java.util.Set;

public class GameActionDrop extends GameAction {
    private String actionEntityName;
    public GameActionDrop() {
        super();
        addTrigger("drop");
        actionEntityName = null;
    }

    @Override
    public boolean isPossible(GameEntityPlayer player, Set<String> commandEntities) {
        if (commandEntities.size() != 1) return false;
        for (String entityName:commandEntities) {
            setActionEntityName(entityName);
        }
        return true;
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        GameEntity entity = player.getEntities().get(actionEntityName);
        if (entity == null) {
            return "You don't have the '" + actionEntityName + "' in your inventory.";
        }

        player.removeEntity(actionEntityName);
        GameEntityLocation playerLocation = (GameEntityLocation) player.getCurrentLocation();
        playerLocation.addEntity(entity);
        entity.setCurrentLocation(playerLocation);
        return "You dropped the " + actionEntityName + ".";
    }

    public void setActionEntityName(String actionEntityName) {
        this.actionEntityName = actionEntityName;
    }
}
