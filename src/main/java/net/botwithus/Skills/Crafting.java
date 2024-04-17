package net.botwithus.Skills;

import net.botwithus.SkeletonScript;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.util.Regex;


import java.util.*;
import java.util.regex.Pattern;

public class Crafting extends SkeletonScript {
    private Random random = new Random();
    public int gemsGained = 0;
    private boolean isSkilling = false;
    private final Pattern uncutPattern = Regex.getPatternForContainingOneOf("Uncut ");

    public Crafting(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        // Subscribe to InventoryUpdateEvent
        subscribe(InventoryUpdateEvent.class, inventoryUpdateEvent -> {
            if (isSkilling) {
                Item item = inventoryUpdateEvent.getNewItem();
                // Check if the new item matches the "Uncut" pattern and is in inventory type 93
                if (uncutPattern.matcher(item.getName()).find()) {
                    gemsGained++;
                }
            }
        });
    }

    public long Traverse() {
        Area.Singular area = new Area.Singular(new Coordinate(2825, 2997, 0));
        if (Bank.isOpen()) {
            Bank.close();
        }
        if (Movement.traverse(NavPath.resolve(area)) == TraverseEvent.State.FINISHED) {
            botState = BotState.CRAFTINGSKILLING;
        } else {
            println("Failed to traverse to gems");
        }
        return random.nextLong(1500, 3000);
    }

    public boolean headbar(LocalPlayer player) {
        if (player.getHeadbars().size() != 5 && player.getHeadbars().get(5).getWidth() < 180) {
            return true;
        } else {
            return false;
        }
    }

    public long handleSkilling(LocalPlayer player) {
        SceneObject gemRock = SceneObjectQuery.newQuery().name("Precious gem rock").option("Mine").results().nearest();
        Area.Rectangular area = new Area.Rectangular(new Coordinate(2827, 2296, 0), new Coordinate(2822, 3002, 0));
        //if our inventory is full, lets bank.
        if (Backpack.isFull()) {
            println("Inventory is full.");
            isSkilling = false;  // Stop skilling
            botState = BotState.CRAFTINGBANKING;
            return random.nextLong(250, 1500);
        }
        if (Bank.isOpen()) {
            Bank.close();
        }
        if (area.contains(player) == false) {
            botState = BotState.CRAFTINGTRAVERSE;
        } else if (gemRock != null && headbar(player) == true) {
            isSkilling = true;  // Start skilling
            print(isSkilling);
            println("In area, mining");
            Execution.delay(random.nextLong(500, 2000));
            println("Interacted rock: " + gemRock.interact("Mine"));
        }
        return random.nextLong(1000, 2000);
    }

    public long Banking() {
        //go to area
        Area.Rectangular areas = new Area.Rectangular(new Coordinate(2848, 2958, 0), new Coordinate(2855, 2953, 0));
        if (Movement.traverse(NavPath.resolve(areas)) == TraverseEvent.State.FINISHED) {
            isSkilling = false;
            if (Bank.isOpen()) {
                println("Bank is open");
                Execution.delay(random.nextLong(500, 2000));
                Bank.depositAllExcept(54004);
                Bank.close();
                botState = BotState.CRAFTINGSKILLING;
            } else {
                ResultSet<Npc> banks = NpcQuery.newQuery().name("Banker").inside(areas).results();
                if (banks.isEmpty()) {
                    println("Bank query was empty.");
                } else {
                    Npc bank = banks.random();
                    if (bank != null) {
                        println("Yay, we found our bank.");
                        println("Interacted bank: " + bank.interact("Bank"));
                        Execution.delay(random.nextLong(500, 2000));
                        Bank.depositAllExcept(54004);
                        Bank.close();
                        botState = BotState.CRAFTINGSKILLING;
                    }
                }
            }
        }
        return random.nextLong(1500, 3000);
    }

    private long startTime;
    public int gemsPerHour;

    public boolean initialize() {
        startTime = System.currentTimeMillis();
        return super.initialize();
    }

    public int gemsPerHour() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > startTime) {
            gemsPerHour = (int) (gemsGained * 3600000.0 / (currentTime - startTime));
        }
        return gemsPerHour;
    }
}