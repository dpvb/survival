package dev.dpvb.survival.chests;

public class ChestManager {

    private static ChestManager instance;

    private ChestManager() {

    }

    public static ChestManager getInstance() {
        if (instance == null) {
            instance = new ChestManager();
        }

        return instance;
    }

}
