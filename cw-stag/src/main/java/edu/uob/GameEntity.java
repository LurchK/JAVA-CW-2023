package edu.uob;

import java.util.HashMap;
import java.util.Map;

abstract public class GameEntity
{
    private String name;
    private String description;
    private GameEntity currentLocation;
    private Map<String, GameEntity> entities;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
        currentLocation = null;
        entities = new HashMap<>();
    }

    public String getName()
    {
        return name;
    }

    public void setCurrentLocation(GameEntity currentLocation) {
        this.currentLocation = currentLocation;
    }

    public GameEntity getCurrentLocation() {
        return currentLocation;
    }

    public String getDescription()
    {
        return description;
    }

    public void addEntity(GameEntity entity) {
        entities.put(entity.getName(), entity);
    }

    public void removeEntity(String entityName) { entities.remove(entityName); }

    public Map<String, GameEntity> getEntities() {
        return entities;
    }

}
