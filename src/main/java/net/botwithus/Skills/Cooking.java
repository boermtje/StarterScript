package net.botwithus.Skills;

import net.botwithus.SkeletonScript;
import net.botwithus.SkeletonScriptGraphicsContext;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.api.game.hud.inventories.BankInventory;
import net.botwithus.api.game.hud.inventories.Inventory;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.minimenu.MiniMenu;
import net.botwithus.rs3.game.minimenu.actions.ComponentAction;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.skills.Skill;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.util.Random;

import static net.botwithus.rs3.script.Execution.delayUntil;
import static net.botwithus.rs3.script.ScriptConsole.println;

public class Cooking {
    static SkeletonScriptGraphicsContext GraphicsContext;
    private static Random random = new Random();
    static Area.Rectangular area = new Area.Rectangular(new Coordinate(2792, 3455, 0), new Coordinate(2808, 3437, 0));
    public Cooking() {
    }

    public static long Traverse() {
        if (Bank.isOpen()) { Bank.close(); }
        if (Movement.traverse(NavPath.resolve(area)) == TraverseEvent.State.FINISHED) {
            SkeletonScript.botState = SkeletonScript.BotState.COOKING;
        } else {
            println("Failed to traverse to cooking station");
        }
        return random.nextLong(1500,3000);
    }



    public static long handleCooking(LocalPlayer player) {
        if (Backpack.isEmpty()) {
            SkeletonScript.botState = SkeletonScript.BotState.COOKINGBANKING;
        }
        if (area.contains(player)) {
            SceneObject Cookingrange = SceneObjectQuery.newQuery().id(45319).results().nearest();
            Execution.delay(random.nextLong(500, 1000));
            println("Cooking at range");
            Cookingrange.interact("Cook-at");
            delayUntil(35000, () -> Interfaces.isOpen(1486));
            Execution.delay(random.nextLong(2000, 3000));
            println("Selecting go cook");
            MiniMenu.interact(ComponentAction.DIALOGUE.getType(), 0, -1, 89784350);
            Execution.delay(random.nextLong(2000, 3000));
            delayUntil(35000, () -> !Interfaces.isOpen(1251));
            Execution.delay(random.nextLong(500, 1000));
            SkeletonScript.botState = SkeletonScript.BotState.COOKINGBANKING;
        }
        else {
            SkeletonScript.botState = SkeletonScript.BotState.COOKINGTRAVERSE;
        }
        return random.nextLong(500, 1000);
    }

    public static long handleBanking() {
        EntityResultSet<SceneObject> BankBooths = SceneObjectQuery.newQuery().id(104414).option("Bank").results();
        SceneObject Banks = BankBooths.random();
        if (Banks != null) {
            Banks.interact("Bank");
            delayUntil(8000, () -> Bank.isOpen());
            Execution.delay(random.nextLong(1000, 2000));
            Bank.depositAll();
            Execution.delay(random.nextLong(1000, 2000));
            int cookingLevel = checkCookingLevel();
            if (cookingLevel >= 52 && Bank.contains("Raw desert sole")) {
                // Desert Soles
                Bank.withdrawAll(40287);
                println("Withdrawing desert soles");
            } else if (cookingLevel >= 25 && Bank.contains("Raw salmon")) {
                // Salmon & Trout
                Bank.withdrawAll("Raw salmon");
                println("Withdrawing salmon");
            } else if (cookingLevel >= 15 && Bank.contains("Raw trout")) {
                // Trout
                Bank.withdrawAll("Raw trout");
                println("Withdrawing trout");
            } else if (cookingLevel >= 1 && Bank.contains("Raw crayfish")) {
                Bank.withdrawAll("Raw crayfish");
                println("Withdrawing crayfish");
            }
            else {
                println("No food to cook");
                Bank.close();
                GraphicsContext.removeTask(SkeletonScript.BotState.COOKING);
            }
            Execution.delay(random.nextLong(1000, 2000));
            Bank.close();
            SkeletonScript.botState = SkeletonScript.BotState.COOKING;
        }
        else {
            println("Failed to find a bank booth");
        }
        return random.nextLong(500,1000);
    }


    private static int checkCookingLevel(){
        // Your logic to check the cooking level
        Skill cookingSkill = Skills.COOKING.getSkill();
        return cookingSkill.getActualLevel();
    }
}
