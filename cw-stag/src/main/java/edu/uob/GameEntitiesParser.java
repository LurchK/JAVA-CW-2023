package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEntitiesParser {
    private Map<String, GameEntity> entities;
    private String startLocation;

    public GameEntitiesParser() {
        entities = new HashMap<>();
        startLocation = null;
    }
    public void parse(GameModel model, File entitiesFile) {
        Parser parser = new Parser();
        try {
            parser.parse(new FileReader(entitiesFile));
        } catch (FileNotFoundException e) {
            System.err.println("File for entities not found!");
            System.exit(1);
        } catch (ParseException e) {
            System.err.println("Failed to parse file for entities!");
            System.exit(1);
        }

        List<Graph> graphs = parser.getGraphs();
        if(graphs.isEmpty()) {
            System.err.println("No graph in file for entities!");
            System.exit(1);
        }
        List<Graph> subGraphs = graphs.get(0).getSubgraphs();
        if(subGraphs.size()<2) {
            System.err.println("There should be locations and paths subgraphs!");
            System.exit(1);
        }

        Graph locationsGraph = subGraphs.get(0);
        parseLocations(locationsGraph);
        Graph pathsGraph = subGraphs.get(1);
        parsePaths(pathsGraph);

        model.setEntities(entities);
        model.setStartLocation(startLocation);
    }

    private void parseLocations(Graph locationsGraph) {
        List<Graph> locationGraphs = locationsGraph.getSubgraphs();
        if(locationGraphs.size() < 2) {
            System.err.println("There should at least be a storeroom and a starting location!");
            System.exit(1);
        }
        for(Graph locationGraph:locationGraphs) {
            List<Node> nodes = locationGraph.getNodes(true);
            if(nodes.isEmpty()) {
                System.err.println("There should be a node which describes the location!");
                System.exit(1);
            }
            Node node = nodes.get(0);
            String name = node.getId().getId();
            if(name.isEmpty()) {
                System.err.println("Empty name for location!");
                System.exit(1);
            }
            String description = node.getAttribute("description");
            GameEntityLocation location = new GameEntityLocation(name, description);
            entities.put(name, location);
            if(startLocation==null) startLocation = name;

            parseLocationEntities(location, locationGraph);
        }

        if(!entities.containsKey("storeroom")) {
            System.err.println("There is no storeroom!");
            System.exit(1);
        }
    }

    private void parsePaths(Graph pathsGraph) {
        List<Edge> edges = pathsGraph.getEdges();
        for(Edge edge:edges) {
            String sourceName = edge.getSource().getNode().getId().getId();
            String targetName = edge.getTarget().getNode().getId().getId();
            if(entities.get(sourceName)==null || entities.get(targetName)==null) {
                System.err.println("Location not existed for a path.");
                System.exit(1);
            }
            if(!(entities.get(sourceName) instanceof GameEntityLocation) ||
                    !(entities.get(targetName) instanceof GameEntityLocation)) {
                System.err.println("Not a location in a path definition.");
                System.exit(1);
            }
            GameEntityLocation sourceLocation = (GameEntityLocation) entities.get(sourceName);
            GameEntityLocation targetLocation = (GameEntityLocation) entities.get(targetName);
            sourceLocation.addAllowedLocation(targetLocation);
        }
    }

    private void parseLocationEntities(GameEntityLocation location, Graph locationGraph) {
        List<Graph> locationEntitiesGraphs = locationGraph.getSubgraphs();
        for(Graph graph:locationEntitiesGraphs) {
            String type = graph.getId().getId();
            List<Node> nodes = graph.getNodes(true);
            for(Node node:nodes) {
                String name = node.getId().getId();
                String description = node.getAttribute("description");
                GameEntity entity = null;
                switch (type) {
                    case "artefacts": entity = new GameEntityArtefact(name, description); break;
                    case "furniture": entity = new GameEntityFurniture(name, description); break;
                    case "characters": entity = new GameEntityCharacter(name, description); break;
                    default:
                        System.err.println("Invalid entity type for location!");
                        System.exit(1);
                }
                location.addEntity(entity);
                entities.put(name, entity);
            }
        }
    }
}
