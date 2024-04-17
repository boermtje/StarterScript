package net.botwithus;

import net.botwithus.Skills.Divination;
import net.botwithus.rs3.game.skills.*;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;


import java.util.*;

import static net.botwithus.rs3.script.ScriptConsole.println;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {
    private SkeletonScript script;
    public boolean progressiveModeEnabled = true;
    private Map<String, SkeletonScript.BotState> botStateMap;
    public Queue<BotQueueItem> botStateQueue = new LinkedList<>();
    public void removeTask(SkeletonScript.BotState stateToRemove) {
        botStateQueue.removeIf(item -> item.state == stateToRemove);
    }
    String levelInputText = "0";

    public static class BotQueueItem {
        public SkeletonScript.BotState state;
        public int targetLevel;
        public Skills skill;

        public BotQueueItem(SkeletonScript.BotState state, int targetLevel, Skills skill) {
            this.state = state;
            this.targetLevel = targetLevel;
            this.skill = skill;
        }
    }

    public Skills mapStateToSkill(SkeletonScript.BotState state) {
        // Your logic to map a BotState to a Skills enum value
        // Example:
        switch (state) {
            case DIVINATION:
                return Skills.DIVINATION;
            case RUNECRAFTING:
                return Skills.RUNECRAFTING;
        }
        return null;
    }

    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SkeletonScript script) {
        super(scriptConsole);
        this.script = script;
        initializeBotStateMap();
    }


    public void initializeBotStateMap() {
        botStateMap = new HashMap<>();
        // Initialize states with default values for demonstration
        botStateMap.put("RuneCrafting", SkeletonScript.BotState.RUNECRAFTING);
        botStateMap.put("Divination", SkeletonScript.BotState.DIVINATION);
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("Starter Script", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Main Settings + Queue", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Current script state: " + script.getBotState().name());
                    if (botStateQueue.isEmpty()) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
                        // Lobby logic
                    }

                    String[] botStateNames = botStateMap.keySet().toArray(new String[0]);

                    // Temporary state for new queue item to be added
                    NativeInteger tempSelectedState = new NativeInteger(0);
                    levelInputText = ImGui.InputText("Level", levelInputText);

                    // Render combo box and level input only if there's at least one state to select
                    if (botStateNames.length > 0) {
                        if (ImGui.Combo("State", tempSelectedState, botStateNames)) {
                            // Combo box selection is registered here
                        }
                        try {
                            int level = Integer.parseInt(levelInputText);

                            // Update the NativeInteger field (if you are using one) or use the level directly
                            // Assuming you have a method or variable to store the parsed level

                            // Button logic to add to queue
                            if (ImGui.Button("Add to Queue")) {
                                // Use the parsed level to add to your queue
                                SkeletonScript.BotState selectedState = botStateMap.get(botStateNames[tempSelectedState.get()]);
                                Skills selectedSkill = mapStateToSkill(selectedState);
                                botStateQueue.add(new BotQueueItem(selectedState, level, selectedSkill));
                            }
                        } catch (NumberFormatException e) {
                            // Handle invalid number format
                            System.err.println("Invalid input: Level must be an integer.");
                        }
                    }
                    ImGui.Separator();

                    if (ImGui.Button("Start Queue")) {
                        // Logic to start processing the queue
                        if (!botStateQueue.isEmpty()) {
                            BotQueueItem currentItem = botStateQueue.peek();
                            script.setBotState(currentItem.state);
                        }
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
                    }

                    if (ImGui.BeginChild("QueueList", 400.0f, 150.0f, true, ImGuiWindowFlag.HorizontalScrollbar.getValue())) {
                        for (Iterator<BotQueueItem> iterator = botStateQueue.iterator(); iterator.hasNext();) {
                            BotQueueItem item = iterator.next();
                            ImGui.PushID(item.state.name()); // Ensure unique ID for controls
                            ImGui.Text(item.state.name() + " until level: " + String.valueOf(item.targetLevel));
                            ImGui.SameLine();
                            if (ImGui.Button("Remove##" + item.state)) {
                                iterator.remove(); // Use iterator.remove() to avoid ConcurrentModificationException
                            }
                            ImGui.PopID();
                        }
                        ImGui.EndChild();
                    }

                    ImGui.EndTabItem();
                }


                if (ImGui.BeginTabItem("Divination", ImGuiWindowFlag.None.getValue())) {
                    if (ImGui.Button("Start Div")) {
                        script.setBotState(SkeletonScript.BotState.DIVINATION);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop Div")) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
                    }
                    ImGui.Text("Current Wisp Type: " + (Divination.getwispState()));

                    progressiveModeEnabled = (ImGui.Checkbox("Enable Progressive Mode", progressiveModeEnabled));
                    // If progressive mode just got enabled, automatically set the highest available wisp type
                        if (!progressiveModeEnabled) {
                            String[] wispTypes = Arrays.stream(Divination.WispType.values())
                                    .map(Enum::name)
                                    .toArray(String[]::new);
                            NativeInteger selectedWisp = new NativeInteger(Divination.getCurrentWispType().ordinal());
                            if (ImGui.Combo("Wisp Type", selectedWisp, wispTypes)) {
                                Divination.WispType newWispType = Divination.WispType.values()[selectedWisp.get()];
                                Divination.setWispType(newWispType);
                                script.saveConfiguration(); // Save the new selection
                            }
                        }
                    ImGui.EndTabItem();
                }


                if (ImGui.BeginTabItem("Runecrafting", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("My scripts state is: " + script.getBotState());
                    if (ImGui.Button("Start")) {
                        script.setBotState(SkeletonScript.BotState.RUNECRAFTING);
                    }

                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        //has been clicked
                        script.setBotState(SkeletonScript.BotState.IDLE);
                    }
                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }
    }

    public int getCurrentSkillLevel() {
        if (!botStateQueue.isEmpty()) {
            BotQueueItem currentItem = botStateQueue.peek(); // Get the current item from the queue
            Skills currentSkill = mapStateToSkill(currentItem.state); // Map the state to a skill
            if (currentSkill != null) {
                return currentSkill.getActualLevel();  // Retrieve the actual level for this skill
            } else {
                System.err.println("Skill mapping returned null for state: " + currentItem.state);
                return -1; // Error handling, adjust as necessary
            }
        } else {
            System.err.println("Queue is empty");
            return -1;  // Queue is empty, handle as needed
        }
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}

