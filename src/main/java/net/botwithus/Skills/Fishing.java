package net.botwithus.Skills;

public class Fishing {
}

//package net.botwithus;
//
//import net.botwithus.api.game.hud.inventories.Backpack;
//import net.botwithus.api.game.hud.inventories.Bank;
//import net.botwithus.api.game.hud.inventories.Inventory;
//import net.botwithus.internal.scripts.ScriptDefinition;
//import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
//import net.botwithus.rs3.events.impl.SkillUpdateEvent;
//import net.botwithus.rs3.game.Area;
//import net.botwithus.rs3.game.Client;
//import net.botwithus.rs3.game.Item;
//import net.botwithus.rs3.game.hud.interfaces.Interfaces;
//import net.botwithus.rs3.game.minimenu.MiniMenu;
//import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
//import net.botwithus.rs3.game.movement.Movement;
//import net.botwithus.rs3.game.movement.NavPath;
//import net.botwithus.rs3.game.movement.TraverseEvent;
//import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
//import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
//import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
//import net.botwithus.rs3.game.queries.results.ResultSet;
//import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
//import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
//import net.botwithus.rs3.game.scene.entities.object.SceneObject;
//import net.botwithus.rs3.game.skills.Skills;
//import net.botwithus.rs3.script.Execution;
//import net.botwithus.rs3.script.LoopingScript;
//import net.botwithus.rs3.script.config.ScriptConfig;
//import net.botwithus.rs3.game.Coordinate;
//import net.botwithus.rs3.util.Regex;
//
//
//import java.util.*;
//        import java.util.concurrent.TimeUnit;
//import java.util.regex.Pattern;
//
//public class SkeletonScript extends LoopingScript {
//    private BotState botState = BotState.IDLE;
//    private Random random = new Random();
//    public int fishGained = 0;
//    private boolean isSkilling = false;
//
//    /////////////////////////////////////Botstate//////////////////////////
//    enum BotState {
//        //define your own states here
//        IDLE,
//        SKILLING,
//        BANKING,
//        MENABANKING,
//        MENAPHOS,
//        //...
//    }
//
//    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
//        super(s, scriptConfig, scriptDefinition);
//        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);
//
//        // Subscribe to InventoryUpdateEvent
//        subscribe(InventoryUpdateEvent.class, inventoryUpdateEvent -> {
//            if (isSkilling) {
//                Item item = inventoryUpdateEvent.getNewItem();
//                fishGained++;
//            }
//        });
//    }
//
//    @Override
//    public void onLoop() {
//        LocalPlayer player = Client.getLocalPlayer();
//        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
//            Execution.delay(random.nextLong(3000, 7000));
//            return;
//        }
//
//        switch (botState) {
//            case IDLE -> {
//                println("We're idle!");
//                Execution.delay(random.nextLong(1000, 3000));
//            }
//            case SKILLING -> {
//                Execution.delay(handleSkilling(player));
//            }
//            case BANKING -> {
//                Execution.delay(Banking());
//            }
//            case MENABANKING -> {
//                Execution.delay(MenaBanking());
//            }
//            case MENAPHOS -> {
//                Execution.delay(Menaphos(player));
//            }
//        }
//    }
//
//    private long Menaphos(LocalPlayer player) {
//        Npc fishing = NpcQuery.newQuery().name("Fishing spot").results().nearest();
//        if (Backpack.isFull()) {
//            println("Inventory is full.");
//            botState = BotState.MENABANKING;
//            return random.nextLong(250, 1500);
//        }
//        if (fishing != null && player.getAnimationId() == -1 && !player.isMoving()){
//            Execution.delay(random.nextLong(1000, 3000));
//            println("Interacted fishing spot: " + fishing.interact("Bait"));
//        }
//        else if (fishing == null){
//            println("No fishing spot found.");
//        }
//        return random.nextLong(500,1000);
//    }
//
//    private long MenaBanking() {
//        SceneObject Bank = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
//        Execution.delay(random.nextLong(1000, 3000));
//        Bank.interact("Load Last Preset from");
//        Execution.delay(random.nextLong(1000, 3000));
//        botState = BotState.MENAPHOS;
//        return random.nextLong(1500,3000);
//    }
//
//    private long handleSkilling(LocalPlayer player) {
//        Area.Polygonal area = new Area.Polygonal(
//                new Coordinate(2833, 2973, 0),
//                new Coordinate(2849, 2973, 0),
//                new Coordinate(2855, 2975, 0),
//                new Coordinate(2868, 2973, 0),
//                new Coordinate(2868, 2969, 0),
//                new Coordinate(2833, 2969, 0));
//        Npc fishing = NpcQuery.newQuery().name("Fishing spot").inside(area).results().nearest();
//        //if our inventory is full, lets bank.
//        if (Backpack.isFull()) {
//            println("Inventory is full.");
//            isSkilling = false;  // Stop skilling
//            botState = BotState.BANKING;
//            return random.nextLong(250, 1500);
//        }
//        if (Bank.isOpen()) { Bank.close(); }
//        else if (fishing != null && player.getAnimationId() == -1 && !player.isMoving()){
//            isSkilling = true;  // Start skilling
//            print(isSkilling);
//            println("Fishing spot found. Interacting.");
//            Execution.delay(random.nextLong(500, 2000));
//            println("Interacted fishing spot: " + fishing.interact("Lure"));
//        }
//        else if (fishing == null){
//            println("No fishing spot found.");
//            isSkilling = false;  // Stop skilling
//        }
//        return random.nextLong(500,1000);
//    }
//
//    private long Banking() {
//        //go to area
//        isSkilling = false;
//        if (Bank.isOpen()) {
//            println("Bank is open");
//            Execution.delay(random.nextLong(500, 2000));
//            Bank.depositAllExcept(314);
//            Bank.close();
//            botState = BotState.SKILLING;
//        }
//        else {
//            ResultSet<Npc> banks = NpcQuery.newQuery().name("Banker").results();
//            if (banks.isEmpty()) {
//                println("Bank query was empty.");
//            } else {
//                Npc bank = banks.random();
//                if (bank != null) {
//                    println("Yay, we found our bank.");
//                    println("Interacted bank: " + bank.interact("Bank"));
//                    Execution.delay(random.nextLong(500, 2000));
//                    Bank.depositAllExcept(314);
//                    Bank.close();
//                    botState = BotState.SKILLING;
//                }
//            }
//        }
//        return random.nextLong(1500,3000);
//    }
//
//    private long startTime;
//    public int fishPerHour;
//    public boolean initialize() {
//        startTime = System.currentTimeMillis();
//        xpGained = 0;
//        levelsGained = 0;
//
//        subscribe(SkillUpdateEvent.class, skillUpdateEvent -> {
//            if (skillUpdateEvent.getId() == Skills.FISHING.getId()) {
//                xpGained += (skillUpdateEvent.getExperience() - skillUpdateEvent.getOldExperience());
//                if (skillUpdateEvent.getOldActualLevel() < skillUpdateEvent.getActualLevel()) {
//                    levelsGained++;
//                }
//            }
//        });
//
//        return super.initialize();
//    }
//
//    public int fishPerHour() {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime > startTime) {
//            fishPerHour = (int) (fishGained * 3600000.0 / (currentTime - startTime));
//        }
//        return fishPerHour;
//    }
//
//    /////////////////STATISTICS////////////////////
//    //XP Gain & Level Gain base is set to zero,
//    private int xpGained = 0;
//    private int levelsGained = 0;
//    private int xpPerHour;
//    private String ttl; // Time to level
//
//    //XP Gain & Level Gain is calculated and added to base
//
//    public String levelsGained() {
//        return levelsGained + " Levels";
//    }
//
//    public String xpPerHour() {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime > startTime) {
//            xpPerHour = (int) (xpGained * 3600000.0 / (currentTime - startTime));
//        }
//        return xpPerHour + " XP/hr";
//    }
//
//    public String ttl() {
//        if (xpPerHour > 0) {
//            int xpToNextLevel = Skills.FISHING.getExperienceToNextLevel();
//            int totalSeconds = (int) (xpToNextLevel * 3600.0 / xpPerHour);
//            int hours = totalSeconds / 3600;
//            int minutes = (totalSeconds % 3600) / 60;
//            int seconds = totalSeconds % 60;
//            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
//        }
//        return "N/A";
//    }
//
//    public String timePassed() {
//        long currentTime = System.currentTimeMillis();
//        long elapsedMillis = currentTime - startTime;
//        long hours = elapsedMillis / 3600000;
//        long minutes = (elapsedMillis % 3600000) / 60000;
//        long seconds = (elapsedMillis % 60000) / 1000;
//        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
//    }
//
//    public String xpGained() {
//        return xpGained + " XP";
//    }
//
//
//    ////////////////////Botstate/////////////////////
//    public BotState getBotState() {
//        return botState;
//    }
//
//    public void setBotState(BotState botState) {
//        this.botState = botState;
//    }
//}
//

