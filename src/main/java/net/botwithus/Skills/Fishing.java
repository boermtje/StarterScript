package net.botwithus.Skills;

import net.botwithus.SkeletonScript;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.game.Coordinate;



import java.util.*;

public class Fishing extends SkeletonScript {
    public int fishGained = 0;
    private boolean isSkilling = false;
    private Random random = new Random();

    public Fishing(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        //Subscribe to InventoryUpdateEvent
        subscribe(InventoryUpdateEvent.class, inventoryUpdateEvent -> {
            if (isSkilling) {
                Item item = inventoryUpdateEvent.getNewItem();
                fishGained++;
            }
        });
    }

        public long Menaphos(LocalPlayer player){
            Npc fishing = NpcQuery.newQuery().name("Fishing spot").results().nearest();
            if (Backpack.isFull()) {
                println("Inventory is full.");
                botState = BotState.FISHINGMENABANKING;
                return random.nextLong(250, 1500);
            }
            if (fishing != null && player.getAnimationId() == -1 && !player.isMoving()) {
                Execution.delay(random.nextLong(1000, 3000));
                println("Interacted fishing spot: " + fishing.interact("Bait"));
            } else if (fishing == null) {
                println("No fishing spot found.");
            }
            return random.nextLong(500, 1000);
        }

        public long MenaBanking() {
            SceneObject Bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
            Execution.delay(random.nextLong(1000, 3000));
            Bank.interact("Load Last Preset from");
            Execution.delay(random.nextLong(1000, 3000));
            botState = BotState.FISHINGMENAPHOS;
            return random.nextLong(1500, 3000);
        }

        public long handleSkilling(LocalPlayer player){
            Area.Polygonal area = new Area.Polygonal(
                    new Coordinate(2833, 2973, 0),
                    new Coordinate(2849, 2973, 0),
                    new Coordinate(2855, 2975, 0),
                    new Coordinate(2868, 2973, 0),
                    new Coordinate(2868, 2969, 0),
                    new Coordinate(2833, 2969, 0));
            Npc fishing = NpcQuery.newQuery().name("Fishing spot").inside(area).results().nearest();
            //if our inventory is full, lets bank.
            if (Backpack.isFull()) {
                println("Inventory is full.");
                isSkilling = false;  // Stop skilling
                botState = BotState.FISHINGBANKING;
                return random.nextLong(250, 1500);
            }
            if (Bank.isOpen()) {
                Bank.close();
            } else if (fishing != null && player.getAnimationId() == -1 && !player.isMoving()) {
                isSkilling = true;  // Start skilling
                print(isSkilling);
                println("Fishing spot found. Interacting.");
                Execution.delay(random.nextLong(500, 2000));
                println("Interacted fishing spot: " + fishing.interact("Lure"));
            } else if (fishing == null) {
                println("No fishing spot found.");
                isSkilling = false;  // Stop skilling
            }
            return random.nextLong(500, 1000);
        }

        public long Banking() {
            //go to area
            isSkilling = false;
            if (Bank.isOpen()) {
                println("Bank is open");
                Execution.delay(random.nextLong(500, 2000));
                Bank.depositAllExcept(314);
                Bank.close();
                botState = BotState.FISHINGSKILLING;
            } else {
                ResultSet<Npc> banks = NpcQuery.newQuery().name("Banker").results();
                if (banks.isEmpty()) {
                    println("Bank query was empty.");
                } else {
                    Npc bank = banks.random();
                    if (bank != null) {
                        println("Yay, we found our bank.");
                        println("Interacted bank: " + bank.interact("Bank"));
                        Execution.delay(random.nextLong(500, 2000));
                        Bank.depositAllExcept(314);
                        Bank.close();
                        botState = BotState.FISHINGSKILLING;
                    }
                }
            }
            return random.nextLong(1500, 3000);
        }

    }

