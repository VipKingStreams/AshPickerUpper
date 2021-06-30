package main;

// Imports

import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.GroundItem;

@ScriptManifest(category = Category.MONEYMAKING, name = "Zack's Ash PickerUpper", description = "it's a Ash PickerUpper", author = "ZackJCreates", version = 1.0)
public class ashes extends AbstractScript {

    int spot;
    int c1 = 0;
    GroundItem itemAshes;
    State state;
    GameObject ash = null;

    Area ashesArea = new Area(3141, 3514, 3197, 3452);
    Tile ashesTile = new Tile(3164, 3481, 0);

    Tile bankTile = new Tile(3167, 3489);
    Area grandExchange = new Area(3157, 3502, 3175, 3478);

    int[] worlds = {302, 303, 304, 305, 306, 307, 309, 310, 311, 312, 313, 314, 315, 317, 318,
            319, 320, 321, 322, 323, 324, 325, 327, 328, 329, 331, 332, 333, 334, 336, 337, 338, 339, 340,
            341, 342, 343, 344, 346, 347, 348, 350, 351, 352, 354, 355, 356, 357, 358, 359, 360,
            362, 365, 367, 368, 369, 370, 374, 375, 376, 377, 378, 386, 387, 388, 389, 390, 395,
            421, 422, 424, 443, 444, 445, 446, 463, 464, 465, 466, 477, 478, 479, 480, 481, 482, 484,
            485, 486, 487, 488, 489, 490, 491, 492, 493, 494, 495, 496, 505, 506, 507, 508, 509, 511, 512,
            513, 514, 515, 516, 517, 518, 519, 520, 521, 522, 523, 524, 525};

    @Override
    public int onLoop() {

        switch (getState()) {
            case STOP:
                log("Stop Script");
                stop();
                break;

            case PICKUPASHES:
                itemAshes = getGroundItems().closest("Ashes");
                if (itemAshes != null) {
                    itemAshes.interact("Take");
                    c1++;
                }
                break;
            case RUNNINGTOAREA:
                log("running to area");
                if (!ashesArea.contains(getLocalPlayer())) {
                    if (!getLocalPlayer().isMoving()) {
                        getWalking().walk(ashesTile);
                        sleepUntil(() -> !getLocalPlayer().isMoving(), 5000);
                    }
                }
                break;

            case GOINGTOBANK:
                log("going to bank");
                if (!grandExchange.contains(getLocalPlayer())) {
                    if (!getLocalPlayer().isMoving()) {
                        Walking.walk(bankTile);
                        sleepUntil(() -> !getLocalPlayer().isMoving(), 5000);
                    }
                }
                break;

            case BANK:
                log("Bank");
                NPC booth = getNpcs().closest(c -> c != null && c.getName().equals("Banker"));
                booth.interact("Bank");
                sleep(randomNum(1500, 2000));
                Bank bank;
                bank = getBank();
                if(Bank.isOpen()) {
                    Bank.depositAllItems();
                    sleepUntil(() -> getInventory().isEmpty(), 3000);
                    Bank.close();
                    sleepUntil(() -> !Bank.isOpen(), 3000);
                }
                break;
        }
        return 0;
    }
    private enum State{
        STOP, PICKUPASHES, RUNNINGTOAREA, GOINGTOBANK, BANK
    }
    private State getState() {
        if(!getClient().isLoggedIn()) {
            state = State.STOP;
        }else if(getInventory().isFull() && !grandExchange.contains(getLocalPlayer())) {
            state = State.GOINGTOBANK;
        }else if(getInventory().isFull() && grandExchange.contains(getLocalPlayer())) {
            state = State.BANK;
        }else if(itemAshes == null && ashesArea.contains(getLocalPlayer())) {
            state = State.PICKUPASHES;
        } else if(!ashesArea.contains(getLocalPlayer()) && !getInventory().isFull()) {
            state = State.RUNNINGTOAREA;
        }
        return state;
    }
    public void onStart() {
        log("Bot Started");
        for(int i = 0; i < worlds.length; i++) {
            if(worlds[i] == getClient().getCurrentWorld()) {
                spot = i + 1;
            }
        }
    }
    public void onExit() {
        log("Bot Ended");
    }
    public void hop() {
        if(getClient().getCurrentWorld() == worlds[worlds.length - 1]) {
            spot = 0;
            getWorldHopper().hopWorld(worlds[spot]);
        }
        else {
            getWorldHopper().hopWorld(worlds[spot]);
            spot++;
        }

    }

    public int randomNum(int i, int k) {
        int num = (int) (Math.random() * (k - i + 1)) + i;
        return num;
    }

}