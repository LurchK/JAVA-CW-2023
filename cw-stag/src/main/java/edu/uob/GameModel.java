package edu.uob;

import java.util.*;

public class GameModel {
    private List<String> reservedWords;
    private Map<String, GameEntity> entities;
    private String startLocation;
    private Map<String, Set<GameAction>> actions;

    public GameModel() {
        reservedWords = new LinkedList<>(Arrays.asList("inventory","inv","get","drop","goto","look"));
        entities = new HashMap<>();
        actions = new HashMap<>();
        startLocation = null;
    }

    public void setEntities(Map<String, GameEntity> entities) {
        this.entities = entities;
    }

    public void setActions(Map<String, Set<GameAction>> actions) {
        this.actions = actions;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public Map<String, GameEntity> getEntities() {
        return entities;
    }

    public Map<String, Set<GameAction>> getActions() {
        return actions;
    }

    public String getStartLocation() {
        return startLocation;
    }
}
