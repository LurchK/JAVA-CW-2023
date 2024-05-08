package edu.uob;

import java.util.*;

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

    @Override
    public boolean isPossible(GameEntityPlayer player, Set<String> commandEntities) {
        if (commandEntities.isEmpty()) return false;
        return super.isPossible(player, commandEntities);
    }

    public String executeAction(GameModel model, GameEntityPlayer player) {
        consume(model, player);
        produce(model, player);
        if (player.getHealth() <= 0) return revivePlayer(model, player);
        else return narration;
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
                if (entity == playerLocation) continue;
                playerLocation.addAllowedLocation(destination);
                continue;
            }

            GameEntity entityLocation = entity.getCurrentLocation();
            entityLocation.removeEntity(entity.getName());
            entity.setCurrentLocation(playerLocation);
            playerLocation.addEntity(entity);
        }
    }

    private String revivePlayer(GameModel model, GameEntityPlayer player) {
        List<GameEntity> playerEntities = new ArrayList<>(player.getEntities().values());
        GameEntityLocation currentLocation = (GameEntityLocation) player.getCurrentLocation();
        for (GameEntity entity:playerEntities) {
            player.removeEntity(entity.getName());
            currentLocation.addEntity(entity);
            entity.setCurrentLocation(currentLocation);
        }

        currentLocation.removeEntity(player.getName());
        String startLocationName = model.getStartLocation();
        GameEntityLocation startLocationEntity = (GameEntityLocation) model.getEntities().get(startLocationName);
        player.setCurrentLocation(startLocationEntity);
        startLocationEntity.addEntity(player);
        player.setHealth(GameEntityPlayer.MAXHEALTH);
        return narration + "\nYou died and lost all of your items, you must return to the start of the game";
    }
}
