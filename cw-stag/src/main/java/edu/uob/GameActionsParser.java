package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameActionsParser {
    private Map<String, Set<GameAction>> gameActions;
    private GameActionExternal gameActionExternal;
    private GameModel model;
    private File actionsFile;
    public GameActionsParser() {
        gameActions = new HashMap<>();
    }

    public void parse(GameModel model, File actionsFile) {
        this.model = model;
        this.actionsFile = actionsFile;
        addBuiltInActions();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actions = root.getElementsByTagName("action");
            int actionsLength = actions.getLength();
            for(int i=0; i<actionsLength; i++) {
                addAction((Element) actions.item(i));
            }
        } catch (FileNotFoundException e) {
            System.err.println("File for entities not found!");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        model.setActions(gameActions);
    }

    private void addBuiltInActions() {
        Set<GameAction> invActionSet = new HashSet<>();
        invActionSet.add(new GameActionInventory());
        gameActions.put("inv", invActionSet);
        Set<GameAction> inventoryActionSet = new HashSet<>();
        inventoryActionSet.add(new GameActionInventory());
        gameActions.put("inventory", inventoryActionSet);

        Set<GameAction> getActionSet = new HashSet<>();
        getActionSet.add(new GameActionGet());
        gameActions.put("get", getActionSet);

        Set<GameAction> dropActionSet = new HashSet<>();
        dropActionSet.add(new GameActionDrop());
        gameActions.put("drop", dropActionSet);

        Set<GameAction> gotoActionSet = new HashSet<>();
        gotoActionSet.add(new GameActionGoto());
        gameActions.put("goto", gotoActionSet);

        Set<GameAction> lookActionSet = new HashSet<>();
        lookActionSet.add(new GameActionLook());
        gameActions.put("look", lookActionSet);
    }

    private void checkNameIfReserved(String name) {
        if(model.getReservedWords().contains(name)) {
            System.err.println("Action used name '" + name + "' is a reserved key word!");
            System.exit(1);
        }
    }

    private void addAction(Element actionElement) {
        gameActionExternal = new GameActionExternal();
        NodeList triggerElements = actionElement.getElementsByTagName("triggers");
        if(triggerElements.getLength() == 0) {
            System.err.println("No trigger in an action!");
            System.exit(1);
        }
        addTriggers((Element) triggerElements.item(0));

        NodeList subjectElements = actionElement.getElementsByTagName("subjects");
        if(subjectElements.getLength() == 0) {
            System.err.println("No subject in an action!");
            System.exit(1);
        }
        addSubjects((Element) subjectElements.item(0));

        NodeList consumedElements = actionElement.getElementsByTagName("consumed");
        if(consumedElements.getLength() != 0) {
            addConsumed((Element) consumedElements.item(0));
        }

        NodeList producedElements = actionElement.getElementsByTagName("produced");
        if(producedElements.getLength() != 0) {
            addProduced((Element) producedElements.item(0));
        }

        NodeList narrationElements = actionElement.getElementsByTagName("narration");
        if(narrationElements.getLength() == 0) {
            System.err.println("No narration in an action!");
            System.exit(1);
        }

        setNarration((Element) narrationElements.item(0));

        for (String trigger: gameActionExternal.getTriggers()) {
            if(gameActions.containsKey(trigger)) {
                gameActions.get(trigger).add(gameActionExternal);
            }
            else {
                Set<GameAction> newActionSet = new HashSet<>();
                newActionSet.add(gameActionExternal);
                gameActions.put(trigger, newActionSet);
            }
        }
    }

    private void addTriggers(Element triggers) {
        NodeList keyphrases = triggers.getElementsByTagName("keyphrase");
        int triggersLength = keyphrases.getLength();
        for(int i=0; i<triggersLength; i++) {
            String keyphrase = keyphrases.item(i).getTextContent().toLowerCase();
            checkNameIfReserved(keyphrase);
            if(keyphrase.isBlank()) {
                System.err.println("Blank key phrase for a trigger!");
                System.exit(1);
            }
            gameActionExternal.addTrigger(keyphrase);
        }
    }

    private void addSubjects(Element subjects) {
        Map<String, GameEntity> gameEntities = model.getEntities();
        NodeList subjectEntities = subjects.getElementsByTagName("entity");
        int subjectsLength = subjectEntities.getLength();
        for(int i=0; i<subjectsLength; i++) {
            String entityName = subjectEntities.item(i).getTextContent().toLowerCase();
            checkNameIfReserved(entityName);
            if(entityName.isBlank()) {
                System.err.println("Blank name for a subject!");
                System.exit(1);
            }
            GameEntity entity = gameEntities.get(entityName);
            if(entity == null) {
                System.err.println("Subject with name '" + entityName + "' doesn't exist!");
                System.exit(1);
            }
            gameActionExternal.addSubject(entityName);
        }
    }

    private void addConsumed(Element consumed) {
        Map<String, GameEntity> gameEntities = model.getEntities();
        NodeList consumedEntities = consumed.getElementsByTagName("entity");
        int consumedLength = consumedEntities.getLength();
        for(int i=0; i<consumedLength; i++) {
            String entityName = consumedEntities.item(i).getTextContent().toLowerCase();
            checkNameIfReserved(entityName);
            if(entityName.isBlank()) {
                System.err.println("Blank name for a consumed entity!");
                System.exit(1);
            }
            GameEntity entity = gameEntities.get(entityName);
            if(!entityName.equalsIgnoreCase("health") && entity == null) {
                System.err.println("Consumed entity with name '" + entityName + "' doesn't exist!");
                System.exit(1);
            }
            gameActionExternal.addConsumed(entityName);
        }
    }

    private void addProduced(Element produced) {
        Map<String, GameEntity> gameEntities = model.getEntities();
        NodeList producedEntities = produced.getElementsByTagName("entity");
        int producedLength = producedEntities.getLength();
        for(int i=0; i<producedLength; i++) {
            String entityName = producedEntities.item(i).getTextContent().toLowerCase();
            checkNameIfReserved(entityName);
            if(entityName.isBlank()) {
                System.err.println("Blank name for a produced entity!");
                System.exit(1);
            }
            GameEntity entity = gameEntities.get(entityName);
            if(!entityName.equalsIgnoreCase("health") && entity == null) {
                System.err.println("Produced entity with name '" + entityName + "' doesn't exist!");
                System.exit(1);
            }
            gameActionExternal.addProduced(entityName);
        }
    }

    private void setNarration(Element narration) {
        gameActionExternal.setNarration(narration.getTextContent());
    }
}
