package net.botwithus;

import net.botwithus.Skills.Divination;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {
    private SkeletonScript script;
    private NativeInteger selectedItem;
    private Divination divinationSkill;
    public boolean progressiveModeEnabled = false;
    private Map<String, SkeletonScript.BotState> botStateMap;

    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SkeletonScript script) {
        super(scriptConsole);
        this.script = script;
        initializeBotStateMap();
    }

    private void initializeBotStateMap() {
        botStateMap = new HashMap<>();
        botStateMap.put("Idle State", SkeletonScript.BotState.IDLE);
        botStateMap.put("RuneCrafting State", SkeletonScript.BotState.RUNECRAFTING);
        botStateMap.put("Divination State", SkeletonScript.BotState.DIVINATIONSKILLING);
        // Add more states as necessary
    }

    @Override
    public void drawSettings () {
        if (ImGui.Begin("Starter Script", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Main Settings + Queue", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("My scripts state is: " + script.getBotState());


                    String[] botStateNames = Arrays.stream(SkeletonScript.BotState.values())
                            .map(Enum::name)
                            .toArray(String[]::new);

                    // Use NativeInteger to manage the selected index
                    if (ImGui.Combo("Bot State", selectedItem, botStateNames)) {
                        script.setBotState(SkeletonScript.BotState.values()[selectedItem.get()]);
                    }

                    if (ImGui.Button("Start")) {
                        script.setBotState(SkeletonScript.BotState.values()[selectedItem.get()]);
                    }

                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        //has been clicked
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
                        script.setBotState(SkeletonScript.BotState.DIVINATIONSKILLING);
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

    @Override
    public void drawOverlay() { super.drawOverlay(); }
}
