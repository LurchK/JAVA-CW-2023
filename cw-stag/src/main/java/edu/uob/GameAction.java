package edu.uob;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract public class GameAction {
    private Set<String> triggers;
    private Set<String> subjects;
    public GameAction() {
        triggers = new HashSet<>();
        subjects = new HashSet<>();
    }

    public void addTrigger(String trigger) {
        triggers.add(trigger);
    }

    public void addSubject(String subject) {
        subjects.add(subject);
    }

    public Set<String> getTriggers() {
        return triggers;
    }

    public Set<String> getSubjects() {
        return subjects;
    }

    public boolean isPossible(GameEntityPlayer player, Set<String> commandEntities) {
        for (String commandEntity:commandEntities) {
            if (subjects.contains(commandEntity)) continue;
            return false;
        }

        GameEntityLocation currentLocation = (GameEntityLocation) player.getCurrentLocation();
        Map<String, GameEntityLocation> locationPaths = currentLocation.getAllowedLocations();
        for (String subject:subjects) {
            if (subject.equals("health")) continue;
            if (subject.equals(currentLocation.getName())) continue;
            if (player.getEntities().containsKey(subject)) continue;
            if (currentLocation.isEntityAvailable(subject)) continue;
            if (locationPaths.containsKey(subject)) continue;
            return false;
        }
        return true;
    }

    abstract public String executeAction(GameModel model, GameEntityPlayer player);

    public String toString() {
        StringBuilder str = new StringBuilder("triggers: ");
        for (String s:triggers) {
            str.append("'").append(s).append("' ");
        }
        str.append("\nsubjects: ");
        for (String s:subjects) {
            str.append("'").append(s).append("' ");
        }
        return str.toString();
    }
}
