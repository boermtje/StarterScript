package net.botwithus;

import net.botwithus.Skills.Divination;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
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
        botStateMap.put("RuneCrafting State", SkeletonScript.BotState.RUNECRAFTING);
        botStateMap.put("Divination State", SkeletonScript.BotState.DIVINATION);

        // Assuming each state has a default starting skill level target
        botStateQueue.add(new BotQueueItem(SkeletonScript.BotState.DIVINATION, 50, Skills.DIVINATION));
        botStateQueue.add(new BotQueueItem(SkeletonScript.BotState.RUNECRAFTING, 30, Skills.RUNECRAFTING));
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("Starter Script", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Main Settings + Queue", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Current script state: " + script.getBotState().name());

                    // Display the queue
                    // Specify a width and height for the child window
                    if (ImGui.BeginChild("QueueList", 300.0f, 150.0f, true, 0)) {
                        for (Iterator<BotQueueItem> iterator = botStateQueue.iterator(); iterator.hasNext();) {
                            BotQueueItem item = iterator.next();
                            ImGui.PushID(item.state.name()); // Ensure unique ID for controls
                            ImGui.Text(item.state.name() + " until level: ");
                            ImGui.SameLine();

                            int newLevel = ImGui.InputInt("##level" + item.state.name(), item.targetLevel);
                            if (newLevel != item.targetLevel) {
                                item.targetLevel = newLevel;
                            }

                            ImGui.SameLine();
                            if (ImGui.Button("Remove##" + item.state)) {
                                iterator.remove(); // Use iterator.remove() to avoid ConcurrentModificationException
                            }
                            ImGui.PopID();
                        }
                        ImGui.EndChild();
                    }
                    if (botStateQueue.isEmpty()) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
                        //Lobby logic
                    }

                    // Allow adding new states to the queue
                    String[] botStateNames = botStateMap.keySet().toArray(new String[0]);
                    selectedItem = new NativeInteger(0); // Manage the selected index
                    if (ImGui.Combo("Add Bot State", selectedItem, botStateNames)) {
                        Skills selectedSkill = mapStateToSkill(botStateMap.get(botStateNames[selectedItem.get()]));
                        // Assuming you want to set a default target level for the new state, e.g., 0
                        botStateQueue.add(new BotQueueItem(botStateMap.get(botStateNames[selectedItem.get()]), 0, selectedSkill));
                    }

                    if (ImGui.Button("Start Queue")) {
                        // Logic to start processing the queue
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        script.setBotState(SkeletonScript.BotState.IDLE);
                    }
                    ImGui.EndTabItem();
                }
                if (ImGui.BeginTabItem("Divination", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Current Wisp Type: " + (divinationSkill.getwispState()));

                    if (ImGui.Checkbox("Enable Progressive Mode", progressiveModeEnabled)) {
                        progressiveModeEnabled = !progressiveModeEnabled;
                        // If progressive mode just got enabled, automatically set the highest available wisp type
                        if (progressiveModeEnabled) {
                            int currentLevel = divinationSkill.currentDivinationLevel;
                            Divination.WispType highestAvailableWisp = divinationSkill.getHighestAvailableWisp(currentLevel);
                            divinationSkill.setWispType(highestAvailableWisp);
                        }
                        script.saveConfiguration(); // Save the new selection
                    }

                    if (!progressiveModeEnabled) {
                        String[] wispTypes = Arrays.stream(Divination.WispType.values())
                                .map(Enum::name)
                                .toArray(String[]::new);
                        NativeInteger selectedWisp = new NativeInteger(divinationSkill.getCurrentWispType().ordinal());
                        if (ImGui.Combo("Wisp Type", selectedWisp, wispTypes)) {
                            Divination.WispType newWispType = Divination.WispType.values()[selectedWisp.get()];
                            divinationSkill.setWispType(newWispType);
                            script.saveConfiguration(); // Save the new selection
                        }
                        if (ImGui.Button("Start Divination")) {
                            script.setBotState(SkeletonScript.BotState.DIVINATION);
                        }
                        ImGui.SameLine();
                        if (ImGui.Button("Stop Divination")) {
                            script.setBotState(SkeletonScript.BotState.IDLE);
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

    public int getCurrentSkillLevel(Skills skill) {
        return skill.getLevel();  // This should call the method that fetches the level for the specified skill
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}

