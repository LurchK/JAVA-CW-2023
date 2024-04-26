package edu.uob;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameModel {
    private Map<String, GameEntity> entities;
    private String startLocation;
    private Map<String, Set<GameAction>> actions;

    public GameModel() {
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
}
