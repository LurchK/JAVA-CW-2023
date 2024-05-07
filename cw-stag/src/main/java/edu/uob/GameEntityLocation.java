package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class GameEntityLocation extends GameEntity {
    private Map<String, GameEntityLocation> allowedLocations;
    public GameEntityLocation(String name, String description) {
        super(name, description);
        allowedLocations = new HashMap<>();
    }

    public void addAllowedLocation(GameEntityLocation location) {
        allowedLocations.put(location.getName(), location);
    }

    public void removeAllowedLocation(String locationName) { allowedLocations.remove(locationName); }

    public Map<String, GameEntityLocation> getAllowedLocations() {
        return allowedLocations;
    }
}
