package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class GameEntityLocation extends GameEntity {
    private Map<String, GameEntity> entities;
    private Map<String, GameEntityLocation> allowedLocations;
    public GameEntityLocation(String name, String description) {
        super(name, description);
        entities = new HashMap<>();
        allowedLocations = new HashMap<>();
    }

    public void addEntity(GameEntity entity) {
        entities.put(entity.getName(), entity);
    }

    public void addAllowedLocation(GameEntityLocation location) {
        allowedLocations.put(location.getName(), location);
    }
}
