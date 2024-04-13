package net.botwithus.Skills;

import net.botwithus.SkeletonScript;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skill;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.util.Random;

public class Cooking extends SkeletonScript {
    private Random random = new Random();
    Area.Rectangular area = new Area.Rectangular(new Coordinate(2792, 3455, 0), new Coordinate(2808, 3437, 0));
    public Cooking(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
    }

    @Override
    public void onLoop() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            //wait some time so we dont immediately start on login.
            Execution.delay(random.nextLong(3000, 7000));
        }
    }

    public long Traverse() {
        if (Bank.isOpen()) { Bank.close(); }
        if (Movement.traverse(NavPath.resolve(area)) == TraverseEvent.State.FINISHED) {
            botState = BotState.COOKINGSKILLING;
        } else {
            println("Failed to traverse to cooking station");
        }
        return random.nextLong(1500,3000);
    }



    public long handleCooking(LocalPlayer player) {
        if (area.contains(player)) {
            SceneObject Cookingrange = SceneObjectQuery.newQuery().id(45319).results().nearest();
            Execution.delay(random.nextLong(500, 1000));
            Cookingrange.interact("Cook-at");
            Execution.delay(random.nextLong(2000, 3000));
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            Execution.delay(random.nextLong(2000, 3000));
            delayUntil(35000, () -> !Interfaces.isOpen(1251));
            Execution.delay(random.nextLong(500, 1000));
            botState = BotState.COOKINGBANKING;
        }
        else {
            botState = BotState.COOKINGTRAVERSE;
        }
        return random.nextLong(500, 1000);
    }

    public long handleBanking() {
        EntityResultSet<SceneObject> BankBooths = SceneObjectQuery.newQuery().id(104414).option("Bank").results();
        SceneObject Banks = BankBooths.random();
        if (Banks != null) {
            Banks.interact("Bank");
            Execution.delay(random.nextLong(1000, 2000));
            Bank.depositAll();
            Execution.delay(random.nextLong(1000, 2000));
            int cookingLevel = checkCookingLevel();
            if (cookingLevel < 15) {
                // Crayfish
                Bank.withdrawAll(13425);
            } else if (cookingLevel < 25) {
                // Trout
            } else if (cookingLevel < 52) {
                // Salmon & Trout
                Bank.withdrawAll(13425);
                if (!Backpack.contains(13425));{
                    Bank.withdrawAll(331);
                }
            } else if (cookingLevel < 80) {
                // Desert Soles
                Bank.withdrawAll(40287);
            }
            Execution.delay(random.nextLong(1000, 2000));
            Bank.close();
            botState = BotState.COOKINGSKILLING;
        }
        else {
            println("Failed to find a bank booth");
        }
        return random.nextLong(500,1000);
    }


    private int checkCookingLevel(){
        // Your logic to check the cooking level
        Skill cookingSkill = Skills.COOKING.getSkill();
        return cookingSkill.getActualLevel();
    }
}
