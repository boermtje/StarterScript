package net.botwithus.Skills;

import net.botwithus.SkeletonScript;
import net.botwithus.SkeletonScript.BotState;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.util.Regex;

import java.util.HashMap;
import java.util.Random;
import java.util.regex.Pattern;

public class Divination extends SkeletonScript {
    public WispType wispState = WispType.PALE;
    private boolean someBool = true;
    private Random random = new Random();
    public HashMap<String, Area> Colonies;

    public enum WispType {
        PALE, FLICKERING, BRIGHT, GLOWING, SPARKLING, GLEAMING, VIBRANT,
        LUSTROUS, ELDER, BRILLIANT, RADIANT, LUMINOUS, INCANDESCENT
    }
    public Divination(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        initializeMaps(); // Call to initialize maps
        subscribe(ChatMessageEvent.class, chatMessageEvent -> {
            if (chatMessageEvent.getMessage().contains("A chronicle escapes from the spring!")) {
                println("Chronicle found!");
                Npc chronicle = NpcQuery.newQuery().id(18204).results().first();
                println(chronicle);
                if (chronicle != null) {
                    Execution.delay(random.nextLong(300, 500));
                    chronicle.interact("Capture");
                }
                else {
                    println("Chronicle is null");
                }
            }
        });
    }

    private void initializeMaps() {
        Colonies = new HashMap<>();
        Area.Rectangular Pale = new Area.Rectangular(new Coordinate(3112, 3223, 0), new Coordinate(3123, 3211, 0));
        Colonies.put("Pale", Pale);
        Area.Rectangular Flickering = new Area.Rectangular(new Coordinate(2999, 3410, 0), new Coordinate(3008, 3400, 0));
        Colonies.put("Flickering", Flickering);
        Area.Rectangular Bright = new Area.Rectangular(new Coordinate(3295, 3404, 0), new Coordinate(3304, 3395, 0));
        Colonies.put("Bright", Bright);
        Area.Rectangular Sparkling = new Area.Rectangular(new Coordinate(2727,3420, 0), new Coordinate(2727, 3412, 0));
        Colonies.put("Sparkling", Sparkling);
        Area.Rectangular Gleaming = new Area.Rectangular(new Coordinate(2896, 3038, 0), new Coordinate(2878, 3058, 0));
        Colonies.put("Gleaming", Gleaming);
        Area.Rectangular Vibrant = new Area.Rectangular(new Coordinate(2430, 2873, 0), new Coordinate(2407, 2854, 0));
        Colonies.put("Vibrant", Vibrant);
        Area.Rectangular Lustrous = new Area.Rectangular(new Coordinate(2416, 2870, 0), new Coordinate(2421, 2864, 0));
        Colonies.put("Lustrous", Lustrous);
        Area.Rectangular Elder = new Area.Rectangular(new Coordinate(2884, 6086, 0), new Coordinate(4385, 6070, 0));
        Colonies.put("Elder", Elder);
        Area.Rectangular Brilliant = new Area.Rectangular(new Coordinate(3390, 6086, 0), new Coordinate(4385, 6070, 0));
        Colonies.put("Brilliant", Brilliant);
        Area.Rectangular Radiant = new Area.Rectangular(new Coordinate(3396, 6086, 0), new Coordinate(4385, 6070, 0));
        Colonies.put("Radiant", Radiant);
        Area.Rectangular Luminous = new Area.Rectangular(new Coordinate(4371, 6086, 0), new Coordinate(4385, 6070, 0));
        Colonies.put("Luminous", Luminous);
        Area.Rectangular Incandescent = new Area.Rectangular(new Coordinate(2273, 3057, 0), new Coordinate(2285, 3045, 0));
        Colonies.put("Incandescent", Incandescent);
    }

    public boolean isInArea(Area area, LocalPlayer player) {
        // Check if player's coordinate is within the area
        return area.contains(player.getCoordinate());
    }

    @Override
    public void onLoop() {
        //Loops every 100ms by default, to change:
        this.loopDelay = 5000;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            //wait some time so we dont immediately start on login.
            Execution.delay(random.nextLong(3000, 7000));
            return;
        }
    }

    public long moveToColony() {
        if (Movement.traverse(NavPath.resolve(Colonies.get(wispState.name()))) == TraverseEvent.State.FINISHED) {
            botState = BotState.DIVINATIONSKILLING;
        } else {
            println("Failed to traverse to colony");
        }
        return random.nextLong(1000, 1500);
    }

    private long deposit() {
        long startTime = System.currentTimeMillis();
        long maxDuration = 180000; // 3 minutes in milliseconds

        println("Backpack is full");
        Execution.delay(random.nextLong(1000, 2000));

        while (System.currentTimeMillis() - startTime < maxDuration) {
            SceneObject rift = SceneObjectQuery.newQuery().name("Energy rift").results().first();
            if (rift != null) {
                rift.interact("Convert memories");
                Execution.delayUntil(5000, () -> !containsMemoryItems());
                if (!containsMemoryItems()) {
                    break;
                }
            }

            SceneObject Rift = SceneObjectQuery.newQuery().name("Energy Rift").results().first();
            if (Rift != null) {
                Rift.interact("Convert memories");
                Execution.delayUntil(3000, () -> !containsMemoryItems());
                if (!containsMemoryItems()) {
                    break;
                }
            }
        }

        if (containsMemoryItems()) {
            println("Backpack is still full");
            botState = BotState.DIVINATIONDEPOSIT;
        } else {
            botState = BotState.DIVINATIONSKILLING;
            println("Backpack is empty");
        }

        return random.nextLong(1000, 1500);
    }

    private boolean containsMemoryItems() {
        Pattern memoryPattern = Regex.getPatternForContainsString(" memory");
        for (Item item : Backpack.getItems()) {
            if (memoryPattern.matcher(item.getName()).find()) {
                return true;
            }
        }
        return false;
    }

    public long handleSkilling(LocalPlayer player, String WispType) {
        Area area = Colonies.get(wispState.name());
        if (!isInArea(area, player)) {
            System.out.println("Change state to traverse");
            botState = BotState.DIVINATIONTRAVERSE;
            return random.nextLong(500, 800);
        }
        if (Backpack.isFull()) {
            botState = BotState.DIVINATIONDEPOSIT;
        }
        else if (player.getAnimationId() == -1) {
            println("Player is not harvesting");
            println("Harvesting " + WispType + " wisp");
            Npc wisp = NpcQuery.newQuery().name(WispType + " wisp").results().nearest();
            if (wisp != null) {
                println("Wisp found!");
                Execution.delay(random.nextLong(500, 1000));
                wisp.interact("Harvest");
                return random.nextLong(500, 1000);
            }
            else {
                println("Wisp is null");
                return 1000;
            }
        }
        else {
            println("Player is already busy");
        }
        return random.nextLong(500, 1000);
    }
    public WispType getwispState() {
        return wispState;
    }

    public void setWispType(WispType wispType) {
        this.wispState = wispType;
    }

    public boolean isSomeBool() {
        return someBool;
    }

    public void setSomeBool(boolean someBool) {
        this.someBool = someBool;
    }
}
