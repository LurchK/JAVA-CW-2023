package edu.uob;

import java.util.Set;

public class GameActionHealth extends GameAction {
    public GameActionHealth() {
        super();
        addTrigger("health");
    }

    @Override
    public boolean isPossible(GameEntityPlayer player, Set<String> commandEntities) {
        return commandEntities.isEmpty();
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        return "Your health level is: " + player.getHealth();
    }
}
