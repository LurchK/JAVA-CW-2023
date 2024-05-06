package edu.uob;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameActionExternal extends GameAction {
    private Set<String> consumed;
    private Set<String> produced;
    private String narration;
    public GameActionExternal() {
        super();
        consumed = new HashSet<>();
        produced = new HashSet<>();
    }

    public void addConsumed(String entity) {
        consumed.add(entity);
    }

    public void addProduced(String entity) {
        produced.add(entity);
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public Set<String> getConsumed() {
        return consumed;
    }

    public Set<String> getProduced() {
        return produced;
    }

    public String getNarration() {
        return narration;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(super.toString());
        str.append("\nconsumed: ");
        for (String s:consumed) {
            str.append("'").append(s).append("' ");
        }
        str.append("\nproduced: ");
        for (String s:produced) {
            str.append("'").append(s).append("' ");
        }
        str.append("\nnarration: ").append(narration);
        return str.toString();
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        consume(model, player);
        produce(model, player);
        return narration;
    }

    private void consume(GameModel model, GameEntityPlayer player) {
        for (String entityName:consumed) {
            if (entityName.equals("health")) {
                player.setHealth(player.getHealth()-1);
                continue;
            }

            GameEntity entity = model.getEntities().get(entityName);
            GameEntityLocation playerLocation = (GameEntityLocation) player.getCurrentLocation();
            if (entity instanceof GameEntityLocation destination) {
                playerLocation.removeAllowedLocation(destination.getName());
                continue;
            }

            GameEntity entityLocation = entity.getCurrentLocation();
            GameEntityLocation storeroom = (GameEntityLocation) model.getEntities().get("storeroom");
            entityLocation.removeEntity(entity.getName());
            entity.setCurrentLocation(storeroom);
            storeroom.addEntity(entity);
        }
    }

    private void produce(GameModel model, GameEntityPlayer player) {
        for (String entityName:produced) {
            if (entityName.equals("health")) {
                player.setHealth(player.getHealth()+1);
                continue;
            }

            GameEntity entity = model.getEntities().get(entityName);
            GameEntityLocation playerLocation = (GameEntityLocation) player.getCurrentLocation();
            if (entity instanceof GameEntityLocation destination) {
                playerLocation.addAllowedLocation(destination);
                continue;
            }

            GameEntity entityLocation = entity.getCurrentLocation();
            entityLocation.removeEntity(entity.getName());
            entity.setCurrentLocation(playerLocation);
            playerLocation.addEntity(entity);
        }
    }
}
