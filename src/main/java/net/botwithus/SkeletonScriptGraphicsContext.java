package net.botwithus;

import net.botwithus.Skills.Divination;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

import java.util.Arrays;

public class SkeletonScriptGraphicsContext extends ScriptGraphicsContext {
    private SkeletonScript script;
    private NativeInteger selectedItem;
    private Divination divinationSkill;

    public SkeletonScriptGraphicsContext(ScriptConsole scriptConsole, SkeletonScript script) {
        super(scriptConsole);
        this.script = script;
        this.selectedItem = new NativeInteger(script.getBotState().ordinal()); // Initialize with the current state's ordinal
    }

    @Override
    public void drawSettings () {
        if (ImGui.Begin("Starter Script", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Main Settings", ImGuiWindowFlag.None.getValue())) {
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

                    String[] wispTypes = Arrays.stream(Divination.WispType.values())
                            .map(Enum::name)
                            .toArray(String[]::new);
                    NativeInteger selectedWisp = new NativeInteger(divinationSkill.getwispState().ordinal());
                    if (ImGui.Combo("Wisp Type", selectedWisp, wispTypes)) {
                        divinationSkill.setWispType(Divination.WispType.values()[selectedWisp.get()]);
                    }
                    if (ImGui.Button("Start Divination")) {
                        script.setBotState(SkeletonScript.BotState.DIVINATIONSKILLING);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop Divination")) {
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
