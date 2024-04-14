package net.botwithus;

import net.botwithus.Skills.Divination;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeBoolean;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.input.GameInput;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

import java.util.*;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {
    private SkeletonScript script;
    private NativeInteger selectedItem;
    private Divination divinationSkill;
    public boolean progressiveModeEnabled = false;
    private Map<String, SkeletonScript.BotState> botStateMap;
    public Queue<BotQueueItem> botStateQueue = new LinkedList<>();
    public void removeTask(SkeletonScript.BotState stateToRemove) {
        botStateQueue.removeIf(item -> item.state == stateToRemove);
    }

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

    private Skills mapStateToSkill(SkeletonScript.BotState state) {
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
                    NativeInteger tempTargetLevel = new NativeInteger(0);


                    // Render combo box and level input only if there's at least one state to select
                    if (botStateNames.length > 0) {
                        if (ImGui.Combo("State", tempSelectedState, botStateNames)) {
                            // Combo box selection is registered here
                        }

                        ImGui.InputInt("Target Level", tempTargetLevel.get());

                        if (ImGui.Button("Add to Queue")) {
                            // Button to confirm addition to the queue
                            SkeletonScript.BotState selectedState = botStateMap.get(botStateNames[tempSelectedState.get()]);
                            Skills selectedSkill = mapStateToSkill(selectedState);
                            int targetLevel = tempTargetLevel.get();
                            botStateQueue.add(new BotQueueItem(selectedState, targetLevel, selectedSkill));
                        }
                    }

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
//                    ImGui.Text("Current Wisp Type: " + (divinationSkill.getwispState()));
//
//                    if (ImGui.Checkbox("Enable Progressive Mode", progressiveModeEnabled)) {
//                        progressiveModeEnabled = !progressiveModeEnabled;
//                        // If progressive mode just got enabled, automatically set the highest available wisp type
//                        if (progressiveModeEnabled) {
//                            int currentLevel = divinationSkill.currentDivinationLevel;
//                            Divination.WispType highestAvailableWisp = divinationSkill.getHighestAvailableWisp(currentLevel);
//                            divinationSkill.setWispType(highestAvailableWisp);
//                        }
//                        script.saveConfiguration(); // Save the new selection
//                    }
//
//                    if (!progressiveModeEnabled) {
//                        String[] wispTypes = Arrays.stream(Divination.WispType.values())
//                                .map(Enum::name)
//                                .toArray(String[]::new);
//                        NativeInteger selectedWisp = new NativeInteger(divinationSkill.getCurrentWispType().ordinal());
//                        if (ImGui.Combo("Wisp Type", selectedWisp, wispTypes)) {
//                            Divination.WispType newWispType = Divination.WispType.values()[selectedWisp.get()];
//                            divinationSkill.setWispType(newWispType);
//                            script.saveConfiguration(); // Save the new selection
//                        }
//                    }
                    if (ImGui.Button("Start Div")) {
                        script.setBotState(SkeletonScript.BotState.DIVINATION);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop Div")) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
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

    public int getCurrentSkillLevel(Skills skill) {
        return skill.getLevel();  // This should call the method that fetches the level for the specified skill
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}

