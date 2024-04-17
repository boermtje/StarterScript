package net.botwithus.Skills;

import net.botwithus.SkeletonScript;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.events.impl.SkillUpdateEvent;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.util.RandomGenerator;

import java.util.*;
import java.util.stream.Collectors;

import static net.botwithus.rs3.script.ScriptConsole.println;

public class RuneCrafting {
    private static Random random = new Random();
    private static HashMap<String, Integer> priorityObjects;
    private HashMap<String, Integer> priorityNPCs;
    private static HashMap<String, Area> islands;
    private static HashMap<String, Integer> levelRequirements;

    public RuneCrafting() {
        initializeMaps(); // Call to initialize maps
    }

    private void initializeMaps() {
        // Initialization logic for priorityObjects, islands, levelRequirements
        // HashMap for priority objects with their level requirements
        priorityObjects = new HashMap<>();
        priorityObjects.put("Undead Soul", 95);
        priorityObjects.put("Living soul", 90);
        priorityObjects.put("Bloody skulls", 83);
        priorityObjects.put("Blood pool", 77);
        priorityObjects.put("Skulls", 65);
        priorityObjects.put("Jumper", 54);
        priorityObjects.put("Shifter", 44);
        priorityObjects.put("Nebula", 40);
        priorityObjects.put("Chaotic cloud", 35);
        priorityObjects.put("Fire storm", 27);
        priorityObjects.put("Fleshy growth", 20);
        priorityObjects.put("Vine", 17);
        priorityObjects.put("Fireball", 14);
        priorityObjects.put("Rock fragment", 9);
        priorityObjects.put("Water pool", 5);
        priorityObjects.put("Mind storm", 1);
        priorityObjects.put("Cyclone", 1);

        priorityNPCs = new HashMap<>();
        priorityNPCs.put("Earth essling", 9);
        priorityNPCs.put("Fire essling", 14);
        priorityNPCs.put("Body essling", 20);
        priorityNPCs.put("Cosmic esshound", 27);
        priorityNPCs.put("Chaos esshound", 35);
        priorityNPCs.put("Astral esshound", 40);
        priorityNPCs.put("Nature esshound", 44);
        priorityNPCs.put("Law esshound", 54);
        priorityNPCs.put("Death esswraith", 65);
        priorityNPCs.put("Blood esshound", 77);
        priorityNPCs.put("Soul esshound", 90);

        islands = new HashMap<>();
        Area.Rectangular Island_1 = new Area.Rectangular(new Coordinate(3989, 6095, 1), new Coordinate(4007, 6119, 1));
        islands.put("Island_Low_1", Island_1);
        Area.Rectangular Island_16 = new Area.Rectangular(new Coordinate(3990, 6067, 1), new Coordinate(4014, 6041, 1));
        islands.put("Island_Low_16", Island_16);
        Area.Rectangular Island_5 = new Area.Rectangular(new Coordinate(4125, 6093, 1), new Coordinate(4146, 6068, 1));
        islands.put("Island_Mid__5", Island_5);
        Area.Rectangular Island_23 = new Area.Rectangular(new Coordinate(4191, 6108, 1), new Coordinate(4204, 6085, 1));
        islands.put("Island_Mid_23", Island_23);
        Area.Rectangular Island_13 = new Area.Rectangular(new Coordinate(4325, 6055, 1), new Coordinate(4365, 6037, 1));
        islands.put("Island_High_13", Island_13);
        Area.Polygonal Island_20 = new Area.Polygonal(
                new Coordinate(4314, 6096, 1),
                new Coordinate(4315, 6110, 1),
                new Coordinate(4335, 6121, 1),
                new Coordinate(4346, 6127, 1),
                new Coordinate(4352, 6116, 1),
                new Coordinate(4353, 6104, 1),
                new Coordinate(4334, 6099, 1)
        );
        islands.put("Island_High_20", Island_20);
        Area.Rectangular Island_29 = new Area.Rectangular(new Coordinate(4371, 6086, 1), new Coordinate(4385, 6070, 1));
        islands.put("Island_High_29", Island_29);

        levelRequirements = new HashMap<>();
        levelRequirements.put("Island_Low_1", 1);
        levelRequirements.put("Island_Low_16", 9);
        levelRequirements.put("Island_Mid__5", 33);
        levelRequirements.put("Island_Mid_23", 50);
        levelRequirements.put("Island_High_13", 66);
        levelRequirements.put("Island_High_20", 77);
        levelRequirements.put("Island_High_29", 90);
    }

    private static void moveToIsland(Area island) {
        println("Traversing to island: " + island);
        Movement.traverse(NavPath.resolve(island.getCentroid())); // Ensure this method matches how you handle movement
        Execution.delay(3000); // Delay for traversal
    }

    private static boolean hasRune_Essence() {
        ResultSet<Item> runeScan = InventoryItemQuery.newQuery(93).ids(24227).results();
        Item rune = runeScan.first();
        if (rune != null) {
            return true;
        }
        return false;
    }

    public static void tryInteractWithNearestObject(Area currentIsland, List<String> eligibleObjects, LocalPlayer player) {
        println("Attempting to interact with objects in " + currentIsland);

        if (!hasRune_Essence()) {
            println("No Rune Essence, going to collect");
            Npc Floating_Essence = NpcQuery.newQuery().name("Floating essence").results().nearest();
            if (Floating_Essence != null) {
                println("found");
                Floating_Essence.interact("Collect");
                println("Collecting Essence");
                Execution.delay(RandomGenerator.nextInt(1000, 2000));
                if (!hasRune_Essence()) {
                    return;
                }
            }
        }

        // First, check if the player is already interacting (animation ID 16596)
        if (player.getAnimationId() == 16596) {
            println("Player is already interacting with an object.");
            return;
        }

        // Iterate through eligible objects in decreasing order of priority
        for (String objectName : eligibleObjects) {
            println("Looking for: " + objectName);
            try {
                EntityResultSet<SceneObject> priorityObjectResultSet = SceneObjectQuery.newQuery()
                        .name(objectName)
                        .inside(currentIsland)
                        .results();

                SceneObject nearestObject = priorityObjectResultSet.nearestTo(player.getCoordinate());
                if (nearestObject != null) {
                    println("Found and interacting with: " + nearestObject.getName());
                    if (nearestObject.interact("Siphon")) {
                        Execution.delay(RandomGenerator.nextInt(5000, 10000)); // Wait for the interaction to complete
                        return; // Exit the method after successful interaction
                    }
                } else {
                    println(objectName + " not found in the current island.");
                }
            } catch (Exception e) {
                println("An error occurred while interacting with " + objectName + ": " + e.getMessage());
            }
        }

        println("No eligible objects found for interaction in the current island.");
    }

    public static long interactWithPriorityObjects(LocalPlayer player) {
        println("We are within the interactWithPriorityObjects method");
        Area bestIsland = getBestAvailableIsland();
        println(bestIsland);
        if (bestIsland != null && !bestIsland.contains(player.getCoordinate())) {
            println("Moving to the best available island: " + bestIsland);
            moveToIsland(bestIsland);
        }
        println("Starting interaction with priority objects");
        Area currentIsland = determineCurrentIsland(player);
        println("Determined current island: " + (currentIsland != null ? currentIsland.getArea() : "None"));
        if (bestIsland != null) {
            List<String> eligibleObjects = getEligibleObjects(player);
            println("Eligible objects determined: " + eligibleObjects);
            Execution.delay(RandomGenerator.nextInt(3000, 5000));
            tryInteractWithNearestObject(currentIsland, eligibleObjects, player);
        } else {
            println("Player is not on any known island.");
        }
        return random.nextLong(3000, 7000);
    }

    private static Area determineCurrentIsland(LocalPlayer player) {
        for (Map.Entry<String, Area> entry : islands.entrySet()) {
            println("Checking island: " + entry.getKey());
            if (entry.getValue().contains(player.getCoordinate())) {
                println("Player is on island: " + entry.getKey());
                return entry.getValue();
            }
        }
        println("Player is not on any known island");
        return null;
    }

    private static List<String> getEligibleObjects(LocalPlayer player) {
        int playerLevel = Skills.RUNECRAFTING.getLevel();
        List<String> eligibleObjects = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : priorityObjects.entrySet()) {
            println("Checking if player level " + playerLevel + " is >= " + entry.getValue() + " for " + entry.getKey());
            if (playerLevel >= entry.getValue()) {
                eligibleObjects.add(entry.getKey());
            }
        }

        return priorityObjects.entrySet().stream()
                // Filter to include only those objects for which player level is sufficient
                .filter(entry -> playerLevel >= entry.getValue())
                // Sort in reverse order by level requirement (highest first)
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                // Map each entry to its key (object name)
                .map(Map.Entry::getKey)
                // Collect the results into a list
                .collect(Collectors.toList());
    }

    private static Area getBestAvailableIsland() {
        println("Searching for best island");
        int playerLevel = Skills.RUNECRAFTING.getLevel();
        Area bestIsland = null;
        int highestAccessibleLevel = 0;
        println("We are past this point");

        for (Map.Entry<String, Integer> entry : levelRequirements.entrySet()) {
            if (playerLevel >= entry.getValue() && entry.getValue() > highestAccessibleLevel) {
                bestIsland = islands.get(entry.getKey());
                highestAccessibleLevel = entry.getValue();
            }
        }
        return bestIsland;
    }
}