package net.botwithus;

import net.botwithus.Skills.Divination;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;


import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static net.botwithus.rs3.game.skills.Skills.DIVINATION;

public class SkeletonScript extends LoopingScript {
    public BotState botState = BotState.IDLE;
    private Random random = new Random();
    private Divination divinationSkill;

    /////////////////////////////////////Botstate//////////////////////////
    public enum BotState {
        //define your own states here
        IDLE,
        DIVINATIONSKILLING,
        DIVINATIONDEPOSIT,
        DIVINATIONTRAVERSE
        //...
    }

    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);
    }

    @Override
    public void onLoop() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN) {
            Execution.delay(random.nextLong(3000, 7000));
            return;
        }

        switch (botState) {
            case IDLE -> {
                println("We're idle!");
                Execution.delay(random.nextLong(1000, 3000));
            }
            case DIVINATIONSKILLING -> {
                //do questing stuff
                Execution.delay(divinationSkill.handleSkilling(player, divinationSkill.wispState.name()));
            }
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

