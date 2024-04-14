package net.botwithus;

import net.botwithus.Skills.*;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;


import java.util.*;

public class SkeletonScript extends LoopingScript {
    public BotState botState = BotState.IDLE;
    private Random random = new Random();
    private SkeletonScriptGraphicsContext GraphicsContext;

    /////////////////////////////////////Botstate//////////////////////////
    public enum BotState {
        //define your own states here
        IDLE,
        DIVINATION,
        DIVINATIONDEPOSIT,
        DIVINATIONTRAVERSE,
        CRAFTINGSKILLING,
        CRAFTINGTRAVERSE,
        CRAFTINGBANKING,
        FISHINGSKILLING,
        FISHINGBANKING,
        FISHINGTRAVERSE,
        FISHINGMENAPHOS,
        FISHINGMENABANKING,
        RUNECRAFTING,
        COOKINGSKILLING,
        COOKINGTRAVERSE,
        COOKINGBANKING
        //...
    }

    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);
        loadConfiguration(); // Load configuration when the script starts
        GraphicsContext = (SkeletonScriptGraphicsContext) sgc;
}

    @Override
    public void onLoop() {
        this.loopDelay = 1000;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            Execution.delay(random.nextLong(3000, 7000));
            return;
        }
        Execution.delay(random.nextLong(500, 1000));
        Execution.delay(random.nextLong(500, 1000));
        processQueueItems();
        Execution.delay(random.nextLong(500, 1000));

        switch (botState) {
            case IDLE -> {
                println("We're idle!");
                Execution.delay(random.nextLong(1000, 3000));
            }
            case DIVINATION -> {
                //do questing stuff
                Execution.delay(Divination.handleSkilling(player, Divination.wispState.name()));
            }
            case DIVINATIONDEPOSIT -> {
                //do deposit stuff
                Execution.delay(Divination.deposit());
            }
            case DIVINATIONTRAVERSE -> {
                //do traverse stuff
                Execution.delay(Divination.moveToColony());
            }
            case CRAFTINGSKILLING -> {
                //do crafting stuff
                Execution.delay(Crafting.handleSkilling(player));
            }
            case CRAFTINGTRAVERSE -> {
                //do traverse stuff
                Execution.delay(Crafting.Traverse());
            }
            case CRAFTINGBANKING -> {
                //do banking stuff
                Execution.delay(Crafting.Banking());
            }
            case FISHINGSKILLING -> {
                //do fishing stuff
                Execution.delay(Fishing.handleSkilling(player));
            }
            case FISHINGBANKING -> {
                //do banking stuff
                Execution.delay(Fishing.Banking());
            }
//            case FISHINGTRAVERSE -> {
//                //do traverse stuff
//                Execution.delay(fishingSkill.Menaphos(player));
//            }
            case FISHINGMENAPHOS -> {
                //do traverse stuff
                Execution.delay(Fishing.Menaphos(player));
            }
            case FISHINGMENABANKING -> {
                //do banking stuff
                Execution.delay(Fishing.MenaBanking());
            }
            case RUNECRAFTING -> {
                //do runecrafting stuff
                Execution.delay(RuneCrafting.interactWithPriorityObjects(player));;
            }
            case COOKINGSKILLING -> {
                //do cooking stuff
                Execution.delay(Cooking.handleCooking(player));
            }
            case COOKINGTRAVERSE -> {
                //do traverse stuff
                Execution.delay(Cooking.Traverse());
            }
            case COOKINGBANKING -> {
                //do banking stuff
                Execution.delay(Cooking.handleBanking());
            }
        }
        println("We're done with loop!");
    }

    private void processQueueItems() {
        println("Starting to process queue items...");
        if (GraphicsContext.botStateQueue.isEmpty()) {
            println("Queue is empty.");
            return;
        }

        Iterator<SkeletonScriptGraphicsContext.BotQueueItem> iterator = GraphicsContext.botStateQueue.iterator();
        while (iterator.hasNext()) {
            SkeletonScriptGraphicsContext.BotQueueItem item = iterator.next();
            println("Processing item: " + item.state);

            Skills skill = GraphicsContext.mapStateToSkill(item.state);  // Convert state to skill
            if (skill == null) {
                println("Skill mapping failed for: " + item.state);
                continue;
            }

            int currentSkillLevel = GraphicsContext.getCurrentSkillLevel(); // Get current skill level for this skill
            println("Current skill level for " + skill + ": " + currentSkillLevel + ", target: " + item.targetLevel);

            if (currentSkillLevel > item.targetLevel) {
                println("Removing " + item.state + " from queue because current level " + currentSkillLevel + " is greater than target " + item.targetLevel);
                iterator.remove(); // Correctly remove using iterator
            }
        }
        println("Finished processing queue items.");
    }

    ////////////////Save & Load Config/////////////////////
    void loadConfiguration() {
        try {
            String selectedWispTypeName = configuration.getProperty("selectedWispType");
            if (selectedWispTypeName != null && !selectedWispTypeName.isEmpty()) {
                // Convert the saved name back to a WispType enum
                Divination.WispType selectedWispType = Divination.WispType.valueOf(selectedWispTypeName);
                Divination.setWispType(selectedWispType);
                println("WispType configuration loaded successfully: " + selectedWispType.name());
            }

            // Load and set the state of progressive mode
            String progressiveModeEnabledString = configuration.getProperty("progressiveModeEnabled");
            if (progressiveModeEnabledString != null && !progressiveModeEnabledString.isEmpty()) {
                GraphicsContext.progressiveModeEnabled = Boolean.parseBoolean(progressiveModeEnabledString);
                println("Progressive mode configuration loaded successfully: " + progressiveModeEnabledString);
            }

        } catch (Exception e) {
            println("Error loading configuration: \n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            println("This is a non-fatal error, you can ignore it.");
        }
    }

    void saveConfiguration() {
        try {
            // Save the selected WispType using its name
            configuration.addProperty("selectedWispType", Divination.getCurrentWispType().name());
            configuration.addProperty("progressiveModeEnabled", String.valueOf(GraphicsContext.progressiveModeEnabled));
            configuration.save();
            println("WispType configuration saved successfully.");
        } catch (Exception e) {
            println("Error saving configuration: \n" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            println("This is a non-fatal error, you can ignore it.");
        }
    }

    ////////////////////Botstate/////////////////////
    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }
}

