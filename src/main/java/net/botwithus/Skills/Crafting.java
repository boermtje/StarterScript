package net.botwithus.Skills;

public class Crafting {
}

//public class SkeletonScript extends LoopingScript {
//    private BotState botState = BotState.IDLE;
//    private Random random = new Random();
//    public int gemsGained = 0;
//    private boolean isSkilling = false;
//    private final Pattern uncutPattern = Regex.getPatternForContainingOneOf("Uncut ");
//
//    /////////////////////////////////////Botstate//////////////////////////
//    enum BotState {
//        //define your own states here
//        IDLE,
//        SKILLING,
//        BANKING,
//        TRAVERSEROCK,
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
//                // Check if the new item matches the "Uncut" pattern and is in inventory type 93
//                if (uncutPattern.matcher(item.getName()).find()) {
//                    gemsGained++;
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onLoop() {
//        //Loops every 100ms by default, to change:
//        //this.loopDelay = 500;
//        LocalPlayer player = Client.getLocalPlayer();
//        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
//            //wait some time so we dont immediately start on login.
//            Execution.delay(random.nextLong(3000, 7000));
//            return;
//        }
//
//        /////////////////////////////////////Botstate//////////////////////////
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
//            case TRAVERSEROCK -> {
//                Execution.delay(Traverse());
//            }
//        }
//    }
//
//    private long Traverse() {
//        Area.Singular area = new Area.Singular(new Coordinate(2825, 2997, 0));
//        if (Bank.isOpen()) { Bank.close(); }
//        if (Movement.traverse(NavPath.resolve(area)) == TraverseEvent.State.FINISHED) {
//            botState = BotState.SKILLING;
//        } else {
//            println("Failed to traverse to gems");
//        }
//        return random.nextLong(1500,3000);
//    }
//
//    public boolean headbar(LocalPlayer player){
//        if (player.getHeadbars().size() != 5 && player.getHeadbars().get(5).getWidth() <180) {
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    private long handleSkilling(LocalPlayer player) {
//        SceneObject gemRock = SceneObjectQuery.newQuery().name("Precious gem rock").option("Mine").results().nearest();
//        Area.Rectangular area = new Area.Rectangular(new Coordinate(2827, 2296, 0), new Coordinate(2822, 3002, 0));
//        //if our inventory is full, lets bank.
//        if (Backpack.isFull()) {
//            println("Inventory is full.");
//            isSkilling = false;  // Stop skilling
//            botState = BotState.BANKING;
//            return random.nextLong(250, 1500);
//        }
//        if (Bank.isOpen()) { Bank.close(); }
//        if (area.contains(player) == false) {botState = BotState.TRAVERSEROCK;
//        }
//        else if (gemRock != null && headbar(player) == true){
//            isSkilling = true;  // Start skilling
//            print(isSkilling);
//            println("In area, mining");
//            Execution.delay(random.nextLong(500, 2000));
//            println("Interacted rock: " + gemRock.interact("Mine"));
//        }
//        return random.nextLong(1000,2000);
//    }
//
//    private long Banking() {
//        //go to area
//        Area.Rectangular areas = new Area.Rectangular(new Coordinate(2848, 2958, 0), new Coordinate(2855, 2953, 0));
//        if (Movement.traverse(NavPath.resolve(areas)) == TraverseEvent.State.FINISHED) {
//            isSkilling = false;
//            if (Bank.isOpen()) {
//                println("Bank is open");
//                Execution.delay(random.nextLong(500, 2000));
//                Bank.depositAllExcept(54004);
//                Bank.close();
//                botState = BotState.SKILLING;
//            }
//            else {
//                ResultSet<Npc> banks = NpcQuery.newQuery().name("Banker").inside(areas).results();
//                if (banks.isEmpty()) {
//                    println("Bank query was empty.");
//                } else {
//                    Npc bank = banks.random();
//                    if (bank != null) {
//                        println("Yay, we found our bank.");
//                        println("Interacted bank: " + bank.interact("Bank"));
//                        Execution.delay(random.nextLong(500, 2000));
//                        Bank.depositAllExcept(54004);
//                        Bank.close();
//                        botState = BotState.SKILLING;
//                    }
//                }
//            }
//        }
//        return random.nextLong(1500,3000);
//    }
//
//    private long startTime;
//    public int gemsPerHour;
//    public boolean initialize() {
//        startTime = System.currentTimeMillis();
//        return super.initialize();
//    }
//
//    public int gemsPerHour() {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime > startTime) {
//            gemsPerHour = (int) (gemsGained * 3600000.0 / (currentTime - startTime));
//        }
//        return gemsPerHour;
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
