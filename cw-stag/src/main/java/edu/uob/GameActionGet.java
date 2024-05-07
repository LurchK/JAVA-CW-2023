package edu.uob;

import java.util.Set;

public class GameActionGet extends GameAction {
    private String actionEntityName;
    public GameActionGet() {
        super();
        addTrigger("get");
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
        if (!(model.getEntities().get(actionEntityName) instanceof GameEntityArtefact)) {
            return "It can't be picked up.";
        }

        GameEntityLocation playerLocation = (GameEntityLocation) player.getCurrentLocation();
        GameEntity entity = playerLocation.getEntities().get(actionEntityName);
        if (entity == null) {
            return "The '" + actionEntityName + "' is not here.";
        }

        playerLocation.removeEntity(actionEntityName);
        player.addEntity(entity);
        entity.setCurrentLocation(player);
        return "You put the '" + actionEntityName + "' into your inventory.";
    }

    public void setActionEntityName(String actionEntityName) {
        this.actionEntityName = actionEntityName;
    }
}
