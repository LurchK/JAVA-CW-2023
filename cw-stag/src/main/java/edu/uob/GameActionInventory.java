package edu.uob;

import java.util.Map;

public class GameActionInventory extends GameAction {
    public GameActionInventory() {
        super();
        addTrigger("inv");
        addTrigger("inventory");
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        Map<String, GameEntity> playerEntities = player.getEntities();
        StringBuilder message = new StringBuilder("You are currently carrying:");
        for (GameEntity entity:playerEntities.values()) {
            message.append(entity.getName()).append(": ").append(entity.getDescription()).append("\n");
        }
        return message.toString();
    }

}
