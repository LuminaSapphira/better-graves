package bettergraves.api;

import bettergraves.BetterGraves;

public class BetterGravesAPI {

    public void registerDeathHandler(String key, DeathHandler handler, RestoreHandler restoreHandler) {
        BetterGraves.deathHandlers.put(key, handler);
        BetterGraves.restoreHandlers.put(key, restoreHandler);
    }

}
