package edu.uob;

import java.util.Map;
import java.util.Set;

public class GameActionInventory extends GameAction {
    public GameActionInventory() {
        super();
        addTrigger("inv");
        addTrigger("inventory");
    }

    @Override
    public boolean isPossible(GameEntityPlayer player, Set<String> commandEntities) {
        return commandEntities.isEmpty();
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        Map<String, GameEntity> playerEntities = player.getEntities();
        if (playerEntities.isEmpty()) return "You are currently carrying nothing.\n";
        StringBuilder message = new StringBuilder("You are currently carrying:\n");
        for (GameEntity entity:playerEntities.values()) {
            message.append("\t").append(entity.getName()).append(": ").append(entity.getDescription()).append("\n");
        }
        return message.toString();
    }
}
