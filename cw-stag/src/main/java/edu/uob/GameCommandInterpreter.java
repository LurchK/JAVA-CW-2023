package edu.uob;

import edu.uob.GameException.*;

import java.util.*;

public class GameCommandInterpreter {
    private GameModel model;
    private GameEntityPlayer player;
    private String command;
    public String interp(GameModel model, String playerName, String command) throws GameException {
        this.model = model;
        this.player = getPlayer(playerName);
        this.command = formatCommand(command);

        Set<String> matchedTriggers = matchTriggersWithCommand();
        Set<GameAction> possibleActions = checkPossibleActions(matchedTriggers);
        String message = "";
        for (GameAction action:possibleActions) {
            message = action.executeAction(model, player);
        }
        return message;
    }

    private GameEntityPlayer getPlayer(String playerName) throws GameException {
        if(model.getEntities().containsKey(playerName)) {
            if(!(model.getEntities().get(playerName) instanceof GameEntityPlayer)) {
                throw(new PlayerNameNotPlayerEntityException(playerName));
            }
            return (GameEntityPlayer) model.getEntities().get(playerName);
        }
        else {
            return createNewPlayer(playerName);
        }
    }

    private GameEntityPlayer createNewPlayer(String playerName) {
        Map<String, GameEntity> entities = model.getEntities();
        String startLocationName = model.getStartLocation();
        GameEntityLocation startLocationEntity = (GameEntityLocation) entities.get(startLocationName);
        GameEntityPlayer newPlayer = new GameEntityPlayer(playerName);
        newPlayer.setCurrentLocation(startLocationEntity);
        entities.put(playerName, newPlayer);
        startLocationEntity.addEntity(newPlayer);
        return newPlayer;
    }

    private String formatCommand(String inputCommand) {
        String formatted = inputCommand;
        formatted = formatted + " ";
        formatted = formatted.replaceAll("[^a-z0-9 \\-](?=.)", " $0 ");
        formatted = formatted.replaceAll("[^a-zA-Z0-9!#$%&()*+,\\-./:;>=<?@\\[\\\\\\]^_`{}~']+"," ");
        while (formatted.contains("  ")) formatted = formatted.replaceAll(" {2}"," ");
        return formatted;
    }

    private Set<String> matchTriggersWithCommand() throws GameException {
        Set<String> matchedTriggers = new HashSet<>();
        for (String trigger:model.getActions().keySet()) {
            if (command.contains(" " + trigger + " ")) {
                matchedTriggers.add(trigger);
            }
        }
        if(matchedTriggers.isEmpty()) throw new NoMatchedTrigger();
        return matchedTriggers;
    }

    private Set<GameAction> checkPossibleActions(Set<String> matchedTriggers) throws GameException {
        Set<GameAction> possibleActions = new HashSet<>();
        for (String trigger:matchedTriggers) {
            Set<String> commandEntities = getEntitiesInCommand(trigger);
            Set<GameAction> actions = model.getActions().get(trigger);
            for (GameAction action:actions) {
                if (action.isPossible(player, commandEntities)) possibleActions.add(action);
            }
        }
        if (possibleActions.isEmpty()) {
            throw new NoPossibleAction();
        }
        if (possibleActions.size() > 1) {
            throw new MultiplePossibleActions();
        }
        return possibleActions;
    }

    private Set<String> getEntitiesInCommand(String trigger) {
        Set<String> commandEntities = new HashSet<>();

        String commandWithoutTrigger = command.replaceFirst(" " + trigger + " ", " ");
        Map<String, GameEntity> gameEntities = model.getEntities();
        for (String entityName:gameEntities.keySet()) {
            if (commandWithoutTrigger.contains(" " + entityName + " ")) {
                commandEntities.add(entityName);
            }
        }
        return commandEntities;
    }
}
